package com.jpa.market.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectAclRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.File;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileService {

    private final S3Client s3Client;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${spring.cloud.aws.region.static}")
    private String region;

    public String uploadFile(String folder,
                             String originalFileName,
                             byte[] fileData) throws Exception {
        // git push용
        //uuid를 이용하여 고유한 파일 이름을 생성하기 위해 사용
        UUID uuid = UUID.randomUUID();

        //확장자 추출
        String extension = originalFileName.substring(originalFileName.lastIndexOf("."));

        //uuid와 확장자를 결합하여 저장할 파일명 생성
        String savedFileName = uuid.toString() + extension;

        //파일이 업로드될 전체 경로를 생성
        String key = folder + "/" + savedFileName;

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType("image/jpeg")
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromBytes(fileData));

        return getUploadUrl(key);
    }

    private String getUploadUrl(String key) {

        return String.format("https://%s.s3.%s.amazonaws.com/%s", bucket, region, key);
    }

    //등록된 파일 삭제
    public void deleteFile(String key) throws Exception {

        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();

        s3Client.deleteObject(deleteObjectRequest);
        System.out.println(key + "S3에서 파일 삭제 완료");

    }
}
