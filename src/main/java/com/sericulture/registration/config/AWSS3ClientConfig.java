
package com.sericulture.registration.config;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
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

    @Value("${aws.region}")
    private String region;

    @Value("${aws.host:}")
    private String host;

    @Bean
    @Profile("dev")
    public AmazonS3 devs3client() {
        log.info("Initializing AWS S3 client for dev profile");

        return AmazonS3ClientBuilder.standard()
                .withRegion(Regions.fromName(region))
                .withCredentials(
                        DefaultAWSCredentialsProviderChain.getInstance()
                )
                .build();
    }

    @Bean
    @Profile("prod")
    public AmazonS3 prods3client() {
        log.info("Initializing AWS S3 client for prod profile");

        AwsClientBuilder.EndpointConfiguration endpointConfiguration =
                new AwsClientBuilder.EndpointConfiguration(host, region);

        return AmazonS3ClientBuilder.standard()
                .withEndpointConfiguration(endpointConfiguration)
                .withCredentials(
                        DefaultAWSCredentialsProviderChain.getInstance()
                )
                .build();
    }
}
