package com.holmes.aws.vocabulary;

import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;

public class AwsS3Vocabulary {

    private final S3Client s3Client;
    private final String bucketName;

    public AwsS3Vocabulary(S3Client s3Client, String bucketName)  {
        this.s3Client = s3Client;
        this.bucketName = bucketName;
    }

    public PutObjectResponse putFile(String fileKey, byte[] objectToPut) {
        return s3Client.putObject(PutObjectRequest.builder().bucket(bucketName).key(fileKey).build(),
                RequestBody.fromBytes(objectToPut));
    }

    public File getFileByKey(String fileKey) {
        ResponseBytes<GetObjectResponse> getFileResponse = s3Client.getObjectAsBytes(
                GetObjectRequest.builder()
                .bucket(bucketName)
                .key(fileKey)
                .build());

        byte[] bb = getFileResponse.asByteArray();
        File file = new File(fileKey);

        try {
            OutputStream outputStream = Files.newOutputStream(file.toPath());
            outputStream.write(bb);
        }
        catch (IOException ex) {
        }

        return file;
    }
}
