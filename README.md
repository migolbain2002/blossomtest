# **Blossom Test API**

##  Overview

This project is a backend application built with **Java 22 and Spring Boot 3** that simulates an e-commerce flow as test dictated:

- User registration and authentication
- Product creation and management
- Order creation with multiple products
- Simulated payment process
- Querying order history

---

## ⚙️**Prerequisites**

Make sure you have the following installed:

- **Java 17** or higher
- **Maven 3.8+**
- **Git** (to clone the project)
- *(Optional)* **Postman** or **Swagger UI** for testing the API

---

## ⚙️**How to run the application locally**
It is necessary to have maven in system environment variables or else an IDE like IntelliJ can be used.

```bash
# 1. Clone the repository
git clone https://github.com/migolbain2002/blossomtest.git
cd blossom-store


# 2. Build and run the project
mvn spring-boot:run

By default, the application runs at:
http://localhost:8080

Swagger UI

Interactive API documentation is available at:
http://localhost:8080/swagger-ui/index.html

H2 In-memory database runs at:

http://localhost:8080/h2-console

```
---

# API Endpoints

## Authentication Endpoints

| Method | Endpoint | Description | Roles Required |
|--------|-----------|--------------|----------------|
| **POST** | `/auth/register` | Registers a new user in the system. | Public |
| **POST** | `/auth/login` | Logs in a user and returns a JWT token. | Public |

---

## Product Endpoints

| Method | Endpoint | Description | Roles Required |
|--------|-----------|--------------|----------------|
| **POST** | `/products/create` | Creates a new product. | ADMIN |
| **GET** | `/products/search` | Searches for products using dynamic filters. | ADMIN, CUSTOMER |
| **GET** | `/products/{id}` | Retrieves a product by its ID. | ADMIN, CUSTOMER |
| **PUT** | `/products/{id}` | Updates product information. | ADMIN |
| **DELETE** | `/products/{id}` | Deletes a product by ID. | ADMIN |

---

## Order Endpoints

| Method | Endpoint | Description | Roles Required |
|--------|-----------|--------------|----------------|
| **POST** | `/orders/create` | Creates a new order with one or more products. | ADMIN, CUSTOMER |
| **GET** | `/orders/{id}` | Retrieves an order by its ID. | ADMIN, CUSTOMER |

---

## Payment Endpoints

| Method | Endpoint | Description | Roles Required |
|--------|-----------|--------------|----------------|
| **POST** | `/payments/create` | Registers a new payment for an order. | ADMIN, CUSTOMER |

---

## User Endpoints

| Method | Endpoint | Description | Roles Required |
|--------|-----------|--------------|----------------|
| **GET** | `/users/{id}` | Retrieves user profile and associated orders. | ADMIN, CUSTOMER |

--- 
## Architecture Decisions

**Custom Dynamic Filters**
A flexible filtering system was implemented using Java Reflection to evaluate simple fields and apply custom logic for complex attributes such as price and basic attributes as the product name. This allows powerful, extensible search capabilities without hardcoding query conditions.

**Modular Organization**
The project follows a clean modular architecture, separating concerns into controllers, services, persistence, and utility classes. This ensures maintainability, scalability, and easier testing.

**Swagger UI & Validation**
Automatic API documentation was integrated using Swagger (OpenAPI 3). All request parameters and payloads are validated to ensure consistency and improve the developer experience during testing and integration.

**Unit Tests with Mocks**
Comprehensive unit tests were implemented for filtering logic and service layers using mocked data to ensure test independence from external systems or databases.

**JWT-Based Security Layer**
Authentication and authorization are handled through JWT (JSON Web Tokens). This ensures stateless and secure communication between client and server while allowing role-based access control (ADMIN, CUSTOMER).

**In-Memory Database for Testing**
Integration tests use an H2 in-memory database, simulating real persistence behavior while maintaining test isolation and fast execution.

**Consistent Response Wrapper**
All endpoints return a unified response structure via the ApiResponse helper, improving readability, standardization, and simplifying client-side handling.

**Transactional Integrity**
Critical operations such as order creation and payment processing are annotated with @Transactional to ensure database consistency in case of errors or rollbacks.
