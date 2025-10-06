# DynamoDB CRUD Application

A production-ready Spring Boot application demonstrating DynamoDB CRUD operations using both AWS SDK v2 approaches:
- **Standard DynamoDB Client** for Customer management
- **DynamoDB Enhanced Client** for Order management

## 🏗️ Architecture

### Technology Stack
- **Spring Boot 3.2.1** - Application framework
- **Spring Modulith** - Modular monolith architecture
- **AWS SDK v2** - DynamoDB integration
- **Spring Cloud AWS 3.3.0** - AWS integration starter
- **Lombok** - Code generation
- **OpenAPI 3** - API documentation
- **Spring Boot Actuator** - Monitoring & health checks
- **Micrometer** - Metrics with Prometheus

### Modules
```
com.example.dynamodb
├── customer (Standard SDK - DynamoDbClient)
│   ├── api         - REST controllers
│   ├── domain      - Domain models
│   ├── repository  - Data access layer
│   └── service     - Business logic
├── order (Enhanced SDK - DynamoDbEnhancedClient)
│   ├── api         - REST controllers
│   ├── domain      - Domain models  
│   ├── repository  - Data access layer
│   └── service     - Business logic
├── product (Spring Cloud AWS - DynamoDbTemplate)
│   ├── api         - REST controllers
│   ├── domain      - Domain models  
│   ├── repository  - Data access layer
│   └── service     - Business logic
├── config          - Configuration
└── shared          - Shared utilities
    ├── exception   - Exception handling
    └── health      - Health indicators
```

## 🚀 Getting Started

### Prerequisites
- Java 25
- Maven 3.6+
- Docker & Docker Compose (for local DynamoDB)

### Local Development

#### 1. Start DynamoDB Local
```bash
docker-compose up dynamodb-local -d
```

#### 2. Run Application
```bash
mvn spring-boot:run
```

Or with Docker:
```bash
docker-compose up
```

#### 3. Access APIs
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **API Docs**: http://localhost:8080/api-docs
- **Health Check**: http://localhost:8080/actuator/health
- **Metrics**: http://localhost:8080/actuator/metrics
- **Prometheus**: http://localhost:8080/actuator/prometheus

## 📡 API Endpoints

### Customer API (Standard SDK - DynamoDbClient)
```http
POST   /api/v1/customers          - Create customer
GET    /api/v1/customers          - Get all customers
GET    /api/v1/customers/{id}     - Get customer by ID
PUT    /api/v1/customers/{id}     - Update customer
DELETE /api/v1/customers/{id}     - Delete customer
```

### Order API (Enhanced SDK - DynamoDbEnhancedClient)
```http
POST   /api/v1/orders             - Create order
GET    /api/v1/orders             - Get all orders
GET    /api/v1/orders?customerId= - Get orders by customer
GET    /api/v1/orders/{id}        - Get order by ID
PUT    /api/v1/orders/{id}        - Update order
PATCH  /api/v1/orders/{id}/status - Update order status
DELETE /api/v1/orders/{id}        - Delete order
```

### Product API (Spring Cloud AWS - DynamoDbTemplate)
```http
POST   /api/v1/products                        - Create product
GET    /api/v1/products                        - Get all products
GET    /api/v1/products?category=              - Get products by category
GET    /api/v1/products?status=                - Get products by status
GET    /api/v1/products/available              - Get available products
GET    /api/v1/products/out-of-stock           - Get out of stock products
GET    /api/v1/products/{id}                   - Get product by ID
PUT    /api/v1/products/{id}                   - Update product
PATCH  /api/v1/products/{id}/status            - Update product status
PATCH  /api/v1/products/{id}/stock/adjust      - Adjust stock (+ or -)
PATCH  /api/v1/products/{id}/stock/reduce      - Reduce stock
PATCH  /api/v1/products/{id}/stock/increase    - Increase stock
DELETE /api/v1/products/{id}                   - Delete product
```

## 💡 Examples

### Create Customer
```bash
curl -X POST http://localhost:8080/api/v1/customers \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john.doe@example.com",
    "firstName": "John",
    "lastName": "Doe",
    "phone": "+1234567890",
    "address": "123 Main St, City"
  }'
```

### Create Order
```bash
curl -X POST http://localhost:8080/api/v1/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": "customer-id-here",
    "productName": "Laptop",
    "quantity": 1,
    "totalAmount": 999.99,
    "status": "PENDING"
  }'
```

### Get All Customers
```bash
curl http://localhost:8080/api/v1/customers
```

### Update Order Status
```bash
curl -X PATCH "http://localhost:8080/api/v1/orders/{orderId}/status?status=SHIPPED"
```

