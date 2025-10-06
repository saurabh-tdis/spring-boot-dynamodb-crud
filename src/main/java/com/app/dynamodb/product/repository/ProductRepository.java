package com.app.dynamodb.product.repository;

import com.app.dynamodb.product.domain.Product;
import io.awspring.cloud.dynamodb.DynamoDbTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ProductRepository {

    private final DynamoDbTemplate dynamoDbTemplate;

    public Product save(Product product) {
        log.debug("Saving product: {}", product.getProductId());
        
        if (product.getCreatedAt() == null) {
            product.setCreatedAt(Instant.now());
        }
        product.setUpdatedAt(Instant.now());

        Product saved = dynamoDbTemplate.save(product);
        log.info("Product saved successfully: {}", saved.getProductId());
        
        return saved;
    }

    public Optional<Product> findById(String productId) {
        log.debug("Finding product by ID: {}", productId);
        
        Key key = Key.builder()
                .partitionValue(productId)
                .build();

        Product product = dynamoDbTemplate.load(key, Product.class);
        
        if (product == null) {
            log.debug("Product not found: {}", productId);
            return Optional.empty();
        }

        return Optional.of(product);
    }

    public List<Product> findAll() {
        log.debug("Finding all products");
        
        PageIterable<Product> pages = dynamoDbTemplate.scanAll(Product.class);
        
        List<Product> products = pages.items().stream()
                .collect(Collectors.toList());
        
        log.info("Found {} products", products.size());
        return products;
    }

    public List<Product> findByCategory(String category) {
        log.debug("Finding products by category: {}", category);
        
        QueryConditional queryConditional = QueryConditional
                .keyEqualTo(Key.builder()
                        .partitionValue(category)
                        .build());

        QueryEnhancedRequest query = QueryEnhancedRequest.builder()
                .queryConditional(queryConditional)
                .build();

        // Note: This requires a GSI named "category-index"
        PageIterable<Product> pages = dynamoDbTemplate.query(query, Product.class);
        
        List<Product> products = pages.items().stream()
                .collect(Collectors.toList());
        
        log.info("Found {} products in category: {}", products.size(), category);
        return products;
    }

    public Product update(Product product) {
        log.debug("Updating product: {}", product.getProductId());
        
        product.setUpdatedAt(Instant.now());
        Product updated = dynamoDbTemplate.update(product);
        
        log.info("Product updated successfully: {}", updated.getProductId());
        return updated;
    }

    public void deleteById(String productId) {
        log.debug("Deleting product: {}", productId);
        
        Key key = Key.builder()
                .partitionValue(productId)
                .build();

        dynamoDbTemplate.delete(key, Product.class);
        log.info("Product deleted successfully: {}", productId);
    }

    public List<Product> findByStatus(Product.ProductStatus status) {
        log.debug("Finding products by status: {}", status);
        
        // Using scan with filter for status (not optimized for large datasets)
        PageIterable<Product> pages = dynamoDbTemplate.scanAll(Product.class);
        
        List<Product> products = pages.items().stream()
                .filter(product -> product.getStatus() == status)
                .collect(Collectors.toList());
        
        log.info("Found {} products with status: {}", products.size(), status);
        return products;
    }

    public boolean updateStock(String productId, int quantity) {
        log.debug("Updating stock for product: {} with quantity: {}", productId, quantity);
        
        Optional<Product> productOpt = findById(productId);
        if (productOpt.isEmpty()) {
            log.warn("Product not found for stock update: {}", productId);
            return false;
        }

        Product product = productOpt.get();
        int newStock = product.getStockQuantity() + quantity;
        
        if (newStock < 0) {
            log.warn("Insufficient stock for product: {}", productId);
            return false;
        }

        product.setStockQuantity(newStock);
        
        // Update status based on stock
        if (newStock == 0) {
            product.setStatus(Product.ProductStatus.OUT_OF_STOCK);
        } else if (product.getStatus() == Product.ProductStatus.OUT_OF_STOCK) {
            product.setStatus(Product.ProductStatus.ACTIVE);
        }

        update(product);
        log.info("Stock updated successfully for product: {}, new stock: {}", productId, newStock);
        
        return true;
    }
}