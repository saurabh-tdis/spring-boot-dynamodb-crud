package com.app.dynamodb.shared.health;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.ListTablesRequest;

@Slf4j
@Component
@RequiredArgsConstructor
public class DynamoDbHealthIndicator implements HealthIndicator {

    private final DynamoDbClient dynamoDbClient;

    @Override
    public Health health() {
        try {
            ListTablesRequest request = ListTablesRequest.builder().limit(1).build();
            
            dynamoDbClient.listTables(request);
            
            return Health.up()
                    .withDetail("service", "DynamoDB")
                    .withDetail("status", "Connected")
                    .build();
        } catch (Exception e) {
            log.error("DynamoDB health check failed: {}", e.getMessage());
            return Health.down()
                    .withDetail("service", "DynamoDB")
                    .withDetail("error", e.getMessage())
                    .build();
        }
    }
}