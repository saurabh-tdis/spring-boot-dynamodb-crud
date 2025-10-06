package com.app.dynamodb.customer.repository;

import com.app.dynamodb.shared.AwsConfigProperties;
import com.app.dynamodb.customer.domain.Customer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Repository
@RequiredArgsConstructor
public class CustomerRepository {

    private final DynamoDbClient dynamoDbClient;
    private final AwsConfigProperties properties;

    private String getTableName() {
        return properties.getTables().get("customer");
    }

    public Customer save(Customer customer) {
        log.debug("Saving customer: {}", customer.getCustomerId());
        
        if (customer.getCreatedAt() == null) {
            customer.setCreatedAt(Instant.now());
        }
        customer.setUpdatedAt(Instant.now());

        Map<String, AttributeValue> item = new HashMap<>();
        item.put("customerId", AttributeValue.builder().s(customer.getCustomerId()).build());
        item.put("email", AttributeValue.builder().s(customer.getEmail()).build());
        item.put("firstName", AttributeValue.builder().s(customer.getFirstName()).build());
        item.put("lastName", AttributeValue.builder().s(customer.getLastName()).build());
        
        if (customer.getPhone() != null) {
            item.put("phone", AttributeValue.builder().s(customer.getPhone()).build());
        }
        if (customer.getAddress() != null) {
            item.put("address", AttributeValue.builder().s(customer.getAddress()).build());
        }
        
        item.put("createdAt", AttributeValue.builder().s(customer.getCreatedAt().toString()).build());
        item.put("updatedAt", AttributeValue.builder().s(customer.getUpdatedAt().toString()).build());

        PutItemRequest request = PutItemRequest.builder()
                .tableName(getTableName())
                .item(item)
                .build();

        dynamoDbClient.putItem(request);
        log.info("Customer saved successfully: {}", customer.getCustomerId());
        
        return customer;
    }

    public Optional<Customer> findById(String customerId) {
        log.debug("Finding customer by ID: {}", customerId);
        
        Map<String, AttributeValue> key = new HashMap<>();
        key.put("customerId", AttributeValue.builder().s(customerId).build());

        GetItemRequest request = GetItemRequest.builder()
                .tableName(getTableName())
                .key(key)
                .build();

        GetItemResponse response = dynamoDbClient.getItem(request);
        
        if (!response.hasItem()) {
            log.debug("Customer not found: {}", customerId);
            return Optional.empty();
        }

        return Optional.of(mapToCustomer(response.item()));
    }

    public List<Customer> findAll() {
        log.debug("Finding all customers");
        
        ScanRequest request = ScanRequest.builder()
                .tableName(getTableName())
                .build();

        ScanResponse response = dynamoDbClient.scan(request);
        
        List<Customer> customers = response.items().stream()
                .map(this::mapToCustomer)
                .collect(Collectors.toList());
        
        log.info("Found {} customers", customers.size());
        return customers;
    }

    public Customer update(Customer customer) {
        log.debug("Updating customer: {}", customer.getCustomerId());
        
        customer.setUpdatedAt(Instant.now());

        Map<String, AttributeValue> key = new HashMap<>();
        key.put("customerId", AttributeValue.builder().s(customer.getCustomerId()).build());

        Map<String, AttributeValueUpdate> updates = new HashMap<>();
        updates.put("email", AttributeValueUpdate.builder()
                .value(AttributeValue.builder().s(customer.getEmail()).build())
                .action(AttributeAction.PUT)
                .build());
        updates.put("firstName", AttributeValueUpdate.builder()
                .value(AttributeValue.builder().s(customer.getFirstName()).build())
                .action(AttributeAction.PUT)
                .build());
        updates.put("lastName", AttributeValueUpdate.builder()
                .value(AttributeValue.builder().s(customer.getLastName()).build())
                .action(AttributeAction.PUT)
                .build());
        
        if (customer.getPhone() != null) {
            updates.put("phone", AttributeValueUpdate.builder()
                    .value(AttributeValue.builder().s(customer.getPhone()).build())
                    .action(AttributeAction.PUT)
                    .build());
        }
        if (customer.getAddress() != null) {
            updates.put("address", AttributeValueUpdate.builder()
                    .value(AttributeValue.builder().s(customer.getAddress()).build())
                    .action(AttributeAction.PUT)
                    .build());
        }
        
        updates.put("updatedAt", AttributeValueUpdate.builder()
                .value(AttributeValue.builder().s(customer.getUpdatedAt().toString()).build())
                .action(AttributeAction.PUT)
                .build());

        UpdateItemRequest request = UpdateItemRequest.builder()
                .tableName(getTableName())
                .key(key)
                .attributeUpdates(updates)
                .build();

        dynamoDbClient.updateItem(request);
        log.info("Customer updated successfully: {}", customer.getCustomerId());
        
        return customer;
    }

    public void deleteById(String customerId) {
        log.debug("Deleting customer: {}", customerId);
        
        Map<String, AttributeValue> key = new HashMap<>();
        key.put("customerId", AttributeValue.builder().s(customerId).build());

        DeleteItemRequest request = DeleteItemRequest.builder()
                .tableName(getTableName())
                .key(key)
                .build();

        dynamoDbClient.deleteItem(request);
        log.info("Customer deleted successfully: {}", customerId);
    }

    private Customer mapToCustomer(Map<String, AttributeValue> item) {
        return Customer.builder()
                .customerId(item.get("customerId").s())
                .email(item.get("email").s())
                .firstName(item.get("firstName").s())
                .lastName(item.get("lastName").s())
                .phone(item.containsKey("phone") ? item.get("phone").s() : null)
                .address(item.containsKey("address") ? item.get("address").s() : null)
                .createdAt(Instant.parse(item.get("createdAt").s()))
                .updatedAt(Instant.parse(item.get("updatedAt").s()))
                .build();
    }
}