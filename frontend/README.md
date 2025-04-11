# Temu Finn - Frontend

The frontend application for Temu Finn marketplace, built with Vue 3, TypeScript, and Vite.

## Overview

This Vue 3 application provides a responsive user interface for the Temu Finn marketplace where users can buy and sell used items. The frontend communicates with the Kotlin backend API and integrates with external services like Mapbox for location visualization and Vipps for payment processing.

## Features

- **User Authentication**: Register and login with secure JWT authentication
- **Product Management**: Create, browse, search, and purchase products
- **Interactive Maps**: View products on a map with location-based filtering
- **Real-time Chat**: Message sellers and buyers through WebSocket integration
- **Responsive Design**: Mobile-friendly interface that adapts to different screen sizes
- **Multilingual Support**: Switch between Norwegian and English interfaces
- **Secure Payments**: Complete purchases using Vipps integration

## Technology Stack

- **Vue 3**: Core framework with Composition API
- **TypeScript**: Type safety throughout the codebase
- **Vite**: Fast development server and optimized builds
- **Vue Router**: Client-side routing
- **Pinia**: State management
- **Vue I18n**: Internationalization
- **Mapbox GL**: Interactive maps
- **Cypress**: End-to-end testing
- **Vitest**: Unit testing

## Getting Started

### Prerequisites

- Node.js (v18 or later)
- pnpm package manager

### Installation

```sh
# Install dependencies
pnpm install
```

### Configuration

Create a `.env` file based on the `.env.example` template:

```
# API configuration
VITE_API_BASE_URL=http://localhost:8080/api

# Mapbox (required for maps functionality)
VITE_MAPBOX_TOKEN=your_mapbox_token_here
```

You'll need to obtain a Mapbox token from [mapbox.com](https://www.mapbox.com/) for the map features to work properly.

### Development

```sh
# Start the development server
pnpm dev
```

The application will be available at http://localhost:5173.

### Building for Production

```sh
# Type-check, compile and minify
pnpm build
```

### Testing

```sh
# Run unit tests
pnpm test:unit

# Run end-to-end tests in development mode
pnpm test:e2e:dev

# Run end-to-end tests against production build
pnpm build
pnpm test:e2e
```

### Linting

```sh
# Lint the codebase
pnpm lint
```

## Project Structure

```
frontend/
├── locales/             # Translation files (en.json, no.json)
├── public/              # Static assets
├── src/
│   ├── assets/          # Styles and images
│   ├── components/      # Vue components
│   ├── composables/     # Composable functions
│   ├── config/          # Configuration files
│   ├── router/          # Vue Router configuration
│   ├── service/         # API services
│   ├── stores/          # Pinia stores
│   ├── utils/           # Utility functions
│   ├── views/           # Page components
│   ├── App.vue          # Root component
│   └── main.ts          # Application entry point
├── cypress/             # End-to-end tests
└── test/                # Unit tests
```

## Development Guidelines

- Use TypeScript for all components and services
- Follow Vue 3 Composition API patterns
- Use Pinia for global state management
- Ensure all user-facing strings are localized
- Write tests for critical functionality
