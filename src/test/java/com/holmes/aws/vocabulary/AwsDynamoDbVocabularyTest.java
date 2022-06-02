package com.holmes.aws.vocabulary;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import static org.junit.jupiter.api.Assertions.*;

class AwsDynamoDbVocabularyTest {

    static DynamoDbClient dynamoDbClient;

    static AwsDynamoDbVocabulary awsDynamoDbVocabulary;

    @BeforeAll
    static void setup() {
        dynamoDbClient = DynamoDbClient.builder()
                .region(Region.EU_WEST_2)
                .build();

        awsDynamoDbVocabulary = new AwsDynamoDbVocabulary(dynamoDbClient);
    }

    @AfterAll
    static void tearDown() {
        dynamoDbClient.close();
        awsDynamoDbVocabulary.close();
    }

    @Test
    public void testCreateTable_pass_if_created() {
        awsDynamoDbVocabulary.createTable();
    }

    @Test
    public void testAddItem_pass_if_item_added() {
        awsDynamoDbVocabulary.addNewItem();
    }

    @Test
    public void testGetAllItems_pass_if_all_items() {
        awsDynamoDbVocabulary.getAllItems();
    }
}