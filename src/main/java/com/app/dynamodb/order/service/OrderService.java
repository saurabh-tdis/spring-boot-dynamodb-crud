package com.app.dynamodb.order.service;

import com.app.dynamodb.order.domain.Order;
import com.app.dynamodb.order.repository.OrderRepository;
import com.app.dynamodb.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository repository;

    public Order createOrder(Order order) {
        log.info("Creating new order for customer: {}", order.getCustomerId());
        order.setOrderId(UUID.randomUUID().toString());
        
        if (order.getStatus() == null) {
            order.setStatus(Order.OrderStatus.PENDING);
        }
        
        return repository.save(order);
    }

    public Order getOrder(String orderId) {
        log.debug("Retrieving order: {}", orderId);
        return repository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + orderId));
    }

    public List<Order> getAllOrders() {
        log.info("Retrieving all orders");
        return repository.findAll();
    }

    public List<Order> getOrdersByCustomerId(String customerId) {
        log.info("Retrieving orders for customer: {}", customerId);
        return repository.findByCustomerId(customerId);
    }

    public Order updateOrder(String orderId, Order order) {
        log.info("Updating order: {}", orderId);
        
        // Verify order exists
        repository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + orderId));
        
        order.setOrderId(orderId);
        return repository.update(order);
    }

    public Order updateOrderStatus(String orderId, Order.OrderStatus status) {
        log.info("Updating order status: {} to {}", orderId, status);
        
        Order order = repository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + orderId));
        
        order.setStatus(status);
        return repository.update(order);
    }

    public void deleteOrder(String orderId) {
        log.info("Deleting order: {}", orderId);
        
        // Verify order exists
        repository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + orderId));
        
        repository.deleteById(orderId);
    }
}