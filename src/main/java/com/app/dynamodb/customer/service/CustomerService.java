package com.app.dynamodb.customer.service;

import com.app.dynamodb.customer.domain.Customer;
import com.app.dynamodb.customer.repository.CustomerRepository;
import com.app.dynamodb.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository repository;

    public Customer createCustomer(Customer customer) {
        log.info("Creating new customer with email: {}", customer.getEmail());
        customer.setCustomerId(UUID.randomUUID().toString());
        return repository.save(customer);
    }

    public Customer getCustomer(String customerId) {
        log.debug("Retrieving customer: {}", customerId);
        return repository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found: " + customerId));
    }

    public List<Customer> getAllCustomers() {
        log.info("Retrieving all customers");
        return repository.findAll();
    }

    public Customer updateCustomer(String customerId, Customer customer) {
        log.info("Updating customer: {}", customerId);
        
        // Verify customer exists
        repository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found: " + customerId));
        
        customer.setCustomerId(customerId);
        return repository.update(customer);
    }

    public void deleteCustomer(String customerId) {
        log.info("Deleting customer: {}", customerId);
        
        // Verify customer exists
        repository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found: " + customerId));
        
        repository.deleteById(customerId);
    }
}