package com.holmes.aws.vocabulary;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

public class AwsS3Vocabulary {

    private final S3Client s3Client;

    public AwsS3Vocabulary(S3Client s3Client) throws IOException {
        this.s3Client = s3Client;


        URL url = new URL("https://raw.githubusercontent.com/Yurizaki/caixiu-resources/main/vocabulary.csv");
        BufferedInputStream in = new BufferedInputStream(url.openStream());

        ReadableByteChannel readableByteChannel = Channels.newChannel(url.openStream());
        FileOutputStream fileOutputStream = new FileOutputStream("tempFile.csv");
        fileOutputStream.getChannel().transferFrom(readableByteChannel, 0, Long.MAX_VALUE);


        s3Client.putObject(PutObjectRequest.builder().bucket("caixiu-dev").key("vocabulary.csv").build(),
                RequestBody.fromBytes(getObjectFile("tempFile.csv")));
    }

    private static byte[] getObjectFile(String filePath) {

        FileInputStream fileInputStream = null;
        byte[] bytesArray = null;

        try {
            File file = new File(filePath);
            bytesArray = new byte[(int) file.length()];
            fileInputStream = new FileInputStream(file);
            fileInputStream.read(bytesArray);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return bytesArray;
    }
}
