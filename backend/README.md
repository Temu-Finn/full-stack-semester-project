# Semester Project - Backend
Backend is written in Kotlin using Spring Boot 3 framework.

### TL;DR

Start the application and database:

```bash
make
```

To reset the database and start fresh:

```bash
make fresh
```

To stop the application:

```bash
make down
```

### Authentication
Spring Security is used for authentication and `io.jsonwebtoken` is used for generating JWT tokens. We store both userId and email in the JWT to work smoothly with Spring Security's standard login flow. The `DaoAuthenticationProvider`, which handles email/password login, relies on `UserDetailsService` looking up users by email (the identifier used for authentication). To keep this consistent, our JWT filter also uses the email claim from the token to validate the user via `UserDetailsService`, meaning that the email must be stored in the JWT. Meanwhile, the userId is included as the JWT's subject, serving as the stable, primary identifier for the authenticated user throughout the application.

Authentication endpoints are found at `/api/auth` and are emitted by the authentication filter. For all other endpoints, the JWT is required to be sent in the `Authorization` header as a Bearer token.

#### Register

Registering a new user is done by sending a POST request to `/api/auth/register` with the following JSON body:

```json
{
    "email": "test@test.com",
    "name": "Test User",
    "password": "password"
}
```

The user will be created if the email is not already in use. The response will be a JWT token that can be used to access protected endpoints.

#### Login

Logging in is done by sending a POST request to `/api/auth/login` with the following JSON body:

```json
{
    "email": "test@test.com",
    "password": "password"
}
```

The user will be logged in if the email and password are correct. The response will be a JWT token that can be used to access protected endpoints.

### Database

The database is a MySQL database that is run locally using Docker. The connection details are stored in the `application.properties` file.

To run the database, run in the root directory:

```bash
make db
```

To stop the database, run in the root directory:

```bash
make down
```

To reset the database, run in the root directory:

```bash
make fresh
```

### API

The API is documented using OpenAPI 3.0 and swagger.

To view the API documentation, run the application and navigate to `http://localhost:8080/swagger-ui/index.html`.

To view the documentation as json, navigate to `http://localhost:8080/v3/api-docs`.

### Testing

To run the tests, run in the root directory:

```bash
make test
```