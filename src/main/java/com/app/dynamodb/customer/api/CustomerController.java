package com.app.dynamodb.customer.api;

import com.app.dynamodb.customer.domain.Customer;
import com.app.dynamodb.customer.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
@Tag(name = "Customer", description = "Customer management API using standard AWS SDK")
public class CustomerController {

    private final CustomerService service;

    @PostMapping
    @Operation(summary = "Create a new customer")
    public ResponseEntity<Customer> createCustomer(@Valid @RequestBody Customer customer) {
        log.info("REST request to create customer");
        Customer created = service.createCustomer(customer);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{customerId}")
    @Operation(summary = "Get customer by ID")
    public ResponseEntity<Customer> getCustomer(@PathVariable String customerId) {
        log.info("REST request to get customer: {}", customerId);
        Customer customer = service.getCustomer(customerId);
        return ResponseEntity.ok(customer);
    }

    @GetMapping
    @Operation(summary = "Get all customers")
    public ResponseEntity<List<Customer>> getAllCustomers() {
        log.info("REST request to get all customers");
        List<Customer> customers = service.getAllCustomers();
        return ResponseEntity.ok(customers);
    }

    @PutMapping("/{customerId}")
    @Operation(summary = "Update customer")
    public ResponseEntity<Customer> updateCustomer(
            @PathVariable String customerId,
            @Valid @RequestBody Customer customer) {
        log.info("REST request to update customer: {}", customerId);
        Customer updated = service.updateCustomer(customerId, customer);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{customerId}")
    @Operation(summary = "Delete customer")
    public ResponseEntity<Void> deleteCustomer(@PathVariable String customerId) {
        log.info("REST request to delete customer: {}", customerId);
        service.deleteCustomer(customerId);
        return ResponseEntity.noContent().build();
    }
}