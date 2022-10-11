package com.cdms.service;

import com.cdms.dto.FileUpload;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

// FileHandler Services Interface
public interface FileUploadService {

	// FileUpload Function
	String fileUplaod(String bucketName, MultipartFile file, String userId);

	// GetBucketFiles Function
	List<FileUpload> getBucketfiles(String bucketName, String userId);

	// Delete File Function
	String deleteFile(String bucketName, String fileName, String userId);

	// Download File Function
	FileUpload downloadFile(String bucketName, String fileName, String userId);


}
