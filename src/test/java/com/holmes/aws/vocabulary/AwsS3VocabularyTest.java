package com.holmes.aws.vocabulary;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.waiters.DynamoDbWaiter;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3ClientBuilder;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.MethodName.class)
class AwsS3VocabularyTest {

    private static S3Client s3Client;
    private static AwsS3Vocabulary awsS3Vocabulary;

    @BeforeAll
    static void setup() throws IOException {
        s3Client = S3Client.builder().region(Region.EU_WEST_2).build();
        awsS3Vocabulary = new AwsS3Vocabulary(s3Client);
    }

    @AfterAll
    static void tearDown() {
        s3Client.close();
    }


    @Test
    public void testy() {

    }
}