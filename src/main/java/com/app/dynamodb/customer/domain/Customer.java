package com.app.dynamodb.customer.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Customer {
    private String customerId;
    private String email;
    private String firstName;
    private String lastName;
    private String phone;
    private String address;
    private Instant createdAt;
    private Instant updatedAt;
}