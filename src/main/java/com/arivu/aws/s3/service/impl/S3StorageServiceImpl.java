package com.arivu.aws.s3.service.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.arivu.aws.s3.service.IStorageService;

@Service
public class S3StorageServiceImpl implements IStorageService {

	private static final Logger logger = LoggerFactory.getLogger(S3StorageServiceImpl.class);
	
	@Value("${cloud.aws.s3.bucket}")
	private String bucket;

	@Autowired
	private AmazonS3 amazonS3Client;
	
	@Override
	public List<S3ObjectSummary> getAllFiles() {
		ObjectListing objectListing = amazonS3Client.listObjects(new ListObjectsRequest().withBucketName(bucket));
		List<S3ObjectSummary> s3ObjectSummaries = objectListing.getObjectSummaries();
		return s3ObjectSummaries;
	}

	@Override
	public PutObjectResult upload(final MultipartFile multipartFile, final boolean allowPublicAccess) throws FileNotFoundException {
		String fileName = multipartFile.getOriginalFilename();
		PutObjectResult putObjectResult = null;
		try {
			File file = convertMultiPartToFile(multipartFile);
			logger.info("File:  name: {}", file.getName());
			final PutObjectRequest putObjectRequest = new PutObjectRequest(bucket, fileName, file);
			if (allowPublicAccess) {
				putObjectRequest.setCannedAcl(CannedAccessControlList.PublicRead);
			}
			putObjectResult = amazonS3Client.putObject(putObjectRequest);
			file.delete();
		} catch (IOException e) {

		}
		return putObjectResult;
	}
	@Override
	public S3Object downloadFile(final String fileName) {
		return amazonS3Client.getObject(bucket, fileName);
	}
    private File convertMultiPartToFile(MultipartFile file) throws IOException {
        File convFile = new File(file.getOriginalFilename());
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(file.getBytes());
        fos.close();
        return convFile;
    }
}
