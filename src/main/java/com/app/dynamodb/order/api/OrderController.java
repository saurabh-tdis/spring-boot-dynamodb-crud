package com.app.dynamodb.order.api;

import com.app.dynamodb.order.domain.Order;
import com.app.dynamodb.order.service.OrderService;
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
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@Tag(name = "Order", description = "Order management API using DynamoDB Enhanced Client")
public class OrderController {

    private final OrderService service;

    @PostMapping
    @Operation(summary = "Create a new order")
    public ResponseEntity<Order> createOrder(@Valid @RequestBody Order order) {
        log.info("REST request to create order");
        Order created = service.createOrder(order);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{orderId}")
    @Operation(summary = "Get order by ID")
    public ResponseEntity<Order> getOrder(@PathVariable String orderId) {
        log.info("REST request to get order: {}", orderId);
        Order order = service.getOrder(orderId);
        return ResponseEntity.ok(order);
    }

    @GetMapping
    @Operation(summary = "Get all orders")
    public ResponseEntity<List<Order>> getAllOrders(
            @RequestParam(required = false) String customerId) {
        log.info("REST request to get all orders");
        
        if (customerId != null) {
            List<Order> orders = service.getOrdersByCustomerId(customerId);
            return ResponseEntity.ok(orders);
        }
        
        List<Order> orders = service.getAllOrders();
        return ResponseEntity.ok(orders);
    }

    @PutMapping("/{orderId}")
    @Operation(summary = "Update order")
    public ResponseEntity<Order> updateOrder(
            @PathVariable String orderId,
            @Valid @RequestBody Order order) {
        log.info("REST request to update order: {}", orderId);
        Order updated = service.updateOrder(orderId, order);
        return ResponseEntity.ok(updated);
    }

    @PatchMapping("/{orderId}/status")
    @Operation(summary = "Update order status")
    public ResponseEntity<Order> updateOrderStatus(
            @PathVariable String orderId,
            @RequestParam Order.OrderStatus status) {
        log.info("REST request to update order status: {} to {}", orderId, status);
        Order updated = service.updateOrderStatus(orderId, status);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{orderId}")
    @Operation(summary = "Delete order")
    public ResponseEntity<Void> deleteOrder(@PathVariable String orderId) {
        log.info("REST request to delete order: {}", orderId);
        service.deleteOrder(orderId);
        return ResponseEntity.noContent().build();
    }
}