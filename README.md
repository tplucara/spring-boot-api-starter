# API Starterpack — Showcase Version

Production-ready backend API starter built with **Java 21** and **Spring Boot 3**, demonstrating Clean Architecture, JWT authentication, and modern development practices.

> **Note:** This is a **showcase version** designed to demonstrate architecture, code quality, and local development setup. Full production deployment (AWS ECS, CI/CD pipelines, infrastructure-as-code) is available as a service.

---

## Architecture

The project follows **Clean Architecture** with a feature-based modular structure:

```
Controller → UseCase → Repository → Database
```

- **Controllers** handle HTTP concerns only
- **UseCases** contain all business logic
- **Repositories** handle persistence via Spring Data JPA
- **DTOs** (Java Records) are used for all API input/output

### Modules

| Module   | Description                        |
|----------|------------------------------------|
| `auth`   | JWT authentication, refresh tokens |
| `user`   | User registration and retrieval    |
| `health` | Application health check           |

---

## Tech Stack

- Java 21
- Spring Boot 3.3
- Spring Security + JWT (jjwt)
- Spring Data JPA
- PostgreSQL 16
- Flyway Migrations
- SpringDoc OpenAPI (Swagger)
- Spring Boot Actuator + Prometheus
- Docker &amp; Docker Compose
- JUnit 5 + Testcontainers + Mockito
- GitHub Actions CI

---

## Getting Started

### Prerequisites

- Docker and Docker Compose
- Java 21 (for local development without Docker)
- Maven 3.9+ (for local development without Docker)

### Run with Docker Compose

```bash
docker-compose up
```

The API will be available at `http://localhost:8080`.

### Run Locally (development)

Start only PostgreSQL:

```bash
docker-compose up postgres
```

Then run the application:

```bash
mvn spring-boot:run
```

---

## API Endpoints

### Authentication

| Method | Endpoint         | Description              | Auth Required |
|--------|------------------|--------------------------|---------------|
| POST   | `/auth/login`    | Authenticate user        | No            |
| POST   | `/auth/refresh`  | Refresh access token     | No            |
| POST   | `/auth/logout`   | Revoke refresh token     | No            |

### Users

| Method | Endpoint        | Description              | Auth Required |
|--------|-----------------|--------------------------|---------------|
| POST   | `/users`        | Register new user        | No            |
| GET    | `/users/me`     | Get current user         | Yes           |
| GET    | `/users/{id}`   | Get user by ID           | Yes           |

### Health

| Method | Endpoint   | Description          | Auth Required |
|--------|------------|----------------------|---------------|
| GET    | `/health`  | Application health   | No            |

### Monitoring

| Endpoint               | Description        |
|------------------------|--------------------|
| `/actuator/health`     | Health check       |
| `/actuator/info`       | App info           |
| `/actuator/metrics`    | Metrics            |
| `/actuator/prometheus` | Prometheus metrics  |

### Documentation

| Endpoint           | Description    |
|--------------------|----------------|
| `/swagger-ui.html` | Swagger UI     |
| `/v3/api-docs`     | OpenAPI spec   |

---

## Project Structure

```
src/main/java/com/example/api/
├── auth/
│   ├── controller/     # Auth endpoints
│   ├── usecase/        # Login, Refresh, Logout logic
│   ├── repository/     # Refresh token persistence
│   ├── dto/            # Request/Response records
│   └── entity/         # RefreshToken JPA entity
├── user/
│   ├── controller/     # User endpoints
│   ├── usecase/        # CreateUser, GetUser logic
│   ├── repository/     # User persistence
│   ├── dto/            # Request/Response records
│   ├── entity/         # User JPA entity
│   └── mapper/         # Entity-to-DTO mapping
├── health/
│   └── controller/     # Health endpoint
└── shared/
    ├── config/         # Security, JWT, OpenAPI config
    ├── security/       # JWT provider, auth filter
    └── exception/      # Global exception handling
```

---

## Testing

Run all tests:

```bash
mvn verify
```

- **Unit tests** cover all UseCases using Mockito
- **Integration tests** use Testcontainers with PostgreSQL

---

## License

This project is a showcase. Full production deployment is available as a service — contact for details.
