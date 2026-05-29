# Bank Card Management Service

A Spring Boot REST API for managing bank cards, users, and transactions with JWT-based authentication and role-based
access control.

## Requirements

- Java 17+
- Maven 3.8+
- Docker & Docker Compose

## Quick Start

### 1. Start the database

```bash
docker compose up -d
```

This starts a PostgreSQL 17 container on port `5432`. Liquibase migrations run automatically on the first app startup.

### 2. Configure environment variables

Copy the example and fill in values:

```bash
cp .env.example .env
```

| Variable                     | Required | Description                                       | Example                                              |
|------------------------------|----------|---------------------------------------------------|------------------------------------------------------|
| `DB_URL`                     | Yes      | JDBC connection URL                               | `jdbc:postgresql://localhost:5432/bank-rest-service` |
| `DB_USERNAME`                | Yes      | Database username                                 | `postgres`                                           |
| `DB_PASSWORD`                | Yes      | Database password                                 | `12345`                                              |
| `BANK_CARDS_PAN_HASH_SECRET` | Yes      | HMAC-SHA256 secret for PAN hashing (min 32 chars) | `my-secret-pan-hash-key-32chars!!`                   |
| `JWT_SECRET`                 | No       | HMAC-SHA256 secret for JWT signing (min 32 chars) | `my-jwt-secret-key-at-least-32-ch`                   |

> `JWT_SECRET` has a built-in default and is optional for local development. Use a strong secret in production.

### 3. Run the application

```bash
mvn spring-boot:run \
  -Dspring-boot.run.jvmArguments="\
  -DDB_URL=jdbc:postgresql://localhost:5432/bank-rest-service \
  -DDB_USERNAME=postgres \
  -DDB_PASSWORD=12345 \
  -DBANK_CARDS_PAN_HASH_SECRET=my-secret-pan-hash-key-32chars!!"
```

Or export variables first:

```bash
export DB_URL=jdbc:postgresql://localhost:5432/bank-rest-service
export DB_USERNAME=postgres.yaml
export DB_PASSWORD=12345
export BANK_CARDS_PAN_HASH_SECRET=my-secret-pan-hash-key-32chars!!

mvn spring-boot:run
```

The application starts on **http://localhost:8080**.

## API Documentation

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI spec**: [`docs/openapi.yaml`](docs/openapi.yaml)

## Authentication

All endpoints except `/api/v1/auth/login` and `/api/v1/auth/register` require a Bearer token.

**Register:**

```bash
curl -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"user@example.com","password":"password123","fullName":"John Doe","phoneNumber":"+1234567890"}'
```

**Login:**

```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"user@example.com","password":"password123"}'
```

Both return:

```json
{
  "token": "<jwt>",
  "type": "Bearer",
  "id": 1,
  "email": "user@example.com",
  "role": "USER"
}
```

Use the token in requests:

```bash
curl http://localhost:8080/api/v1/users/me/cards \
  -H "Authorization: Bearer <jwt>"
```

## Roles

| Role    | Access                                                               |
|---------|----------------------------------------------------------------------|
| `ADMIN` | Full access: manage all cards, users, transactions                   |
| `USER`  | Own cards only: view, search, request block, transfer, check balance |

## Running Tests

```bash
mvn test
```

## Project Structure

```
src/
├── main/
│   ├── java/com/example/bankcards/
│   │   ├── config/          # Security, JWT, OpenAPI configuration
│   │   ├── controller/      # REST controllers
│   │   ├── dto/             # Request/response DTOs
│   │   ├── entity/          # JPA entities
│   │   ├── repository/      # Spring Data repositories
│   │   ├── security/        # JWT converter, PAN hashing
│   │   ├── service/         # Business logic
│   │   └── util/            # Enums, mappers
│   └── resources/
│       ├── application.yml
│       └── db/migration/    # Liquibase changelogs
docs/
└── openapi.yaml             # Full OpenAPI 3.0 specification
docker-compose.yml           # PostgreSQL dev environment
```