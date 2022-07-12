package com.b2tech.aws.file.upload.config;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;



@Configuration
public class AwsS3Config {

    @Value("$(s3.region.name)")
    private String regionName;

    @Value("$(s3.bucket.name)")
    private String bucketName;

    @Value("$(access.key.id)")
    private String accessKeyId; // fetched from application.properties file

    @Value("$(access.key.secret)")
    private String accessKeySecret; // fetched from application.properties file

    @Bean
    public AmazonS3 s3client() {
        AWSCredentials awscredentials = new BasicAWSCredentials(accessKeyId, accessKeySecret);

        return AmazonS3ClientBuilder.standard().withRegion(regionName)
                .withCredentials(new AWSStaticCredentialsProvider(awscredentials)).build();
    }
}
