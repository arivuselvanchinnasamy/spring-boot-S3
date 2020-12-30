# spring-boot-S3

  In this demo application, developed REST APIs to perform operations on S3:
  - Listing of contents in Buckets
  - Uploading a object (any type of file) to S3 Bucket 
  - Download an object from an S3 Bucket.

# Technology Stack

  - Java 8
  - Spring Boot:- 2.3.7.RELEASE
  - Spring Cloud :- Hoxton.SR8
  - Maven

# What is S3?

   S3 is nothing but its Amazon’s Simple Storage Service that provides object storage for users to store and retrieve any amount of data over the internet. Its fully managed by Amazon which means that the underlying infrastructure provisioned and managed by Amazon and also provides 99.999999999% durability and 99.99% availability of objects. It allows us to store objects of size range from 0 to terabytes.

# What is Spring Cloud AWS?

  Spring Cloud AWS part of Spring Cloud umbrella project, eases the integration with hosted AWS services. It also provides dedicated Spring Boot support. Spring Cloud AWS can be configured using Spring Boot properties and will also automatically guess any sensible configuration based on the general setup.
  
  The below properties added in application yaml file for this application run.
  ```java
cloud:
  aws:
    credentials:
      accessKey: <>
      secretKey: <>
    ##The accessKey and secretKey are specific to the user who has S3 access with required ##permission to perform the above said operation.
    region:
      auto: false
      static: us-east-1
    stack:
      auto: false
    s3:
     bucket: <>
     ##Create a bucket in S3 and provide the same over here to perform the operations.
```
  Create a bean for amazonS3Client with configured region.
```java
@Bean
	public AmazonS3 amazonS3Client() {
		return AmazonS3ClientBuilder.standard()
				.withCredentials(new ProfileCredentialsProvider())
				.withRegion(region).build();
	}
```
# Running the project

As the application uses Spring Boot, follow any one of the step to run this project:

- Run the main method from SpringBootAwsS3Application
- Use the Maven Spring Boot Plugin: mvn spring-boot: run

# API endpoints:

- List Bucket contents:
  - URL: http://localhost:8082/api/v1/files/
  - Request Method : GET
  
- Upload a File:
  - URL: http://localhost:8082/api/v1/files/
  - Request Method : POST
  - Content-type : form-data with key "file" and value as file that need to be uploaded 
 
- Download a File: 
  - URL: http://localhost:8082/api/v1/files/download?fileName=PertrolPump.xlsx
  - Request Method : GET