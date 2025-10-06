package com.app.dynamodb.customer;

import com.app.dynamodb.customer.domain.Customer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.utility.DockerImageName;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.testcontainers.containers.localstack.LocalStackContainer.Service.DYNAMODB;

@SpringBootTest
@AutoConfigureMockMvc
//@Testcontainers
class CustomerIntegrationTest {

//    @Container
    static LocalStackContainer localstack = new LocalStackContainer(
            DockerImageName.parse("localstack/localstack:latest"))
            .withServices(DYNAMODB);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("aws.dynamodb.endpoint", 
            () -> localstack.getEndpointOverride(DYNAMODB).toString());
        registry.add("aws.dynamodb.region", () -> localstack.getRegion());
        registry.add("aws.dynamodb.access-key", () -> localstack.getAccessKey());
        registry.add("aws.dynamodb.secret-key", () -> localstack.getSecretKey());
    }

    @Test
    void testCreateAndGetCustomer() throws Exception {
        Customer customer = Customer.builder()
                .email("integration@test.com")
                .firstName("Integration")
                .lastName("Test")
                .phone("+1234567890")
                .address("123 Test St")
                .build();

        String customerJson = objectMapper.writeValueAsString(customer);

        // Create customer
        String response = mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(customerJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.customerId").exists())
                .andExpect(jsonPath("$.email").value("integration@test.com"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Customer created = objectMapper.readValue(response, Customer.class);

        // Get customer
        mockMvc.perform(get("/api/v1/customers/" + created.getCustomerId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerId").value(created.getCustomerId()))
                .andExpect(jsonPath("$.email").value("integration@test.com"));
    }

    @Test
    void testGetNonExistentCustomer() throws Exception {
        mockMvc.perform(get("/api/v1/customers/non-existent"))
                .andExpect(status().isNotFound());
    }
}