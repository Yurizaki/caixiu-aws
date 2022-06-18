package com.holmes.aws.vocabulary;

import software.amazon.awssdk.core.waiters.WaiterResponse;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeDefinition;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.CreateTableRequest;
import software.amazon.awssdk.services.dynamodb.model.CreateTableResponse;
import software.amazon.awssdk.services.dynamodb.model.DescribeTableRequest;
import software.amazon.awssdk.services.dynamodb.model.DescribeTableResponse;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;
import software.amazon.awssdk.services.dynamodb.model.KeySchemaElement;
import software.amazon.awssdk.services.dynamodb.model.KeyType;
import software.amazon.awssdk.services.dynamodb.model.ListTablesResponse;
import software.amazon.awssdk.services.dynamodb.model.ProvisionedThroughput;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.ResourceNotFoundException;
import software.amazon.awssdk.services.dynamodb.model.ScalarAttributeType;
import software.amazon.awssdk.services.dynamodb.waiters.DynamoDbWaiter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class AwsDynamoDbVocabulary {

    private final DynamoDbClient dynamoDbClient;
    private final DynamoDbWaiter dynamoDbWaiter;
    private final String tableName = "T_VOCABULARY";
    private final String key = "k_vocab_id";

    private final DynamoDbEnhancedClient dynamoDbEnhancedClient;

    public AwsDynamoDbVocabulary(DynamoDbClient dynamoDbClient) {
        this.dynamoDbClient = dynamoDbClient;
        this.dynamoDbWaiter = dynamoDbClient.waiter();

        dynamoDbEnhancedClient = DynamoDbEnhancedClient.builder().dynamoDbClient(this.dynamoDbClient).build();
    }

    public void createTable() {
        ListTablesResponse listTablesResponse = dynamoDbClient.listTables();

        if (!listTablesResponse.tableNames().contains(tableName)) {
            CreateTableRequest createTableRequest = CreateTableRequest.builder()
                    .attributeDefinitions(AttributeDefinition.builder()
                            .attributeName(key)
                            .attributeType(ScalarAttributeType.S)
                            .build())
                    .keySchema(KeySchemaElement.builder()
                            .attributeName(key)
                            .keyType(KeyType.HASH)
                            .build())
                    .provisionedThroughput(ProvisionedThroughput.builder()
                            .readCapacityUnits(10L)
                            .writeCapacityUnits(10L)
                            .build())
                    .tableName(tableName)
                    .build();

            try {
                CreateTableResponse createTableResponse = dynamoDbClient.createTable(createTableRequest);
                DescribeTableRequest describeTableRequest = DescribeTableRequest.builder()
                        .tableName(tableName)
                        .build();

                WaiterResponse<DescribeTableResponse> waiterResponse
                        = dynamoDbWaiter.waitUntilTableExists(describeTableRequest);
                waiterResponse.matched().response().ifPresent(System.out::println);

                String newTable = createTableResponse.tableDescription().tableName();
            } catch (DynamoDbException ex) {
                System.err.println(ex.getMessage());
                System.exit(1);
            }
        }
    }

    public void close() {
        try {
            if (dynamoDbClient != null) {
                dynamoDbClient.close();
            }
            if (dynamoDbWaiter != null) {
                dynamoDbWaiter.close();
            }
        } catch (DynamoDbException ex) {
            System.err.println(ex.getMessage());
            System.exit(1);
        }
    }

    public void putVocabulary(Vocabulary vocabulary) {
        try {
            dynamoDbEnhancedClient.table(tableName, TableSchema.fromBean(Vocabulary.class)).putItem(vocabulary);
        }
        catch(DynamoDbException dynamoDbException) {
            System.out.println();
        }
    }

    public Vocabulary deleteVocabulary(String name) {
        try {
            return dynamoDbEnhancedClient.table(tableName, TableSchema.fromBean(Vocabulary.class))
                    .deleteItem(Key.builder().partitionValue(name).build());
        }
        catch(DynamoDbException dynamoDbException) {
            System.out.println();
            return null;
        }
    }

    public List<Vocabulary> getAllItems() {
        Iterator<Vocabulary> scanResults =
                dynamoDbEnhancedClient.table(tableName, TableSchema.fromBean(Vocabulary.class))
                .scan()
                .items()
                .iterator();

        List<Vocabulary> vocabularies = new ArrayList<>();
        while(scanResults.hasNext()) {
            vocabularies.add(scanResults.next());
        }

        return vocabularies;
    }

    public Vocabulary getVocabulary(String key) {
        try {
            return dynamoDbEnhancedClient.table(tableName, TableSchema.fromBean(Vocabulary.class))
                    .getItem(Key.builder().partitionValue(key).build());
        }
        catch(DynamoDbException dynamoDbException) {
            System.out.println();
            return null;
        }
    }

    public void addNewItem() {
        Map<String, AttributeValue> map = new HashMap<>();
        map.put("name", AttributeValue.builder().s("asdd").build());
        map.put("k_vocab_id", AttributeValue.builder().s("1").build());

        PutItemRequest putItemRequest = PutItemRequest.builder()
                .tableName(tableName)
                .item(map)
                .build();

        try {
            dynamoDbClient.putItem(putItemRequest);
        } catch (ResourceNotFoundException e) {
            System.err.format("Error: The Amazon DynamoDB table \"%s\" can't be found.\n", tableName);
            System.err.println("Be sure that it exists and that you've typed its name correctly!");
            System.exit(1);
        } catch (DynamoDbException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
}
