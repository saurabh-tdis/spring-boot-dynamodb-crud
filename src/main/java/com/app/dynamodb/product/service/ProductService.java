package com.app.dynamodb.product.service;

import com.app.dynamodb.product.domain.Product;
import com.app.dynamodb.product.repository.ProductRepository;
import com.app.dynamodb.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository repository;

    public Product createProduct(Product product) {
        log.info("Creating new product: {}", product.getName());
        
        product.setProductId(UUID.randomUUID().toString());
        
        if (product.getStatus() == null) {
            product.setStatus(product.getStockQuantity() > 0 
                ? Product.ProductStatus.ACTIVE 
                : Product.ProductStatus.OUT_OF_STOCK);
        }
        
        return repository.save(product);
    }

    public Product getProduct(String productId) {
        log.debug("Retrieving product: {}", productId);
        return repository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + productId));
    }

    public List<Product> getAllProducts() {
        log.info("Retrieving all products");
        return repository.findAll();
    }

    public List<Product> getProductsByCategory(String category) {
        log.info("Retrieving products by category: {}", category);
        return repository.findByCategory(category);
    }

    public List<Product> getProductsByStatus(Product.ProductStatus status) {
        log.info("Retrieving products by status: {}", status);
        return repository.findByStatus(status);
    }

    public Product updateProduct(String productId, Product product) {
        log.info("Updating product: {}", productId);
        
        // Verify product exists
        repository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + productId));
        
        product.setProductId(productId);
        
        // Auto-update status based on stock
        if (product.getStockQuantity() != null) {
            if (product.getStockQuantity() == 0) {
                product.setStatus(Product.ProductStatus.OUT_OF_STOCK);
            } else if (product.getStatus() == Product.ProductStatus.OUT_OF_STOCK) {
                product.setStatus(Product.ProductStatus.ACTIVE);
            }
        }
        
        return repository.update(product);
    }

    public Product updateProductStatus(String productId, Product.ProductStatus status) {
        log.info("Updating product status: {} to {}", productId, status);
        
        Product product = repository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + productId));
        
        product.setStatus(status);
        return repository.update(product);
    }

    public boolean adjustStock(String productId, int quantity) {
        log.info("Adjusting stock for product: {} by quantity: {}", productId, quantity);
        
        boolean success = repository.updateStock(productId, quantity);
        
        if (!success) {
            if (repository.findById(productId).isEmpty()) {
                throw new ResourceNotFoundException("Product not found: " + productId);
            }
            throw new IllegalStateException("Insufficient stock for product: " + productId);
        }
        
        return true;
    }

    public boolean reduceStock(String productId, int quantity) {
        log.info("Reducing stock for product: {} by quantity: {}", productId, quantity);
        return adjustStock(productId, -quantity);
    }

    public boolean increaseStock(String productId, int quantity) {
        log.info("Increasing stock for product: {} by quantity: {}", productId, quantity);
        return adjustStock(productId, quantity);
    }

    public void deleteProduct(String productId) {
        log.info("Deleting product: {}", productId);
        
        // Verify product exists
        repository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + productId));
        
        repository.deleteById(productId);
    }

    public List<Product> getAvailableProducts() {
        log.info("Retrieving available products");
        return repository.findByStatus(Product.ProductStatus.ACTIVE);
    }

    public List<Product> getOutOfStockProducts() {
        log.info("Retrieving out of stock products");
        return repository.findByStatus(Product.ProductStatus.OUT_OF_STOCK);
    }
}