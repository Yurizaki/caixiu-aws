package com.holmes.aws.vocabulary;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.waiters.DynamoDbWaiter;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestMethodOrder(MethodOrderer.MethodName.class)
class AwsDynamoDbVocabularyTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(AwsDynamoDbVocabularyTest.class);
    private static DynamoDbClient dynamoDbClient;
    private static DynamoDbWaiter dynamoDbWaiter;
    private static AwsDynamoDbVocabulary awsDynamoDbVocabulary;

    @BeforeAll
    static void setup() {
        dynamoDbClient = DynamoDbClient.builder().region(Region.EU_WEST_2).build();
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
    public void testGetAllItems_pass_if_all_items() {
        List<Vocabulary> vocabularyList = awsDynamoDbVocabulary.getAllItems();
        vocabularyList.forEach(v -> { System.out.println(v.toString()); });
    }

    @Test
    public void testAddVocabularyItem_pass_if_item_add() {
        Vocabulary vocabulary = new Vocabulary("你好", "nihao", VocabularyCategory.EXPRESSION);
        awsDynamoDbVocabulary.putVocabulary(vocabulary);
    }

    @Test
    public void testDeleteVocabularyItem_pass_if_item_deleted() {
        Vocabulary vocabulary = new Vocabulary("你好", "nihao", VocabularyCategory.EXPRESSION);
        assertEquals(vocabulary, awsDynamoDbVocabulary.deleteVocabulary(vocabulary.getK_vocab_id()));
    }

    @Test
    public void testGetVocabularyItem_pass_if_item_retrieved() {
        Vocabulary vocabulary = new Vocabulary("你好", "nihao", VocabularyCategory.EXPRESSION);
        assertEquals(vocabulary, awsDynamoDbVocabulary.getVocabulary(vocabulary.getK_vocab_id()));
    }

    @Test
    public void testBatchPutVocabulary_pass_if_items_added() {
        Vocabulary vocabulary = new Vocabulary("好", "hao", VocabularyCategory.EXPRESSION);
        Vocabulary vocabulary2 = new Vocabulary("不错", "bu cuo", VocabularyCategory.EXPRESSION);

        awsDynamoDbVocabulary.putBatchItem(Arrays.asList(vocabulary, vocabulary2));
    }
}