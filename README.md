# Support Ticket Management System

Web application for managing support tickets: **Spring Boot** REST API, **PostgreSQL**, and **React** UI.

Feature specification and implementation plan live under [`specs/001-support-ticket-management/`](specs/001-support-ticket-management/).

## Prerequisites

- Java 21
- Maven 3.9+ (or use `backend/mvnw`)
- Node.js 20+
- Docker (for PostgreSQL)

## Quick start

### 1. Database

```bash
docker compose up -d
```

PostgreSQL listens on `localhost:5432` with database `support_tickets`, user `postgres`, password `postgres`.

### 2. Backend

Set environment variables (use a local `.env` file; do not commit secrets):

| Variable | Description | Local example |
|----------|-------------|---------------|
| `SPRING_DATASOURCE_URL` | JDBC URL | `jdbc:postgresql://localhost:5432/support_tickets` |
| `SPRING_DATASOURCE_USERNAME` | DB user | `postgres` |
| `SPRING_DATASOURCE_PASSWORD` | DB password | `postgres` |
| `APP_JWT_SECRET` | JWT signing secret (long random string) | *(set locally)* |
| `APP_BOOTSTRAP_ADMIN_USERNAME` | First admin if none exists | `admin` |
| `APP_BOOTSTRAP_ADMIN_PASSWORD` | First admin password | *(set locally)* |
| `APP_BOOTSTRAP_USER_USERNAME` | Demo assignee USER if none exists | `agent1` |
| `APP_BOOTSTRAP_USER_PASSWORD` | Demo USER password | *(optional for assignee-only)* |

```bash
cd backend
./mvnw spring-boot:run
```

API: `http://localhost:8080`  
Swagger UI: `http://localhost:8080/swagger-ui.html`

### 3. Frontend

```bash
cd frontend
npm install
npm run dev
```

Dev server proxies `/api` to the backend (see `frontend/vite.config.ts`).

## Project layout

```text
backend/     Java 21 + Spring Boot API
frontend/    React + TypeScript (Vite)
specs/       Feature spec, plan, tasks, contracts
```

## Tests

```bash
cd backend
./mvnw test          # unit + WebMvc contract tests (32 tests)
./mvnw verify        # also runs *IT integration tests (Docker + Testcontainers required)
```

Integration tests under `backend/src/test/java/.../integration/` are expected to fail until Phase 3.5 (real auth, persistence, bootstrap) is complete.

## Documentation

- [Quickstart validation checklist](specs/001-support-ticket-management/quickstart.md)
- [OpenAPI contract](specs/001-support-ticket-management/contracts/openapi.yaml)
