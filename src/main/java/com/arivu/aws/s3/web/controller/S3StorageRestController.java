package com.arivu.aws.s3.web.controller;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.arivu.aws.s3.exception.S3StorageOperationException;
import com.arivu.aws.s3.model.FileUploadResponse;
import com.arivu.aws.s3.service.IStorageService;

@RestController
@RequestMapping("api/v1/files")
public class S3StorageRestController {
	private static final Logger logger = LoggerFactory.getLogger(S3StorageRestController.class);
	
    @Autowired
    IStorageService storageService;

    @GetMapping()
    public ResponseEntity<List<S3ObjectSummary>> getFilesList() throws IOException{
    	List<S3ObjectSummary> files = storageService.getAllFiles();    	
        return ResponseEntity.ok(files);
    }
    
	@PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE })
	public ResponseEntity<FileUploadResponse> uploadFile(@RequestParam(value = "file", required = true) MultipartFile file) throws Exception {
		FileUploadResponse  fileUploadResponse = new FileUploadResponse();
		fileUploadResponse.setFileName(file.getName());
		if (!file.isEmpty()) {
			PutObjectResult putObjectResult = storageService.upload(file, false);
			if(putObjectResult != null) {
				fileUploadResponse.setMessage("File has been uploaded successfully");
			}
		} else {
			fileUploadResponse.setMessage("File is emppty.");
			return ResponseEntity.badRequest().body(fileUploadResponse);
		}
		return ResponseEntity.ok(fileUploadResponse);
	}

	@GetMapping(value = "/download")
	public ResponseEntity<InputStreamResource> downloadFile(@RequestParam("fileName") String fileName) throws Exception {
		logger.info("fileName {}", fileName);
		S3Object s3Object = storageService.downloadFile(fileName);
		String contentType = s3Object.getObjectMetadata().getContentType();
		if(!StringUtils.isEmpty(contentType) && contentType.equals("application/x-directory")) {
			String errorMsg = String.format("Requested file with name %s is an directory", fileName);
			throw new S3StorageOperationException(errorMsg);
		}
		S3ObjectInputStream inputStream = s3Object.getObjectContent();
		int contentLength  = Long.valueOf(s3Object.getObjectMetadata().getContentLength()).intValue();
		return ResponseEntity
	                .ok()
	                .contentLength(contentLength)
	                .header("Content-type",MediaType.parseMediaType(contentType).toString())
	                .header("Content-disposition", String.format("attachment; filename=%s",fileName))
	                .body(new InputStreamResource(inputStream));
	}
}