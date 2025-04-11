# Temu Finn - Backend

The backend service for Temu Finn marketplace, built with Kotlin and Spring Boot 3.

## Overview

This backend provides a robust API for the Temu Finn marketplace, handling user authentication, product management, messaging, and payment processing. It's built with Kotlin and Spring Boot 3, using MySQL for data storage.

## Features

- **RESTful API**: Comprehensive endpoints for all marketplace functionality
- **Authentication**: Secure JWT-based authentication system
- **Product Management**: Create, read, update, and delete product listings
- **Search Engine**: Advanced product search with filtering and sorting
- **Media Handling**: Upload and serve product images
- **WebSockets**: Real-time messaging between users
- **Vipps Integration**: Secure payment processing
- **Geospatial Support**: Location-based queries using MySQL spatial extensions

## Technology Stack

- **Kotlin**: Primary programming language
- **Spring Boot 3**: Application framework
- **JDK 21**: Java runtime
- **Spring Security**: Authentication and authorization
- **MySQL**: Database with spatial extensions
- **Flyway**: Database migrations
- **Docker**: Containerization for development and deployment
- **WebSockets**: Real-time communication (in progress)

## Getting Started

### Prerequisites

- JDK 21
- Docker and Docker Compose

### Quick Start

The project includes a Makefile that simplifies common operations:

```bash
# Start application and database
make

# Reset database and start fresh
make fresh

# Stop the application
make down

# Run tests
make test
```

### Database

The application uses MySQL with geospatial extensions. Database migrations are handled by Flyway and automatically applied on startup.

```bash
# Start only the database
make db
```

### API Documentation

The API is documented using OpenAPI 3.0 and Swagger UI.

- Swagger UI: http://localhost:8080/swagger-ui/index.html
- OpenAPI JSON: http://localhost:8080/v3/api-docs

## Authentication

The service uses Spring Security with JWT tokens for authentication.

### Register

```http
POST /api/auth/register
Content-Type: application/json

{
    "email": "user@example.com",
    "name": "User Name",
    "password": "password"
}
```

### Login

```http
POST /api/auth/login
Content-Type: application/json

{
    "email": "user@example.com",
    "password": "password"
}
```

The response includes a JWT token to be used in subsequent requests:

```http
Authorization: Bearer {token}
```

## Project Structure

```
backend/
├── src/
│   ├── main/
│   │   ├── kotlin/
│   │   │   └── edu/ntnu/idatt2105/gr2/backend/
│   │   │       ├── configs/         # Configuration classes
│   │   │       ├── controllers/     # REST controllers
│   │   │       ├── dto/             # Data transfer objects
│   │   │       ├── exception/       # Exception handling
│   │   │       ├── model/           # Domain models
│   │   │       ├── repository/      # Data access
│   │   │       ├── service/         # Business logic
│   │   │       └── Application.kt   # Application entry point
│   │   └── resources/
│   │       ├── application.properties  # Application configuration
│   │       └── db/migration/           # Flyway migrations
│   └── test/
│       ├── kotlin/                     # Test classes
│       └── resources/                  # Test resources
├── docker-compose.yml                  # Docker configuration
├── Dockerfile                          # Application container definition
└── Makefile                            # Common commands
```

## Development Guidelines

- Use Kotlin idioms and follow best practices
- Write unit and integration tests for critical functionality
- Use DTOs for API requests and responses
- Document API endpoints with OpenAPI annotations
- Implement proper error handling and validation