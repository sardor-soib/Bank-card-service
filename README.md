# Bank Card Management Service

A Spring Boot REST API for managing bank cards, users, and transactions.
Authentication is delegated to **Auth0** (OAuth2 / JWT). The service acts as a
resource server — it validates tokens issued by Auth0 and maps them to local
database users.

## Requirements

- Java 17+
- Maven 3.8+
- Docker & Docker Compose
- An [Auth0](https://auth0.com) tenant

## Getting Access Tokens

Source your `.env` first, then run either command:

```bash
source .env
```

### Admin token (M2M — `client_credentials`)

Maps to the seeded user `admin@example.com` (`ROLE_ADMIN`, id=1).

```bash
curl -s -X POST "https://$AUTH0_DOMAIN/oauth/token" \
  -H "Content-Type: application/json" \
  -d "{
    \"grant_type\": \"client_credentials\",
    \"client_id\": \"$AUTH0_CLIENT_ID\",
    \"client_secret\": \"$AUTH0_CLIENT_SECRET\",
    \"audience\": \"$AUTH0_AUDIENCE\"
  }"
```

### User token (password grant)

Maps to the seeded user `user@example.com` (`ROLE_USER`, id=2).

```bash
curl -s -X POST "https://$AUTH0_DOMAIN/oauth/token" \
  -H "Content-Type: application/json" \
  -d "{
    \"grant_type\": \"password\",
    \"username\": \"$AUTH0_USER_EMAIL\",
    \"password\": \"$AUTH0_USER_PASSWORD\",
    \"client_id\": \"$AUTH0_CLIENT_ID\",
    \"client_secret\": \"$AUTH0_CLIENT_SECRET\",
    \"audience\": \"$AUTH0_AUDIENCE\",
    \"scope\": \"openid email\"
  }"
```

Both responses contain an `access_token` field. Copy only that value — not the
surrounding JSON — and pass it as the Bearer token:

```bash
curl http://localhost:8080/api/v1/users/me/cards \
  -H "Authorization: Bearer <access_token>"
```

---

## Environment Variables

Copy the example file and fill in your values:

```bash
cp .env.example .env
```

| Variable                     | Required | Description                                                |
|------------------------------|----------|------------------------------------------------------------|
| `SERVER_PORT`                | No       | HTTP port (default: `8080`)                                |
| `POSTGRES_DB`                | Yes      | PostgreSQL database name                                   |
| `POSTGRES_USER`              | Yes      | PostgreSQL username                                        |
| `POSTGRES_PASSWORD`          | Yes      | PostgreSQL password                                        |
| `POSTGRES_HOST`              | Yes      | PostgreSQL host (use `db` when running via Docker Compose) |
| `POSTGRES_PORT`              | Yes      | PostgreSQL port (default: `5432`)                          |
| `SPRING_DATASOURCE_URL`      | Yes      | Full JDBC URL, e.g. `jdbc:postgresql://db:5432/bankcards`  |
| `SPRING_DATASOURCE_USERNAME` | Yes      | Same as `POSTGRES_USER`                                    |
| `SPRING_DATASOURCE_PASSWORD` | Yes      | Same as `POSTGRES_PASSWORD`                                |
| `BANK_CARDS_PAN_HASH_SECRET` | Yes      | HMAC-SHA256 secret for PAN hashing — min 32 chars          |
| `AUTH0_DOMAIN`               | Yes      | Auth0 tenant domain, e.g. `your-tenant.eu.auth0.com`       |
| `AUTH0_AUDIENCE`             | Yes      | Auth0 API identifier, e.g. `https://bank-card-service`     |
| `AUTH0_CLIENT_ID`            | Tests    | M2M app Client ID — used to fetch the admin token          |
| `AUTH0_CLIENT_SECRET`        | Tests    | M2M app Client Secret                                      |
| `AUTH0_USER_EMAIL`           | Tests    | Test user email — used to fetch a user token               |
| `AUTH0_USER_PASSWORD`        | Tests    | Test user password                                         |

Generate `BANK_CARDS_PAN_HASH_SECRET`:

```bash
openssl rand -base64 32
```

---

## Running the Application

### Option A — Docker Compose (recommended)

Builds and starts both the app and PostgreSQL from your working tree:

```bash
docker compose up --build
```

The application starts on **http://localhost:8080** (or `SERVER_PORT`).
Liquibase migrations run automatically on first startup.

### Option B — Local Maven

Start only the database container:

```bash
docker compose up -d db
```

Then run the app (variables are loaded from `.env` automatically via
`spring.config.import`):

```bash
mvn spring-boot:run
```

---

## Seed Data

Liquibase seeds the following records on a fresh database:

| Table          | ID | Detail                                          |
|----------------|----|-------------------------------------------------|
| `users`        | 1  | `admin@example.com`, `ROLE_ADMIN`               |
| `users`        | 2  | `user@example.com`, `ROLE_USER`                 |
| `cards`        | 1  | VISA `**** **** **** 1111`, balance 5 000 USD   |
| `cards`        | 2  | Mastercard `**** **** **** 5559`, balance 0 USD |
| `transactions` | 1  | 500 USD DEBIT on card 1 → card 2                |
| `transactions` | 2  | 500 USD CREDIT on card 2                        |

The seeded users have no `sub` field set. On first login the JWT converter
links the token to the local user by matching the Auth0 `email` claim, then
persists the `sub` for future lookups.

---

## Roles

| Role    | Endpoints                                                   |
|---------|-------------------------------------------------------------|
| `ADMIN` | `GET/POST/PUT/DELETE /api/v1/users/**`                      |
|         | `GET/POST/PUT/DELETE /api/v1/cards/**`                      |
|         | `GET /api/v1/transactions/**`                               |
| `USER`  | `GET/PATCH/POST /api/v1/users/me/cards/**` (own cards only) |
|         | `GET /api/v1/transactions/me`                               |

Any token whose `sub` claim ends with `@clients` (Auth0 M2M convention) is
automatically granted `ROLE_ADMIN`. Regular user tokens are matched against the
local `users` table by `sub`, with an `email` claim fallback for first login.

---

## API Documentation

- **Swagger UI**: http://localhost:8080/swagger-ui/index.html
- **OpenAPI JSON**: http://localhost:8080/v3/api-docs

---

## Automated Tests — Postman Collections

Two ready-to-import collections are in the `docs/` directory.

### `docs/admin-collection.json` — ROLE_ADMIN endpoints

1. Import into Postman.
2. Open **Collection Variables** and set `token` to your M2M `access_token`.
3. All requests inherit `Authorization: Bearer {{token}}`.
4. "Create card" and "Create user" requests include a **Pre-request Script** that
   generates a unique PAN / email on every run — no manual edits required.

### `docs/user-collection.json` — ROLE_USER endpoints

1. Import into Postman.
2. Open **Collection Variables** and set `userToken` to your user `access_token`.
3. All requests inherit `Authorization: Bearer {{userToken}}`.

> **Important:** paste only the `access_token` string into the variable — not the
> full JSON response. The token ends before the `","` separator; everything from
> `","id_token":"...` onwards must be excluded.

---

## Project Structure

```
src/
├── main/
│   ├── java/com/example/bankcards/
│   │   ├── config/          # Auth0Properties, SecurityConfig, CustomUserDetails
│   │   ├── controller/      # REST controllers + RestExceptionHandler
│   │   ├── dto/             # Request/response DTOs
│   │   ├── entity/          # JPA entities (User, Card, Transaction)
│   │   ├── exception/       # ResourceNotFoundException
│   │   ├── repository/      # Spring Data repositories
│   │   ├── security/        # JwtToUserAuthenticationConverter, PanHashEncoder
│   │   ├── service/         # Business logic + CardExpirationScheduler
│   │   └── util/            # Enums, mappers
│   └── resources/
│       ├── application.yml
│       └── db/migration/    # Liquibase changelogs (001-007)
docs/
├── admin-collection.json    # Postman — ROLE_ADMIN endpoints
└── user-collection.json     # Postman — ROLE_USER endpoints
test-api.sh                  # End-to-end shell test script
docker-compose.yml           # PostgreSQL + app (builds from working tree)
.env.example                 # Environment variable template
```
