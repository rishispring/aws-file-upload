package com.b2tech.aws.file.upload.config;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;



@Configuration
public class AwsS3Config {

    // Please do not use this, it is deprecated!!!
    // AmazonS3Client is a class
//    @Bean
//    public AmazonS3Client configureAmazonS3Client(){
//
//    }

    @Bean
    public AmazonS3 s3client() {
        AWSCredentials awscredentials = new BasicAWSCredentials(
                <your-access-key>,
                <your-secret-key>);

        return AmazonS3ClientBuilder
                .standard()
                .withRegion(<your-region-name>)
                .withCredentials(new AWSStaticCredentialsProvider(awscredentials))
                .build();
    }

}
