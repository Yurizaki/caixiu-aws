package com.holmes.aws.vocabulary;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.core.waiters.WaiterResponse;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeDefinition;
import software.amazon.awssdk.services.dynamodb.model.CreateTableRequest;
import software.amazon.awssdk.services.dynamodb.model.CreateTableResponse;
import software.amazon.awssdk.services.dynamodb.model.DescribeTableRequest;
import software.amazon.awssdk.services.dynamodb.model.DescribeTableResponse;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;
import software.amazon.awssdk.services.dynamodb.model.KeySchemaElement;
import software.amazon.awssdk.services.dynamodb.model.KeyType;
import software.amazon.awssdk.services.dynamodb.model.ListTablesResponse;
import software.amazon.awssdk.services.dynamodb.model.ProvisionedThroughput;
import software.amazon.awssdk.services.dynamodb.model.ScalarAttributeType;
import software.amazon.awssdk.services.dynamodb.waiters.DynamoDbWaiter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class AwsDynamoDbVocabulary {

    private static final Logger LOGGER = LoggerFactory.getLogger(AwsDynamoDbVocabulary.class);

    private final DynamoDbClient dynamoDbClient;
    private final DynamoDbWaiter dynamoDbWaiter;
    private final DynamoDbEnhancedClient dynamoDbEnhancedClient;

    public AwsDynamoDbVocabulary(DynamoDbClient dynamoDbClient) {
        this.dynamoDbClient = dynamoDbClient;
        this.dynamoDbWaiter = dynamoDbClient.waiter();

        dynamoDbEnhancedClient = DynamoDbEnhancedClient.builder().dynamoDbClient(this.dynamoDbClient).build();
    }

    public CreateTableResponse createTable() {
        ListTablesResponse listTablesResponse = dynamoDbClient.listTables();

        if (!listTablesResponse.tableNames().contains(Vocabulary.TABLE_NAME)) {
            LOGGER.info("Initiating create table: " + Vocabulary.TABLE_NAME);

            CreateTableRequest createTableRequest = CreateTableRequest.builder()
                    .attributeDefinitions(AttributeDefinition.builder()
                            .attributeName(Vocabulary.PARTITION_KEY_NAME)
                            .attributeType(ScalarAttributeType.S)
                            .build())
                    .keySchema(KeySchemaElement.builder()
                            .attributeName(Vocabulary.PARTITION_KEY_NAME)
                            .keyType(KeyType.HASH)
                            .build())
                    .provisionedThroughput(ProvisionedThroughput.builder()
                            .readCapacityUnits(10L)
                            .writeCapacityUnits(10L)
                            .build())
                    .tableName(Vocabulary.TABLE_NAME)
                    .build();

            try {
                CreateTableResponse createTableResponse = dynamoDbClient.createTable(createTableRequest);
                DescribeTableRequest describeTableRequest = DescribeTableRequest.builder()
                        .tableName(Vocabulary.TABLE_NAME)
                        .build();

                WaiterResponse<DescribeTableResponse> waiterResponse
                        = dynamoDbWaiter.waitUntilTableExists(describeTableRequest);
                waiterResponse.matched().response().ifPresent(AwsDynamoDbVocabulary::present);

                return createTableResponse;
            }
            catch (DynamoDbException ex) {
                LOGGER.error(ex.getMessage());
                return null;
            }
        }
        else {
            LOGGER.info("Table already created: " + Vocabulary.TABLE_NAME);
            return null;
        }
    }

    private static void present(DescribeTableResponse describeTableResponse) {
        LOGGER.info("Completed create table: " + describeTableResponse.table().tableName());
    }

    public void close() {
        try {
            if (dynamoDbClient != null) {
                dynamoDbClient.close();
            }
            if (dynamoDbWaiter != null) {
                dynamoDbWaiter.close();
            }
        }
        catch (DynamoDbException ex) {
            LOGGER.error(ex.getMessage());
        }
    }

    public void putVocabulary(Vocabulary vocabulary) {
        try {
            LOGGER.info("Adding new vocabulary: " + vocabulary);
            dynamoDbEnhancedClient.table(Vocabulary.TABLE_NAME, TableSchema.fromBean(Vocabulary.class)).putItem(vocabulary);
        }
        catch (DynamoDbException ex) {
            LOGGER.error(ex.getMessage());
        }
    }

    public Vocabulary deleteVocabulary(String key) {
        try {
            LOGGER.info("Deleting vocabulary with key: " + key);
            return dynamoDbEnhancedClient.table(Vocabulary.TABLE_NAME, TableSchema.fromBean(Vocabulary.class))
                    .deleteItem(Key.builder().partitionValue(key).build());
        }
        catch (DynamoDbException ex) {
            LOGGER.error(ex.getMessage());
            return null;
        }
    }

    public List<Vocabulary> getAllItems() {
        try {
            LOGGER.info("Getting all Vocabulary Items");
            Iterator<Vocabulary> scanResults =
                    dynamoDbEnhancedClient.table(Vocabulary.TABLE_NAME, TableSchema.fromBean(Vocabulary.class))
                            .scan()
                            .items()
                            .iterator();

            List<Vocabulary> vocabularies = new ArrayList<>();
            while (scanResults.hasNext()) {
                vocabularies.add(scanResults.next());
            }
            return vocabularies;
        }
        catch (DynamoDbException ex) {
            LOGGER.error(ex.getMessage());
            return null;
        }
    }

    public Vocabulary getVocabulary(String key) {
        try {
            LOGGER.info("Getting vocabulary with key: " + key);
            return dynamoDbEnhancedClient.table(Vocabulary.TABLE_NAME, TableSchema.fromBean(Vocabulary.class))
                    .getItem(Key.builder().partitionValue(key).build());
        }
        catch (DynamoDbException ex) {
            LOGGER.error(ex.getMessage());
            return null;
        }
    }
}
