package com.holmes.aws;

import org.junit.jupiter.api.*;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.MethodName.class)
public class AwsDynamodbTest {

    static DynamoDbClient dynamoDbClient;

    private String testTableName = "JunitTest";

    @BeforeAll
    static void setup() {
        dynamoDbClient = DynamoDbClient.builder()
                .region(Region.EU_WEST_2)
                .build();
    }

    @AfterAll
    static void tearDown() {
        dynamoDbClient.close();
    }

    @Test
    public void testQueryTable_pass_if_connected() {
        ListTablesResponse listTablesResponse = dynamoDbClient.listTables();
        assertTrue(listTablesResponse.tableNames().size() > 0);
    }

    @Test
    public void testCreateTable_pass_if_created() {
        CreateTableRequest createTableRequest = CreateTableRequest.builder()
                .tableName(testTableName)
                .keySchema(
                        KeySchemaElement.builder()
                                .keyType(KeyType.HASH)
                                .attributeName("TestName")
                                .build())
                .attributeDefinitions(
                        AttributeDefinition.builder()
                                .attributeName("TestName")
                                .attributeType(ScalarAttributeType.S)
                                .build())
                .billingMode(BillingMode.PAY_PER_REQUEST)
                .build();

        CreateTableResponse createTableResponse = dynamoDbClient.createTable(createTableRequest);
        assertTrue(createTableResponse.sdkHttpResponse().isSuccessful());
    }

    @Test
    public void testDeleteTable_pass_if_deleted() {
        ListTablesResponse listTablesResponse = dynamoDbClient.listTables();
        if(listTablesResponse.tableNames().contains(testTableName)) {
            DeleteTableResponse deleteTableResponse = dynamoDbClient.deleteTable(
                    DeleteTableRequest.builder()
                            .tableName(testTableName)
                            .build());

            assertTrue(deleteTableResponse.sdkHttpResponse().isSuccessful());
        }
    }

}
