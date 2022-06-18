package com.holmes.aws.vocabulary;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import software.amazon.awssdk.core.waiters.WaiterResponse;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;
import software.amazon.awssdk.services.dynamodb.waiters.DynamoDbWaiter;

import java.lang.reflect.Type;
import java.util.HashMap;
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

    public PutItemResponse addNewVocabulary(Vocabulary vocabulary) {
        if(vocabulary != null) {
            try {
                return dynamoDbClient.putItem(vocabulary.getRequest(tableName));
            }
            catch (DynamoDbException e) {
                System.err.println(e.getMessage());
                System.exit(1);
            }
        }

        return null;
    }

    public DeleteItemResponse deleteVocabulary(String name) {
        HashMap<String, AttributeValue> mappy = new HashMap<>();
        mappy.put("k_vocab_id", AttributeValue.builder().s(name).build());


        DeleteItemRequest deleteItemRequest = DeleteItemRequest.builder()
                .tableName(tableName)
                .key(mappy)
                .build();
        return dynamoDbClient.deleteItem(deleteItemRequest);
    }



    public void getAllItems() {
//        GetItemResponse getItemResponse = dynamoDbClient.getItem(GetItemRequest.builder()
//                .tableName(tableName)
//                .projectionExpression("*")
//                .build());

        ScanRequest scanRequest = ScanRequest.builder()
                .tableName(tableName)
                .build();

        ScanResponse scanResponse = dynamoDbClient.scan(scanRequest);


        Map<String, AttributeValue> items = scanResponse.items().get(0);

        for (String key1 : items.keySet()) {
            System.out.format("%s: %s\n", key1, items.get(key1).toString());
        }

        Gson gson = new Gson();
        Type gsonType = new TypeToken<HashMap>() {
        }.getType();
        String gsonString = gson.toJson(items, gsonType);
        System.out.println(gsonString);
    }



    public GetItemResponse getVocabulary(String key) {
        HashMap<String, AttributeValue> mappy2 = new HashMap<>();
        mappy2.put("k_vocab_id", AttributeValue.builder().s(key).build());

        GetItemRequest getItemRequest = GetItemRequest.builder()
                .tableName(tableName)
                .key(mappy2)
                .build();

        return dynamoDbClient.getItem(getItemRequest);
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
