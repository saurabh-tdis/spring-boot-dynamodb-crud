package com.app.dynamodb.config;

import com.app.dynamodb.shared.AwsConfigProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class DynamoDbTableInitializer {

    private final DynamoDbClient dynamoDbClient;
    private final AwsConfigProperties properties;

    @Bean
    public CommandLineRunner initializeTables() {
        return args -> {
            createCustomerTable();
            createOrderTable();
            createProductTable();
        };
    }

    private void createCustomerTable() {
        String tableName = properties.getTables().get("customer");
        
        if (tableExists(tableName)) {
            log.info("Customer table already exists: {}", tableName);
            return;
        }

        log.info("Creating Customer table: {}", tableName);

        CreateTableRequest request = CreateTableRequest.builder()
                .tableName(tableName)
                .keySchema(KeySchemaElement.builder()
                        .attributeName("customerId")
                        .keyType(KeyType.HASH)
                        .build())
                .attributeDefinitions(AttributeDefinition.builder()
                        .attributeName("customerId")
                        .attributeType(ScalarAttributeType.S)
                        .build())
                .billingMode(BillingMode.PAY_PER_REQUEST)
                .build();

        try {
            dynamoDbClient.createTable(request);
            log.info("Customer table created successfully: {}", tableName);
        } catch (ResourceInUseException e) {
            log.warn("Table already exists: {}", tableName);
        } catch (Exception e) {
            log.error("Error creating customer table: {}", e.getMessage(), e);
        }
    }

    private void createOrderTable() {
        String tableName = properties.getTables().get("order");
        
        if (tableExists(tableName)) {
            log.info("Order table already exists: {}", tableName);
            return;
        }

        log.info("Creating Order table: {}", tableName);

        CreateTableRequest request = CreateTableRequest.builder()
                .tableName(tableName)
                .keySchema(KeySchemaElement.builder()
                        .attributeName("orderId")
                        .keyType(KeyType.HASH)
                        .build())
                .attributeDefinitions(AttributeDefinition.builder()
                        .attributeName("orderId")
                        .attributeType(ScalarAttributeType.S)
                        .build())
                .billingMode(BillingMode.PAY_PER_REQUEST)
                .build();

        try {
            dynamoDbClient.createTable(request);
            log.info("Order table created successfully: {}", tableName);
        } catch (ResourceInUseException e) {
            log.warn("Table already exists: {}", tableName);
        } catch (Exception e) {
            log.error("Error creating order table: {}", e.getMessage(), e);
        }
    }

    private boolean tableExists(String tableName) {
        try {
            DescribeTableRequest request = DescribeTableRequest.builder()
                    .tableName(tableName)
                    .build();
            dynamoDbClient.describeTable(request);
            return true;
        } catch (ResourceNotFoundException e) {
            return false;
        }
    }

    private void createProductTable() {
        String tableName = properties.getTables().get("product");
        
        if (tableExists(tableName)) {
            log.info("Product table already exists: {}", tableName);
            return;
        }

        log.info("Creating Product table: {}", tableName);

        CreateTableRequest request = CreateTableRequest.builder()
                .tableName(tableName)
                .keySchema(KeySchemaElement.builder()
                        .attributeName("productId")
                        .keyType(KeyType.HASH)
                        .build())
                .attributeDefinitions(
                        AttributeDefinition.builder()
                                .attributeName("productId")
                                .attributeType(ScalarAttributeType.S)
                                .build(),
                        AttributeDefinition.builder()
                                .attributeName("category")
                                .attributeType(ScalarAttributeType.S)
                                .build()
                )
                .globalSecondaryIndexes(GlobalSecondaryIndex.builder()
                        .indexName("category-index")
                        .keySchema(KeySchemaElement.builder()
                                .attributeName("category")
                                .keyType(KeyType.HASH)
                                .build())
                        .projection(Projection.builder()
                                .projectionType(ProjectionType.ALL)
                                .build())
                        .build())
                .billingMode(BillingMode.PAY_PER_REQUEST)
                .build();

        try {
            dynamoDbClient.createTable(request);
            log.info("Product table created successfully: {}", tableName);
        } catch (ResourceInUseException e) {
            log.warn("Table already exists: {}", tableName);
        } catch (Exception e) {
            log.error("Error creating product table: {}", e.getMessage(), e);
        }
    }
}