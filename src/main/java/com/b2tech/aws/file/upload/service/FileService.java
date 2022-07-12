package com.b2tech.aws.file.upload.service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class FileService implements FileServiceInterface{

    @Value("$(s3.bucket.name)")
    private String bucketName;  // fetched from application.properties file

    @Autowired
    private AmazonS3Client awsS3Client;

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

        // now get file from the multipart file and upload it to s3 bucket
        PutObjectResult result;
        try{
            result = awsS3Client.putObject(this.bucketName, objectKey, multipartFile.getInputStream(), metadata);
        } catch(IOException e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error occurred while uploading file");
        }

        String fileMetadataAsString = fileMetaData.keySet().stream()
                        .map(key -> key + "=" + fileMetaData.get(key))
                        .collect(Collectors.joining(", ", "{", "}"));

        awsS3Client.setObjectAcl(this.bucketName, objectKey, CannedAccessControlList.PublicRead);
        String resourceUrl = awsS3Client.getResourceUrl(this.bucketName, objectKey);

        Map<String, String> response = new HashMap<>();
        response.put("ResourceUrl", resourceUrl);
        response.put("E-Tag info", result.getETag());
        response.put("Metadata", fileMetadataAsString);
        return response;
    }

    @Override
    public Map<String, String> deleteFile(String fileName){
        // get objectkey of the file
        String objectKey = UUID.nameUUIDFromBytes(fileName.getBytes()).toString();

        Map<String, String> response = new HashMap<>();
        try {
            awsS3Client.deleteObject(this.bucketName, fileName);
            response.put("Status", "ok");
            response.put("Message", "Object in aws s3 with given file-name deleted");
        }
        catch(SdkClientException e){
            response.put("Status", "error");
            response.put("Message", e.getMessage());
        }
        return response;
    }

}

//        System.out.printf("getName :  %s\n", multipartFile.getName());
//        System.out.printf("getOriginalFileName :  %s\n", multipartFile.getOriginalFilename());
//        System.out.printf("getSize :  %d\n", multipartFile.getSize());
//        System.out.printf("cleanFileName :  %s\n", StringUtils.cleanPath(multipartFile.getOriginalFilename()));
//
//        try(Reader reader = new InputStreamReader(multipartFile.getResource().getInputStream(), UTF_8)){
//            System.out.println("Trying to read file\n\n\n");
//            System.out.println(FileCopyUtils.copyToString(reader));
//        } catch (IOException e){
//            throw new UncheckedIOException(e);
//        }