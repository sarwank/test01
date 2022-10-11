package com.cdms.service;

import com.cdms.dao.UserRepository;
import com.cdms.dto.EmailDetails;
import com.cdms.dto.FileUpload;
import com.cdms.model.User;
import io.minio.*;
import io.minio.errors.*;
import io.minio.messages.Item;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

// Implementation of FileHandler Service
@Service
public class FileUploadServiceImpl implements FileUploadService {

    private static final Logger log = LoggerFactory.getLogger(FileUploadServiceImpl.class);

    @Autowired
    private MinioClient minioClient;

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserRepository userRepository;

    @Override
    public String fileUplaod(String bucketName, MultipartFile file, String userId) {
        log.info("FileHandler : in FileUpload");
        String fileName = "";
        try {
            if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build())) {
                log.info("FileHandler : bucket not exists");
                return "Bucket Not Exist";
            }
            log.info("FileHandler : creating fileName for adding userId");
            fileName = userId + "/" + file.getOriginalFilename();
            log.info("FileHandler : objectMetadata creating");

            // Create a InputStream for object upload.
            ByteArrayInputStream bais = new ByteArrayInputStream(file.getBytes());

            // Create object 'my-objectname' in 'my-bucketname' with content from the input stream.
            minioClient.putObject(
                    PutObjectArgs.builder().bucket(bucketName).object(fileName).stream(
                                    bais, bais.available(), -1)
                            .build());
            User user = userRepository.findByUserId(userId);
            if (user.isNotificationEnabled()) {
                EmailDetails emailDetails = new EmailDetails();
                String body = "";
                body += "Hi,\n";
                body += "File is uploaded successfully\n";
                body += "File Name : ";
                body += fileName.split(userId+"/")[1];
                body += "\n";
                body += "File Link : ";
                String url = "http://local.cdmswebapp.live:31000/s3/file/download/" + fileName.split(userId+"/")[1];
                body += url;
                body += "\n\n";
                body += "Thanks";
                emailDetails.setMsgBody(body);
                emailDetails.setSubject("Document Uploaded - Notification");
                emailDetails.setRecipient(user.getEmail());
                emailService.sendSimpleMail(emailDetails);
            }
            bais.close();
            log.info("FileHandler : File Uploaded");

        } catch ( IOException e) {
            log.info("FileHandler : Exception occur while uploading");
            return "Exception";
        } catch (ServerException e) {
            e.printStackTrace();
        } catch (InsufficientDataException e) {
            e.printStackTrace();
        } catch (ErrorResponseException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (InvalidResponseException e) {
            e.printStackTrace();
        } catch (XmlParserException e) {
            e.printStackTrace();
        } catch (InternalException e) {
            e.printStackTrace();
        }
        return "File Uploaded Successfully \nFileName:- " + fileName;
    }

    @Override
    public List<FileUpload> getBucketfiles(String bucketName, String userId) {
        log.info("FileHandler : in getBucketFiles");
        List<FileUpload> fileUploadList = new ArrayList<>();
        try {
            if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build())) {
                log.info("FileHandler : bucket Not found");
                log.error("No Bucket Found");
                return null;
            }
            log.info("FileHandler : retrieving files");
            Iterable<Result<Item>> results =
                    minioClient.listObjects(ListObjectsArgs.builder().bucket(bucketName).recursive(true).build());

            for (Result<Item> result : results) {
                Item item = result.get();
                System.out.println(item.objectName());
                if (item.objectName().startsWith(userId)) {
                    FileUpload fileUpload = new FileUpload();
                    fileUpload.setFileName(item.objectName().split(userId+"/")[1]);
                    fileUpload.setLastModified(item.lastModified().toString());
                    fileUpload.setFileSize(item.size());
                    fileUpload.setFilePath(item.objectName());
                    fileUploadList.add(fileUpload);
                }

            }
            return fileUploadList;
        } catch (ErrorResponseException e) {
            e.printStackTrace();
        } catch (InsufficientDataException e) {
            e.printStackTrace();
        } catch (InternalException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (InvalidResponseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (ServerException e) {
            e.printStackTrace();
        } catch (XmlParserException e) {
            e.printStackTrace();
        }
        return fileUploadList;

    }

    @Override
    public FileUpload downloadFile(String bucketName, String fileName, String userId) {
        log.info("FileHandler : in download File");
        FileUpload fileUpload = new FileUpload();
        try {
            if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build())) {
                log.info("FileHandler : bucket Not found");
                log.error("No Bucket Found");
                return null;
            }
            log.info("FileHandler : downloading file");
            InputStream inputStream = minioClient.getObject(GetObjectArgs.builder().bucket(bucketName).object(userId+"/"+fileName).build());
            fileUpload.setFile(FileCopyUtils.copyToByteArray(inputStream));
            fileUpload.setFileName(fileName);
        } catch (ErrorResponseException e) {
            e.printStackTrace();
        } catch (InsufficientDataException e) {
            e.printStackTrace();
        } catch (InternalException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (InvalidResponseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (ServerException e) {
            e.printStackTrace();
        } catch (XmlParserException e) {
            e.printStackTrace();
        }
        return fileUpload;
    }

    @Override
    public String deleteFile(String bucketName, String fileName, String userId) {
        log.info("FileHandler : in deleteFile");
        try {
            if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build())) {
                log.info("FileHandler : bucket Not found");
                log.error("No Bucket Found");
                return null;
            }
            log.info("FileHandler : deleting file");
            minioClient.removeObject(
                    RemoveObjectArgs.builder().bucket(bucketName).object(userId+"/"+fileName).build());
            User user = userRepository.findByUserId(userId);
            if (user.isNotificationEnabled()) {
                EmailDetails emailDetails = new EmailDetails();
                String body = "";
                body += "Hi,\n";
                body += "File is deleted successfully\n";
                body += "File Name : ";
                body += fileName;
                body += "\n\n";
                body += "Thanks";
                emailDetails.setMsgBody(body);
                emailDetails.setSubject("Document Deleted - Notification");
                emailDetails.setRecipient(user.getEmail());
                emailService.sendSimpleMail(emailDetails);
            }
            log.info("FileHandler : file deleted Successfully");
        } catch (ErrorResponseException e) {
            e.printStackTrace();
        } catch (InsufficientDataException e) {
            e.printStackTrace();
        } catch (InternalException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (InvalidResponseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (ServerException e) {
            e.printStackTrace();
        } catch (XmlParserException e) {
            e.printStackTrace();
        }
        return "File Deleted Successfully";
    }

}
