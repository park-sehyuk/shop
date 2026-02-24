package com.jpa.market.config.aws;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class S3Config {

    @Value("${spring.cloud.aws.credentials.access-key}")
    private String accessKey;

    @Value("${spring.cloud.aws.credentials.secret-key}")
    private String secretKsy;

    @Value("${spring.cloud.aws.region.static}")
    private String region;

    // 스프링부트가 AWS 계정의 S3에 버킷에 젒속할 수 있도록 설정
    @Bean
    public S3Client s3Client(){
        // 클라이언트 객체 생성
        return S3Client.builder()
                .region(Region.of(region))  // 리전 설정
                .credentialsProvider(       // 인증 절차
                        StaticCredentialsProvider.create(   // IAM 사용자의 키를 이용
                                AwsBasicCredentials.create(accessKey, secretKsy)
                        )
                )
                .build();
    }
}
