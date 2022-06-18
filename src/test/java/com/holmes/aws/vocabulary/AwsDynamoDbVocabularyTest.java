package com.holmes.aws.vocabulary;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.core.waiters.WaiterResponse;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;
import software.amazon.awssdk.services.dynamodb.waiters.DynamoDbWaiter;

import java.time.LocalDateTime;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class AwsDynamoDbVocabularyTest {

    static DynamoDbClient dynamoDbClient;

    static DynamoDbWaiter dynamoDbWaiter;

    static AwsDynamoDbVocabulary awsDynamoDbVocabulary;

    @BeforeAll
    static void setup() {
        dynamoDbClient = DynamoDbClient.builder()
                .region(Region.EU_WEST_2)
                .build();

        dynamoDbWaiter = dynamoDbClient.waiter();

        awsDynamoDbVocabulary = new AwsDynamoDbVocabulary(dynamoDbClient);
    }

    @AfterAll
    static void tearDown() {
        dynamoDbClient.close();
        awsDynamoDbVocabulary.close();
        dynamoDbWaiter.close();
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

    @Test
    public void testAddVocabularyItem_pass_if_item_add() {
        Vocabulary vocabulary = new Vocabulary("你好", "你好", "nihao");
        awsDynamoDbVocabulary.putVocabulary(vocabulary);
    }

    @Test
    public void testDeleteVocabularyItem_pass_if_item_deleted() {
        Vocabulary vocabulary = new Vocabulary("你好", "你好", "nihao");
        assertEquals(vocabulary, awsDynamoDbVocabulary.deleteVocabulary(vocabulary.k_vocab_id));
    }

    @Test
    public void testGetVocabularyItem_pass_if_item_retrieved() {
        Vocabulary vocabulary = new Vocabulary("你好", "你好", "nihao");
        assertEquals(vocabulary, awsDynamoDbVocabulary.getVocabulary(vocabulary.k_vocab_id));
    }
}