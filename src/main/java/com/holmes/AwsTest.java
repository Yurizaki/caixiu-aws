package com.holmes;

//import com.amazonaws.services.s3.AmazonS3;
//import com.amazonaws.services.s3.AmazonS3ClientBuilder;
//import com.amazonaws.services.s3.model.Bucket;

import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.*;
import java.nio.file.Files;
import java.util.List;

public class AwsTest {
    public static void main(String[] args) throws IOException {

        Region region = Region.EU_WEST_2;
        S3Client s3 = S3Client.builder().region(region).build();

        String bucket = "caixiu-dev";
        String key = "chinese_vocabulary.data";

//        tutorialSetup(s3, bucket, region);

        System.out.println("Uploading object...");

//        s3.putObject(PutObjectRequest.builder().bucket(bucket).key(key)
//                        .build(),
//                RequestBody.fromString("Testing with the {sdk-java}"));

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();

        ResponseInputStream<GetObjectResponse> responseInputStream =
                s3.getObject(getObjectRequest);

        GetObjectResponse getObjectResponse = responseInputStream.response();

        ResponseBytes<GetObjectResponse> responseBytes = s3.getObjectAsBytes(getObjectRequest);
        byte[] data = responseBytes.asByteArray();
        File file = new File("alex");
        OutputStream outputStream = Files.newOutputStream(file.toPath());
        outputStream.write(data);
        outputStream.close();

        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        while(bufferedReader.ready()) {
            String line = bufferedReader.readLine();
            System.out.println(line);
        }

        file.deleteOnExit();

        System.out.println("Upload complete");
        System.out.printf("%n");

//        cleanUp(s3, bucket, key);

        System.out.println("Closing the connection to {S3}");
        s3.close();
        System.out.println("Connection closed");
        System.out.println("Exiting...");
    }

    public static void tutorialSetup(S3Client s3Client, String bucketName, Region region) {
        try {
            s3Client.createBucket(CreateBucketRequest
                    .builder()
                    .bucket(bucketName)
                    .createBucketConfiguration(
                            CreateBucketConfiguration.builder()
                                    .locationConstraint(region.id())
                                    .build())
                    .build());
            System.out.println("Creating bucket: " + bucketName);
            s3Client.waiter().waitUntilBucketExists(HeadBucketRequest.builder()
                    .bucket(bucketName)
                    .build());
            System.out.println(bucketName +" is ready.");
            System.out.printf("%n");
        } catch (S3Exception e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }

    public static void cleanUp(S3Client s3Client, String bucketName, String keyName) {
        System.out.println("Cleaning up...");
        try {
            System.out.println("Deleting object: " + keyName);
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder().bucket(bucketName).key(keyName).build();
            s3Client.deleteObject(deleteObjectRequest);
            System.out.println(keyName +" has been deleted.");
            System.out.println("Deleting bucket: " + bucketName);
            DeleteBucketRequest deleteBucketRequest = DeleteBucketRequest.builder().bucket(bucketName).build();
            s3Client.deleteBucket(deleteBucketRequest);
            System.out.println(bucketName +" has been deleted.");
            System.out.printf("%n");
        } catch (S3Exception e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        System.out.println("Cleanup complete");
        System.out.printf("%n");
    }
}

//
////import com.amazonaws.ClientConfiguration;
////import org.springframework.beans.factory.annotation.Autowired;
////import org.springframework.boot.CommandLineRunner;
////import org.springframework.boot.SpringApplication;
////import org.springframework.boot.autoconfigure.SpringBootApplication;
////import org.springframework.context.annotation.Bean;
////import org.springframework.context.annotation.Configuration;
////import org.springframework.core.io.Resource;
////import org.springframework.core.io.ResourceLoader;
////import org.springframework.core.io.WritableResource;
//
//import java.io.IOException;
//import java.io.OutputStream;
//
////@SpringBootApplication
//public class AwsTest implements CommandLineRunner  {
//
//    public static void main(String[] args) {
//        SpringApplication.run(AwsTest.class, args);
//    }
//
//    @Override
//    public void run(String... args) throws Exception {
//        writeResource();
//    }
//
//
////    @Bean(name = "com.amazonaws.ClientConfiguration.AwsTest")
////    ClientConfiguration clientConfiguration() {
////        ClientConfiguration clientConfiguration= new ClientConfiguration();
//
////        clientConfiguration.setProxyHost(proxyHost);
////        clientConfiguration.setProxyPort(proxyPort);
////        clientConfiguration.setProxyUsername(proxyUserName);
////        clientConfiguration.setProxyPassword(proxyPassword);
////
////        return clientConfiguration;
////    }
//
//    @Autowired
//    private ResourceLoader resourceLoader;
//
//    public void writeResource() throws IOException {
//        Resource resource = this.resourceLoader.getResource("s3://myBucket/rootFile.log");
//        WritableResource writableResource = (WritableResource) resource;
//        try (OutputStream outputStream = writableResource.getOutputStream()) {
//            outputStream.write("test".getBytes());
//        }
//    }
//
//}
