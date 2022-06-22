package com.holmes.aws.vocabulary;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


@TestMethodOrder(MethodOrderer.MethodName.class)
class AwsS3VocabularyTest {

    private static S3Client s3Client;
    private static AwsS3Vocabulary awsS3Vocabulary;

    private static final String BUCKET_NAME = "caixiu-dev";
    private static final String FILE_NAME = "vocabulary.csv";
    private static URL fileUrl;


    @BeforeAll
    static void setup() throws IOException {
        s3Client = S3Client.builder().region(Region.EU_WEST_2).build();
        awsS3Vocabulary = new AwsS3Vocabulary(s3Client, BUCKET_NAME);

        fileUrl = new URL("https://raw.githubusercontent.com/Yurizaki/caixiu-resources/main/vocabulary.csv");
    }

    @AfterAll
    static void tearDown() {
        s3Client.close();
    }

    @Test
    public void putFile_pass_if_file_added() throws IOException {
        BufferedInputStream in = new BufferedInputStream(fileUrl.openStream());
        ReadableByteChannel readableByteChannel = Channels.newChannel(fileUrl.openStream());
        FileOutputStream fileOutputStream = new FileOutputStream("tempFile.csv");
        fileOutputStream.getChannel().transferFrom(readableByteChannel, 0, Long.MAX_VALUE);

        awsS3Vocabulary.putFile(FILE_NAME, getObjectFile("tempFile.csv"));
    }

    private static byte[] getObjectFile(String filePath) {
        FileInputStream fileInputStream = null;
        byte[] bytesArray = null;

        try {
            File file = new File(filePath);
            bytesArray = new byte[(int) file.length()];
            fileInputStream = new FileInputStream(file);
            fileInputStream.read(bytesArray);

        }
        catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return bytesArray;
    }


    @Test
    public void getFileFromS3Bucket_pass_if_file_retrieved() throws FileNotFoundException {
        File myVocabFile = awsS3Vocabulary.getFileByKey(FILE_NAME);

        assertNotNull(myVocabFile);
        assertEquals(FILE_NAME, myVocabFile.getName());

        List<Vocabulary> vocabularyList = new ArrayList<>();
        Scanner scanner = new Scanner(myVocabFile);
        while(scanner.hasNext()) {
            String line = scanner.nextLine();
            System.out.println(line);

            String[] splitter = line.split(",");
            vocabularyList.add(new Vocabulary(splitter[0], splitter[1]));
        }

        assertTrue(vocabularyList.size() > 0);
    }
}