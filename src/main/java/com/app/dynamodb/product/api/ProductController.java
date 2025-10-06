package com.app.dynamodb.product.api;

import com.app.dynamodb.product.domain.Product;
import com.app.dynamodb.product.service.ProductService;
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
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Tag(name = "Product", description = "Product management API using Spring Cloud AWS DynamoDB")
public class ProductController {

    private final ProductService service;

    @PostMapping
    @Operation(summary = "Create a new product")
    public ResponseEntity<Product> createProduct(@Valid @RequestBody Product product) {
        log.info("REST request to create product");
        Product created = service.createProduct(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{productId}")
    @Operation(summary = "Get product by ID")
    public ResponseEntity<Product> getProduct(@PathVariable String productId) {
        log.info("REST request to get product: {}", productId);
        Product product = service.getProduct(productId);
        return ResponseEntity.ok(product);
    }

    @GetMapping
    @Operation(summary = "Get all products or filter by category/status")
    public ResponseEntity<List<Product>> getProducts(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Product.ProductStatus status) {
        log.info("REST request to get products");
        
        if (category != null) {
            List<Product> products = service.getProductsByCategory(category);
            return ResponseEntity.ok(products);
        }
        
        if (status != null) {
            List<Product> products = service.getProductsByStatus(status);
            return ResponseEntity.ok(products);
        }
        
        List<Product> products = service.getAllProducts();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/available")
    @Operation(summary = "Get all available products")
    public ResponseEntity<List<Product>> getAvailableProducts() {
        log.info("REST request to get available products");
        List<Product> products = service.getAvailableProducts();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/out-of-stock")
    @Operation(summary = "Get all out of stock products")
    public ResponseEntity<List<Product>> getOutOfStockProducts() {
        log.info("REST request to get out of stock products");
        List<Product> products = service.getOutOfStockProducts();
        return ResponseEntity.ok(products);
    }

    @PutMapping("/{productId}")
    @Operation(summary = "Update product")
    public ResponseEntity<Product> updateProduct(
            @PathVariable String productId,
            @Valid @RequestBody Product product) {
        log.info("REST request to update product: {}", productId);
        Product updated = service.updateProduct(productId, product);
        return ResponseEntity.ok(updated);
    }

    @PatchMapping("/{productId}/status")
    @Operation(summary = "Update product status")
    public ResponseEntity<Product> updateProductStatus(
            @PathVariable String productId,
            @RequestParam Product.ProductStatus status) {
        log.info("REST request to update product status: {} to {}", productId, status);
        Product updated = service.updateProductStatus(productId, status);
        return ResponseEntity.ok(updated);
    }

    @PatchMapping("/{productId}/stock/adjust")
    @Operation(summary = "Adjust product stock (positive or negative)")
    public ResponseEntity<Void> adjustStock(
            @PathVariable String productId,
            @RequestParam int quantity) {
        log.info("REST request to adjust stock for product: {} by {}", productId, quantity);
        service.adjustStock(productId, quantity);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{productId}/stock/reduce")
    @Operation(summary = "Reduce product stock")
    public ResponseEntity<Void> reduceStock(
            @PathVariable String productId,
            @RequestParam int quantity) {
        log.info("REST request to reduce stock for product: {} by {}", productId, quantity);
        service.reduceStock(productId, quantity);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{productId}/stock/increase")
    @Operation(summary = "Increase product stock")
    public ResponseEntity<Void> increaseStock(
            @PathVariable String productId,
            @RequestParam int quantity) {
        log.info("REST request to increase stock for product: {} by {}", productId, quantity);
        service.increaseStock(productId, quantity);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{productId}")
    @Operation(summary = "Delete product")
    public ResponseEntity<Void> deleteProduct(@PathVariable String productId) {
        log.info("REST request to delete product: {}", productId);
        service.deleteProduct(productId);
        return ResponseEntity.noContent().build();
    }
}