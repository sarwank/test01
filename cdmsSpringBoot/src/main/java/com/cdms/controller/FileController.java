package com.cdms.controller;

// Libraries Imports
import com.cdms.dto.FileUpload;
import com.cdms.service.EmailService;
import com.cdms.service.FileUploadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

@RestController
@RequestMapping(value = "/s3")
public class FileController {

    // For logging
    private static final Logger log = LoggerFactory.getLogger(FileController.class);

    // bucket name for storing files
    final String bucketName = "cdms";

    // Services
    @Autowired
    private FileUploadService fileUploadService;

    @Autowired
    private EmailService emailService;

    // Get all files of specific user
    @GetMapping(value = "/bucket/files/")
    public List<FileUpload> getBucketfiles(@CookieValue(value = "user_id", defaultValue = "noname") String userId) {
        log.info("FileUpload Controller : in getBucketFiles");
        return fileUploadService.getBucketfiles(bucketName, userId);
    }

    // Upload File
    @PostMapping(value = "/file/upload/")
    public String fileUpload(@CookieValue(value = "user_id", defaultValue = "noname") String userId, @RequestParam MultipartFile file) {
        log.info("FileUpload Controller : in fileUpload");
        return fileUploadService.fileUplaod(bucketName, file, userId);
    }

    // Delete File
    @DeleteMapping(value = "/file/delete/{fileName}")
    public String deleteFile(@CookieValue(value = "user_id", defaultValue = "noname") String userId, @PathVariable String fileName) {
        log.info("FileUpload Controller : in deleteFile");
        return fileUploadService.deleteFile(bucketName, fileName, userId);
    }

    // DownloadFile
    @GetMapping(value = "/file/download/{fileName}")
    public StreamingResponseBody downloadFile(@CookieValue(value = "user_id", defaultValue = "noname") String userId, @PathVariable String fileName, HttpServletResponse httpResponse) {
        log.info("FileUpload Controller : in downloadFile");
        FileUpload downloadFile = fileUploadService.downloadFile(bucketName, fileName, userId);
        httpResponse.setContentType("application/octet-stream");
        httpResponse.setHeader("Content-Disposition",
                String.format("inline; filename=\"%s\"", downloadFile.getFileName()));
        return outputStream -> {
            outputStream.write(downloadFile.getFile());
            outputStream.flush();
        };
    }

}
