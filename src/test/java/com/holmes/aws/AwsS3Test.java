package com.holmes.aws;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

public class AwsS3Test {

    private S3Client s3Client;

    public void setup() {
        s3Client = S3Client.builder()
                .region(Region.EU_WEST_2)
                .build();
    }

    public void breakDown() {
        s3Client.close();
    }

    public void tests() {

    }
}
