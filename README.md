# 🚀 Production-Ready Spring Boot API Starter

Production-ready backend API built with **Java 21** and **Spring Boot 3**, designed to help teams launch scalable and maintainable backend systems faster.

This project demonstrates how to structure a real-world backend using **Clean Architecture**, secure authentication, and modern development practices.

> ⚠️ **Showcase Version**
> This repository demonstrates architecture, code quality, and local development setup.
> A full production-ready version (AWS, CI/CD pipelines, infrastructure, auto-scaling) is available as a service.

---

## 💡 What This Project Solves

Many backend projects fail due to poor structure, lack of scalability, and fragile deployment setups.

This starter provides a solid foundation with:

* Clear architecture and separation of concerns
* Production-ready authentication
* Database versioning with migrations
* Containerized development environment
* CI pipeline for reliability

---

## 🧱 Architecture

The project follows **Clean Architecture**:

```
Controller → UseCase → Repository → Database
```

* Controllers handle HTTP concerns only
* UseCases contain business logic
* Repositories manage persistence
* DTOs (Java Records) define API contracts

---

## 📦 Modules

| Module   | Description                         |
| -------- | ----------------------------------- |
| `auth`   | JWT authentication + refresh tokens |
| `user`   | User registration and retrieval     |
| `health` | Application health check            |

---

## ⚙️ Tech Stack

* Java 21
* Spring Boot 3
* Spring Security + JWT
* Spring Data JPA
* PostgreSQL
* Flyway Migrations
* Docker & Docker Compose
* GitHub Actions (CI)
* Testcontainers + JUnit

---

## ▶️ Running Locally

### With Docker

```bash
docker-compose up
```

API available at:

```
http://localhost:8080
```

---

### Local Development

```bash
docker-compose up postgres
mvn spring-boot:run
```

---

## 🔐 API Overview

### Auth

* `POST /auth/login`
* `POST /auth/refresh`
* `POST /auth/logout`

### Users

* `POST /users`
* `GET /users/me`
* `GET /users/{id}`

### Health

* `GET /health`

---

## 📊 Observability

* `/actuator/health`
* `/actuator/metrics`
* `/actuator/prometheus`

---

## 📁 Project Structure

```
auth/
user/
health/
shared/
```

Organized by feature and aligned with Clean Architecture principles.

---

## 🧪 Testing

```bash
mvn verify
```

* Unit tests for business logic
* Integration tests with Testcontainers

---

## 📌 Important Note

This repository is intentionally limited to:

* Local development setup
* Core backend architecture
* Basic CI pipeline

It does **not include**:

* Cloud infrastructure (AWS, ECS, Load Balancer)
* Full CI/CD deployment pipelines
* Production environment configuration

---

## 🚀 Need a Production-Ready Backend?

This project can be extended into a fully production-ready backend with:

* AWS ECS Fargate deployment
* Load balancing and auto-scaling
* Complete CI/CD pipelines
* Environment separation (dev/staging/prod)

If you need help implementing this in your project, feel free to reach out.

---

## 📄 License

MIT
