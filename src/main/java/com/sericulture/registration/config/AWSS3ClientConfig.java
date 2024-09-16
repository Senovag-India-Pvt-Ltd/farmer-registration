package com.sericulture.registration.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Slf4j
public class AWSS3ClientConfig {

    @Value("${aws.accessKeyId}")
    private String accessKey;

    @Value("${aws.secretKey}")
    private String secretKey;

    @Value("${aws.region}")
    private String region;

    @Value("${aws.host}")
    private String host;

    @Bean
    @Profile("dev")
    public AmazonS3 devs3client() {
        log.info("Inside devs3client");
        BasicAWSCredentials awsCredentials = new BasicAWSCredentials(accessKey, secretKey);
        return AmazonS3ClientBuilder.standard()
                .withRegion(Regions.fromName(region))
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .build();
    }

    @Bean
    @Profile("prod")
            public AmazonS3 prods3client() {
                log.info("Inside prods3client");
                AwsClientBuilder.EndpointConfiguration  endpointConfiguration = new AwsClientBuilder.EndpointConfiguration(host,"");
//                String[] parts = host.split("\\.");
                BasicAWSCredentials awsCredentials = new BasicAWSCredentials(accessKey, secretKey);
                return AmazonS3ClientBuilder.standard()
//                       .withRegion(Regions.fromName(parts[1]))
                        .withRegion(Regions.fromName(region))
                        .withEndpointConfiguration(endpointConfiguration)
                        .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                        .build();
        }
}