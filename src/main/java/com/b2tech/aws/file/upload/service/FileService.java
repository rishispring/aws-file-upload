package com.b2tech.aws.file.upload.service;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.util.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class FileService implements FileServiceInterface{

    @Value("${s3.bucket.name}")
    private String bucketName;  // fetched from application.properties file

    @Autowired
    private AmazonS3 amazonS3;

    @Override
    public Map<String, String> save(MultipartFile multipartFile){

        // Task is to put the file in bucket
        // so how do you get file from multipartFile?
        // https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/multipart/MultipartFile.html

        // create unique key for file
        String fileName = multipartFile.getOriginalFilename();
        String objectKey = UUID.nameUUIDFromBytes(fileName.getBytes()).toString();

        Map<String, String> fileMetaData = new HashMap<>();
        fileMetaData.put("originalFileName", fileName);
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setUserMetadata(fileMetaData);
        metadata.setContentType(multipartFile.getContentType());
        metadata.setContentLength(multipartFile.getSize());

        // now get file from the multipart file and upload it to s3 bucket
        PutObjectResult result;
        try{
            result = amazonS3.putObject(bucketName, objectKey, multipartFile.getInputStream(), metadata);
        } catch(IOException e){
            System.out.println("Some error occurred during putObject");
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error occurred while uploading file");
        }

        String fileMetadataAsString = fileMetaData.keySet().stream()
                        .map(key -> key + "=" + fileMetaData.get(key))
                        .collect(Collectors.joining(", ", "{", "}"));

//        amazonS3.setObjectAcl(bucketName, objectKey, CannedAccessControlList.PublicRead);
//        String resourceUrl = amazonS3.getUrl(bucketName, objectKey).toString();

        Map<String, String> response = new HashMap<>();
//        response.put("ResourceUrl", resourceUrl);
        response.put("E-Tag info", result.getETag());
        response.put("Metadata", fileMetadataAsString);
        return response;
    }

    public void listFiles() {
        System.out.format("Bucket name is : %s", this.bucketName);
        System.out.println("Listing objects, in file service");
        ListObjectsV2Result totalObjectsResult = amazonS3.listObjectsV2(bucketName);
        List<S3ObjectSummary> objects = totalObjectsResult.getObjectSummaries();
        for(S3ObjectSummary os : objects){
            System.out.println("* " + os.getKey());
        }
    }

    @Override
    public Map<String, String> deleteFile(String fileName){
        // get objectkey of the file
        String objectKey = UUID.nameUUIDFromBytes(fileName.getBytes()).toString();
        Map<String, String> response = new HashMap<>();
        try {
            amazonS3.deleteObject(this.bucketName, objectKey);
            response.put("Status", "ok");
            response.put("Message", "Object in aws s3 with given file-name deleted");
        }
        catch(SdkClientException e){
            response.put("Status", "error");
            response.put("Message", e.getMessage());
        }
        return response;
    }

//    @Override
//    public byte[] downloadFile(String fileName){
//        String objectKey = UUID.nameUUIDFromBytes(fileName.getBytes()).toString();
//        S3Object object = amazonS3.getObject(bucketName, objectKey);
//        S3ObjectInputStream objectContent = object.getObjectContent();
//        try{
//            return IOUtils.toByteArray(objectContent);
//        } catch (IOException e){
//            throw new RuntimeException(e);
//        }
//    }

}