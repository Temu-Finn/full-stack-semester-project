# Temu Finn

Temu Finn is a modern marketplace application for buying and selling used items. The platform enables users to list products, search through listings with advanced filtering options, communicate with sellers/buyers, and complete transactions securely.

## Project Overview

This is a full-stack monorepo with:
- **Frontend**: Vue 3, TypeScript, Vite
- **Backend**: Kotlin, Spring Boot 3, MySQL
- **Features**: User authentication, product listings with media support, location-based search, in-app messaging, and Vipps payment integration

## Structure

The project is organized as a monorepo with two main components:

```
.
├── backend/         # Kotlin Spring Boot API
├── frontend/        # Vue 3 + TypeScript frontend
```

## Key Features

- **User Authentication**: Register, login, and session management
- **Product Management**: Create, view, update, and delete product listings
- **Media Support**: Upload and manage product images
- **Search & Filter**: Find products by location, category, and text search
- **Interactive Maps**: View products on a map with Mapbox integration
- **Real-time Messaging**: Chat with sellers/buyers through WebSockets
- **Vipps Integration**: Complete purchases securely with Vipps
- **Responsive Design**: Works on desktop and mobile devices
- **Internationalization**: Supports multiple languages (English and Norwegian)

## Getting Started

### Prerequisites

- Node.js (v18+) and pnpm
- JDK 21
- Docker and Docker Compose

### Setup and Run

#### Backend

```bash
cd backend
make db      # Starts the database
make run     # API
```

Additional backend commands:
- `make fresh` - Reset database and start fresh
- `make down` - Stop the application
- `make test` - Run tests

#### Frontend

```bash
cd frontend
pnpm install
pnpm dev      # Start development server
```

#### Environment Setup

1. Copy `frontend/.env.example` to `frontend/.env` and configure API endpoint and Mapbox token
2. Backend environment is managed through Docker Compose and application properties

## Development Notes

- Frontend runs on http://localhost:5173 (Vite default)
- Backend API runs on http://localhost:8080
- Swagger UI available at http://localhost:8080/swagger-ui/index.html

## License

This project was developed as part of the IDATT2105 Full Stack Development course.