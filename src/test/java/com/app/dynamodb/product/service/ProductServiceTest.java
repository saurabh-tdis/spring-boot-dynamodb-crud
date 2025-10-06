package com.app.dynamodb.product.service;

import com.app.dynamodb.product.domain.Product;
import com.app.dynamodb.product.repository.ProductRepository;
import com.app.dynamodb.shared.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository repository;

    @InjectMocks
    private ProductService service;

    private Product testProduct;

    @BeforeEach
    void setUp() {
        testProduct = Product.builder()
                .productId("prod-123")
                .name("Laptop")
                .description("High-performance laptop")
                .category("Electronics")
                .price(new BigDecimal("999.99"))
                .stockQuantity(50)
                .manufacturer("TechCorp")
                .status(Product.ProductStatus.ACTIVE)
                .build();
    }

    @Test
    void createProduct_Success() {
        when(repository.save(any(Product.class))).thenReturn(testProduct);

        Product result = service.createProduct(testProduct);

        assertNotNull(result);
        assertNotNull(result.getProductId());
        verify(repository, times(1)).save(any(Product.class));
    }

    @Test
    void createProduct_AutoSetStatus() {
        testProduct.setStatus(null);
        testProduct.setStockQuantity(0);
        
        when(repository.save(any(Product.class))).thenAnswer(invocation -> {
            Product p = invocation.getArgument(0);
            assertEquals(Product.ProductStatus.OUT_OF_STOCK, p.getStatus());
            return p;
        });

        service.createProduct(testProduct);
        
        verify(repository, times(1)).save(any(Product.class));
    }

    @Test
    void getProduct_Found() {
        when(repository.findById(anyString())).thenReturn(Optional.of(testProduct));

        Product result = service.getProduct("prod-123");

        assertNotNull(result);
        assertEquals("Laptop", result.getName());
        verify(repository, times(1)).findById("prod-123");
    }

    @Test
    void getProduct_NotFound() {
        when(repository.findById(anyString())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            service.getProduct("non-existent");
        });
    }

    @Test
    void getAllProducts_Success() {
        List<Product> products = Arrays.asList(testProduct);
        when(repository.findAll()).thenReturn(products);

        List<Product> result = service.getAllProducts();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(repository, times(1)).findAll();
    }

    @Test
    void getProductsByCategory_Success() {
        List<Product> products = Arrays.asList(testProduct);
        when(repository.findByCategory(anyString())).thenReturn(products);

        List<Product> result = service.getProductsByCategory("Electronics");

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(repository, times(1)).findByCategory("Electronics");
    }

    @Test
    void updateProduct_Success() {
        when(repository.findById(anyString())).thenReturn(Optional.of(testProduct));
        when(repository.update(any(Product.class))).thenReturn(testProduct);

        Product result = service.updateProduct("prod-123", testProduct);

        assertNotNull(result);
        verify(repository, times(1)).update(any(Product.class));
    }

    @Test
    void updateProduct_AutoUpdateStatusWhenOutOfStock() {
        testProduct.setStockQuantity(0);
        
        when(repository.findById(anyString())).thenReturn(Optional.of(testProduct));
        when(repository.update(any(Product.class))).thenAnswer(invocation -> {
            Product p = invocation.getArgument(0);
            assertEquals(Product.ProductStatus.OUT_OF_STOCK, p.getStatus());
            return p;
        });

        service.updateProduct("prod-123", testProduct);
        
        verify(repository, times(1)).update(any(Product.class));
    }

    @Test
    void adjustStock_Success() {
        when(repository.updateStock(anyString(), anyInt())).thenReturn(true);

        boolean result = service.adjustStock("prod-123", 10);

        assertTrue(result);
        verify(repository, times(1)).updateStock("prod-123", 10);
    }

    @Test
    void adjustStock_ProductNotFound() {
        when(repository.updateStock(anyString(), anyInt())).thenReturn(false);
        when(repository.findById(anyString())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            service.adjustStock("non-existent", 10);
        });
    }

    @Test
    void adjustStock_InsufficientStock() {
        when(repository.updateStock(anyString(), anyInt())).thenReturn(false);
        when(repository.findById(anyString())).thenReturn(Optional.of(testProduct));

        assertThrows(IllegalStateException.class, () -> {
            service.adjustStock("prod-123", 10);
        });
    }

    @Test
    void reduceStock_Success() {
        when(repository.updateStock(anyString(), anyInt())).thenReturn(true);

        boolean result = service.reduceStock("prod-123", 5);

        assertTrue(result);
        verify(repository, times(1)).updateStock("prod-123", -5);
    }

    @Test
    void increaseStock_Success() {
        when(repository.updateStock(anyString(), anyInt())).thenReturn(true);

        boolean result = service.increaseStock("prod-123", 10);

        assertTrue(result);
        verify(repository, times(1)).updateStock("prod-123", 10);
    }

    @Test
    void deleteProduct_Success() {
        when(repository.findById(anyString())).thenReturn(Optional.of(testProduct));
        doNothing().when(repository).deleteById(anyString());

        service.deleteProduct("prod-123");

        verify(repository, times(1)).deleteById("prod-123");
    }

    @Test
    void deleteProduct_NotFound() {
        when(repository.findById(anyString())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            service.deleteProduct("non-existent");
        });
    }

    @Test
    void getAvailableProducts_Success() {
        List<Product> products = Arrays.asList(testProduct);
        when(repository.findByStatus(Product.ProductStatus.ACTIVE)).thenReturn(products);

        List<Product> result = service.getAvailableProducts();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(repository, times(1)).findByStatus(Product.ProductStatus.ACTIVE);
    }
}