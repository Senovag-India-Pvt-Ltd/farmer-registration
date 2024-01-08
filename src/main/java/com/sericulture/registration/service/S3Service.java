package com.sericulture.registration.service;//package com.sericulture.registration.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import java.io.File;

@Service
@Slf4j
public class S3Service {

    @Value("${aws.accessKeyId}")
    private String accessKey;

    @Value("${aws.secretKey}")
    private String secretKey;

    @Value("${aws.region}")
    private String region;

    S3Client s3Client;

    public S3Service() {
//        this.s3Client = S3Client.builder()
//                .region(Region.of(region))
//                .credentialsProvider(() -> AwsBasicCredentials.create(accessKey, secretKey))
//                .build();
    }

    public void uploadFile(String bucketName, String key, File file) throws Exception {
        try {
            this.s3Client = S3Client.builder()
                    .region(Region.of(region))
                    .credentialsProvider(() -> AwsBasicCredentials.create(accessKey, secretKey))
                    .build();
            s3Client.putObject(PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build(), file.toPath());
        }catch (Exception ex){
            ex.printStackTrace();
            System.out.println("Error");
        }
    }
}

