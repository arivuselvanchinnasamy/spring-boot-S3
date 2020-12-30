package com.arivu.aws.s3.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;

public interface IStorageService {

	List<S3ObjectSummary> getAllFiles() throws IOException;

	PutObjectResult upload(MultipartFile file, boolean allowPublicAccess) throws FileNotFoundException;

	S3Object downloadFile(String fileName);	
}
