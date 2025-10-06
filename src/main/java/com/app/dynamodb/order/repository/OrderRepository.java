package com.app.dynamodb.order.repository;

import com.app.dynamodb.order.domain.Order;
import com.app.dynamodb.shared.AwsConfigProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Repository
@RequiredArgsConstructor
public class OrderRepository {

    private final DynamoDbEnhancedClient enhancedClient;
    private final AwsConfigProperties properties;
    private DynamoDbTable<Order> table;

    private DynamoDbTable<Order> getTable() {
        if (table == null) {
            String tableName = properties.getTables().get("order");
            table = enhancedClient.table(tableName, TableSchema.fromBean(Order.class));
        }
        return table;
    }

    public Order save(Order order) {
        log.debug("Saving order: {}", order.getOrderId());
        
        if (order.getCreatedAt() == null) {
            order.setCreatedAt(Instant.now());
        }
        order.setUpdatedAt(Instant.now());

        getTable().putItem(order);
        log.info("Order saved successfully: {}", order.getOrderId());
        
        return order;
    }

    public Optional<Order> findById(String orderId) {
        log.debug("Finding order by ID: {}", orderId);
        
        Key key = Key.builder()
                .partitionValue(orderId)
                .build();

        Order order = getTable().getItem(key);
        
        if (order == null) {
            log.debug("Order not found: {}", orderId);
            return Optional.empty();
        }

        return Optional.of(order);
    }

    public List<Order> findAll() {
        log.debug("Finding all orders");
        
        List<Order> orders = getTable().scan().items().stream()
                .collect(Collectors.toList());
        
        log.info("Found {} orders", orders.size());
        return orders;
    }

    public Order update(Order order) {
        log.debug("Updating order: {}", order.getOrderId());
        
        order.setUpdatedAt(Instant.now());
        getTable().updateItem(order);
        
        log.info("Order updated successfully: {}", order.getOrderId());
        return order;
    }

    public void deleteById(String orderId) {
        log.debug("Deleting order: {}", orderId);
        
        Key key = Key.builder()
                .partitionValue(orderId)
                .build();

        getTable().deleteItem(key);
        log.info("Order deleted successfully: {}", orderId);
    }

    public List<Order> findByCustomerId(String customerId) {
        log.debug("Finding orders for customer: {}", customerId);
        
        List<Order> orders = getTable().scan().items().stream()
                .filter(order -> order.getCustomerId().equals(customerId))
                .collect(Collectors.toList());
        
        log.info("Found {} orders for customer: {}", orders.size(), customerId);
        return orders;
    }
}