### Create Product
```bash
curl -X POST http://localhost:8080/api/v1/products \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Laptop Dell XPS 15",
    "description": "High-performance laptop",
    "category": "Electronics",
    "price": 1499.99,
    "stockQuantity": 50,
    "manufacturer": "Dell",
    "status": "ACTIVE"
  }'
```

### Get Products by Category
```bash
curl "http://localhost:8080/api/v1/products?category=Electronics"
```

### Adjust Product Stock
```bash
curl -X PATCH "http://localhost:8080/api/v1/products/{productId}/stock/reduce?quantity=5"
```

## 🔧 Configuration

### Environment Variables
```properties
AWS_DYNAMODB_ENDPOINT=http://localhost:8000
AWS_REGION=us-east-1
AWS_ACCESS_KEY_ID=your-access-key
AWS_SECRET_ACCESS_KEY=your-secret-key
```

### Application Properties
Key configurations in `application.yml`:
- DynamoDB endpoint and credentials
- Table names configuration
- Actuator endpoints
- Logging levels
- Metrics export

## 🏭 Production Deployment

### AWS Deployment

#### 1. Update Configuration
```yaml
aws:
  dynamodb:
    endpoint: ""  # Leave empty for AWS DynamoDB
    region: us-east-1
    # Use IAM roles instead of access keys
```

#### 2. Build Application
```bash
mvn clean package -DskipTests
```

#### 3. Deploy to ECS/EKS
Use the provided Dockerfile for containerized deployment.

### IAM Permissions Required
```json
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Action": [
        "dynamodb:PutItem",
        "dynamodb:GetItem",
        "dynamodb:UpdateItem",
        "dynamodb:DeleteItem",
        "dynamodb:Scan",
        "dynamodb:Query",
        "dynamodb:DescribeTable",
        "dynamodb:CreateTable"
      ],
      "Resource": [
        "arn:aws:dynamodb:*:*:table/customers",
        "arn:aws:dynamodb:*:*:table/orders"
      ]
    }
  ]
}
```

## 🧪 Testing

### Run Tests
```bash
mvn test
```

### Integration Tests
The application uses Testcontainers for integration testing with LocalStack.

## 📊 Monitoring

### Health Checks
- **Application**: `/actuator/health`
- **DynamoDB**: Custom health indicator checks connectivity

### Metrics
- Micrometer metrics exposed at `/actuator/metrics`
- Prometheus format at `/actuator/prometheus`
- Custom metrics for DynamoDB operations

## 🔐 Security Best Practices

1. **Never commit credentials** - Use environment variables or AWS IAM roles
2. **Use IAM roles** in production instead of access keys
3. **Enable encryption** at rest for DynamoDB tables
4. **Use VPC endpoints** for private connectivity
5. **Implement rate limiting** for API endpoints
6. **Enable audit logging** with CloudWatch

## 📖 Key Differences: Three Approaches Compared

### 1. Standard SDK - DynamoDbClient (Customer Module)
- **Manual mapping** between DynamoDB AttributeValue and Java objects
- **Fine-grained control** over operations
- More **verbose** code
- Better for **complex queries** and custom operations
- Direct access to low-level DynamoDB API

### 2. Enhanced SDK - DynamoDbEnhancedClient (Order Module)
- **Automatic mapping** using annotations
- **Type-safe** operations
- **Cleaner** and more concise code
- Better for **simple CRUD** operations
- Object-oriented approach

### 3. Spring Cloud AWS - DynamoDbTemplate (Product Module)
- **Spring-native** integration with Spring Boot
- **Highest level of abstraction**
- **Auto-configuration** support
- Seamless integration with Spring ecosystem
- Declarative table mapping with `@DynamoDbTableName`
- Built-in support for pagination and scan operations

| Feature | Standard SDK | Enhanced SDK | Spring Cloud AWS |
|---------|-------------|--------------|------------------|
| **Abstraction Level** | Low | Medium | High |
| **Code Verbosity** | High | Medium | Low |
| **Spring Integration** | Manual | Manual | Automatic |
| **Learning Curve** | Steep | Moderate | Easy |
| **Flexibility** | Maximum | High | Medium |
| **Best For** | Complex operations | Standard CRUD | Spring applications |

## 🛠️ Troubleshooting

### DynamoDB Connection Issues
```bash
# Test DynamoDB Local connectivity
aws dynamodb list-tables --endpoint-url http://localhost:8000
```

### View Logs
```bash
docker-compose logs -f app
```

### Reset Local Database
```bash
docker-compose down -v
docker-compose up
```

## 📚 Additional Resources

- [AWS SDK for Java v2](https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/)
- [DynamoDB Enhanced Client](https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/dynamodb-enhanced-client.html)
- [Spring Modulith Documentation](https://spring.io/projects/spring-modulith)
- [Spring Boot Actuator](https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html)

## 📝 License

This project is licensed under the MIT License.

## 👥 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.