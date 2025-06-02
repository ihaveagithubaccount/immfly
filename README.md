# Order Management Platform API

A simple REST API for managing orders, products, and categories.

## Requirements

- Java 17
- Maven
- H2 Database (in-memory)

## Setup

1. Clone the repository
2. Build the project:
   mvn clean install
3. Run the application:
   mvn spring-boot:run

The application will start on port 8080.

## API Endpoints

### Products
- GET /api/products - List all products
- GET /api/products/{id} - Get product by ID
- POST /api/products - Create new product
- PUT /api/products/{id} - Update product
- DELETE /api/products/{id} - Delete product

### Categories
- GET /api/categories - List all categories
- GET /api/categories/{id} - Get category by ID
- POST /api/categories - Create new category
- PUT /api/categories/{id} - Update category
- DELETE /api/categories/{id} - Delete category

### Orders
- GET /api/orders - List all orders
- GET /api/orders/{id} - Get order by ID
- POST /api/orders - Create new order
- PUT /api/orders/{id} - Update order
- DELETE /api/orders/{id} - Cancel order

## H2 Console

The H2 database console is available at:
http://localhost:8080/h2-console

Database credentials:
- JDBC URL: jdbc:h2:mem:immflydb
- Username: sa
- Password: password


