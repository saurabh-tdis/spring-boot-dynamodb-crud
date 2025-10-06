package com.app.dynamodb.customer.service;

import com.app.dynamodb.customer.domain.Customer;
import com.app.dynamodb.customer.repository.CustomerRepository;
import com.app.dynamodb.shared.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository repository;

    @InjectMocks
    private CustomerService service;

    private Customer testCustomer;

    @BeforeEach
    void setUp() {
        testCustomer = Customer.builder()
                .customerId("test-123")
                .email("test@app.com")
                .firstName("John")
                .lastName("Doe")
                .phone("+1234567890")
                .address("123 Main St")
                .build();
    }

    @Test
    void createCustomer_Success() {
        when(repository.save(any(Customer.class))).thenReturn(testCustomer);

        Customer result = service.createCustomer(testCustomer);

        assertNotNull(result);
        assertNotNull(result.getCustomerId());
        verify(repository, times(1)).save(any(Customer.class));
    }

    @Test
    void getCustomer_Found() {
        when(repository.findById(anyString())).thenReturn(Optional.of(testCustomer));

        Customer result = service.getCustomer("test-123");

        assertNotNull(result);
        assertEquals("test@app.com", result.getEmail());
        verify(repository, times(1)).findById("test-123");
    }

    @Test
    void getCustomer_NotFound() {
        when(repository.findById(anyString())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            service.getCustomer("non-existent");
        });
    }

    @Test
    void getAllCustomers_Success() {
        List<Customer> customers = Arrays.asList(testCustomer);
        when(repository.findAll()).thenReturn(customers);

        List<Customer> result = service.getAllCustomers();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(repository, times(1)).findAll();
    }

    @Test
    void updateCustomer_Success() {
        when(repository.findById(anyString())).thenReturn(Optional.of(testCustomer));
        when(repository.update(any(Customer.class))).thenReturn(testCustomer);

        Customer result = service.updateCustomer("test-123", testCustomer);

        assertNotNull(result);
        verify(repository, times(1)).update(any(Customer.class));
    }

    @Test
    void updateCustomer_NotFound() {
        when(repository.findById(anyString())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            service.updateCustomer("non-existent", testCustomer);
        });
    }

    @Test
    void deleteCustomer_Success() {
        when(repository.findById(anyString())).thenReturn(Optional.of(testCustomer));
        doNothing().when(repository).deleteById(anyString());

        service.deleteCustomer("test-123");

        verify(repository, times(1)).deleteById("test-123");
    }

    @Test
    void deleteCustomer_NotFound() {
        when(repository.findById(anyString())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            service.deleteCustomer("non-existent");
        });
    }
}