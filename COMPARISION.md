# DynamoDB SDK Approaches - Comprehensive Comparison

This document provides a detailed comparison of three different approaches to interact with AWS DynamoDB in Spring Boot applications.

## 📚 Overview of Three Approaches

### 1. Standard AWS SDK v2 - DynamoDbClient
**Module**: Customer Management  
**Dependency**: `software.amazon.awssdk:dynamodb`

The low-level client that provides direct access to DynamoDB operations with complete control over requests and responses.

### 2. Enhanced Client - DynamoDbEnhancedClient
**Module**: Order Management  
**Dependency**: `software.amazon.awssdk:dynamodb-enhanced`

Object mapper built on top of the standard client, providing type-safe operations with annotation-based mapping.

### 3. Spring Cloud AWS - DynamoDbTemplate
**Module**: Product Management  
**Dependency**: `io.awspring.cloud:spring-cloud-aws-starter-dynamodb`

Spring-native abstraction that integrates seamlessly with Spring Boot's auto-configuration and ecosystem.

---

## 🔍 Detailed Feature Comparison

### Code Examples

#### **Creating an Entity**

**Standard SDK (Customer)**
```java
Map<String, AttributeValue> item = new HashMap<>();
item.put("customerId", AttributeValue.builder().s(customer.getCustomerId()).build());
item.put("email", AttributeValue.builder().s(customer.getEmail()).build());
item.put("firstName", AttributeValue.builder().s(customer.getFirstName()).build());

PutItemRequest request = PutItemRequest.builder()
    .tableName(tableName)
    .item(item)
    .build();

dynamoDbClient.putItem(request);
```

**Enhanced SDK (Order)**
```java
@DynamoDbBean
public class Order {
    private String orderId;
    
    @DynamoDbPartitionKey
    public String getOrderId() {
        return orderId;
    }
}

// Repository
table.putItem(order);
```

**Spring Cloud AWS (Product)**
```java
@DynamoDbBean
@DynamoDbTableName("products")
public class Product {
    private String productId;
    
    @DynamoDbPartitionKey
    public String getProductId() {
        return productId;
    }
}

// Repository
dynamoDbTemplate.save(product);
```

#### **Reading an Entity**

**Standard SDK**
```java
Map<String, AttributeValue> key = new HashMap<>();
key.put("customerId", AttributeValue.builder().s(customerId).build());

GetItemRequest request = GetItemRequest.builder()
    .tableName(tableName)
    .key(key)
    .build();

GetItemResponse response = dynamoDbClient.getItem(request);
Customer customer = mapToCustomer(response.item());
```

**Enhanced SDK**
```java
Key key = Key.builder()
    .partitionValue(orderId)
    .build();

Order order = table.getItem(key);
```

**Spring Cloud AWS**
```java
Key key = Key.builder()
    .partitionValue(productId)
    .build();

Product product = dynamoDbTemplate.load(key, Product.class);
```

#### **Scanning All Items**

**Standard SDK**
```java
ScanRequest request = ScanRequest.builder()
    .tableName(tableName)
    .build();

ScanResponse response = dynamoDbClient.scan(request);
List<Customer> customers = response.items().stream()
    .map(this::mapToCustomer)
    .collect(Collectors.toList());
```

**Enhanced SDK**
```java
List<Order> orders = table.scan().items().stream()
    .collect(Collectors.toList());
```

**Spring Cloud AWS**
```java
PageIterable<Product> pages = dynamoDbTemplate.scanAll(Product.class);
List<Product> products = pages.items().stream()
    .collect(Collectors.toList());
```

---

## 📊 Comparison Matrix

| Aspect | Standard SDK | Enhanced SDK | Spring Cloud AWS |
|--------|--------------|--------------|------------------|
| **Lines of Code (Save)** | ~20 lines | ~1 line | ~1 line |
| **Type Safety** | ❌ Manual | ✅ Compile-time | ✅ Compile-time |
| **Null Handling** | Manual checks | Automatic | Automatic |
| **Configuration** | Manual beans | Manual beans | Auto-configuration |
| **Spring Integration** | Manual | Manual | Native |
| **Annotation Support** | ❌ None | ✅ Basic | ✅ Enhanced |
| **Query Builder** | ✅ Full control | ✅ Type-safe | ✅ Type-safe |
| **Batch Operations** | ✅ Manual | ✅ Built-in | ✅ Built-in |
| **Transaction Support** | ✅ Full | ✅ Limited | ✅ Limited |
| **GSI Support** | ✅ Full | ✅ Annotations | ✅ Annotations |
| **Custom Logic** | ✅ Maximum | ⚠️ Limited | ⚠️ Limited |
| **Learning Curve** | 🔴 Steep | 🟡 Moderate | 🟢 Easy |
| **Debugging** | Complex | Moderate | Simple |
| **Performance** | ⚡ Fastest | ⚡ Fast | ⚡ Fast |
| **Boilerplate Code** | 🔴 High | 🟢 Low | 🟢 Low |

---

## 🎯 When to Use Each Approach

### Use Standard SDK When:
- ✅ You need **complete control** over DynamoDB operations
- ✅ Implementing **complex queries** with multiple conditions
- ✅ Working with **conditional writes** and transactions
- ✅ Optimizing for **maximum performance**
- ✅ Need access to **all DynamoDB features**
- ✅ Building a **generic data access layer**

**Real-world scenarios:**
- High-throughput transaction processing