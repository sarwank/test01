package com.cdms.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// FileUpload
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FileUpload {
	// Attributes of FileUpload
	private String fileName;
	private Long fileSize;
	private String filePath;
	public String lastModified;
	private byte[] file;


	// Constructor
	public FileUpload(String fileName, Long fileSize, String filePath, String lastModified) {
		super();
		this.fileName = fileName;
		this.fileSize = fileSize;
		this.filePath = filePath;
		this.lastModified = lastModified;
	}
}
