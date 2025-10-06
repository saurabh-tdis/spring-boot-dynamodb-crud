.PHONY: help build test run clean docker-up docker-down docker-build docker-logs

help: ## Display this help message
	@echo "Available commands:"
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | sort | awk 'BEGIN {FS = ":.*?## "}; {printf "\033[36m%-20s\033[0m %s\n", $$1, $$2}'

build: ## Build the application
	./mvnw clean package -DskipTests

test: ## Run tests
	./mvnw test

integration-test: ## Run integration tests
	./mvnw verify

run: ## Run the application locally
	./mvnw spring-boot:run

clean: ## Clean build artifacts
	./mvnw clean

docker-up: ## Start all services with Docker Compose
	docker-compose up -d

docker-down: ## Stop all services
	docker-compose down

docker-build: ## Build Docker image
	docker-compose build

docker-logs: ## View Docker logs
	docker-compose logs -f

docker-restart: docker-down docker-up ## Restart all services

dynamodb-local: ## Start only DynamoDB Local
	docker-compose up -d dynamodb-local

create-tables: ## Create DynamoDB tables manually
	aws dynamodb create-table \
		--table-name customers \
		--attribute-definitions AttributeName=customerId,AttributeType=S \
		--key-schema AttributeName=customerId,KeyType=HASH \
		--billing-mode PAY_PER_REQUEST \
		--endpoint-url http://localhost:8000
	aws dynamodb create-table \
		--table-name orders \
		--attribute-definitions AttributeName=orderId,AttributeType=S \
		--key-schema AttributeName=orderId,KeyType=HASH \
		--billing-mode PAY_PER_REQUEST \
		--endpoint-url http://localhost:8000
	aws dynamodb create-table \
		--table-name products \
		--attribute-definitions \
			AttributeName=productId,AttributeType=S \
			AttributeName=category,AttributeType=S \
		--key-schema AttributeName=productId,KeyType=HASH \
		--global-secondary-indexes \
			'IndexName=category-index,KeySchema=[{AttributeName=category,KeyType=HASH}],Projection={ProjectionType=ALL}' \
		--billing-mode PAY_PER_REQUEST \
		--endpoint-url http://localhost:8000

list-tables: ## List DynamoDB tables
	aws dynamodb list-tables --endpoint-url http://localhost:8000

swagger: ## Open Swagger UI in browser
	@echo "Opening Swagger UI..."
	@open http://localhost:8080/swagger-ui.html || xdg-open http://localhost:8080/swagger-ui.html

health: ## Check application health
	curl http://localhost:8080/actuator/health | jq

metrics: ## View application metrics
	curl http://localhost:8080/actuator/metrics | jq

modulith-docs: ## Generate Spring Modulith documentation
	./mvnw test -Dtest=ModulithArchitectureTests

install: ## Install Maven wrapper
	mvn -N io.takari:maven:wrapper