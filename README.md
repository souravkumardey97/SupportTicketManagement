# Support Ticket Management System

Full-stack web app for support tickets: **Spring Boot 3** REST API, **PostgreSQL** (Flyway migrations), and **React** (Vite + TypeScript) UI.

Feature specification and implementation plan: [`specs/001-support-ticket-management/`](specs/001-support-ticket-management/).

## Features

- **Authentication** — JWT login; bootstrap **ADMIN** (and optional demo **USER** assignee) on first run when the database has no admin
- **Tickets** — create, list, view, update; required assignee; priorities P1–P4; status lifecycle (OPEN → IN_PROGRESS → RESOLVED → CLOSED, plus CANCELLED) with invalid transitions rejected
- **Comments** — add comments on tickets; shown in chronological order
- **Search & filter** — case-insensitive keyword search and status filter on the ticket list
- **Users (ADMIN)** — create additional USER accounts for assignment
- **Authorization** — only **ADMIN** may manage tickets; **USER** accounts are valid assignees but receive 403 on ticket APIs

## Prerequisites

- Java 21
- Maven 3.9+ (or `backend/mvnw`)
- Node.js 20+
- Docker (PostgreSQL for local dev; Testcontainers for `mvn verify`)

## Quick start

### 1. Database

PostgreSQL 16 with database name matching the JDBC URL (default below: `supportticketmanagement`):

```bash
docker run --name support-pg \
  -e POSTGRES_PASSWORD=postgres \
  -e POSTGRES_DB=supportticketmanagement \
  -p 5432:5432 \
  -d postgres:16
```

Schema is applied automatically on backend startup via **Flyway** (`backend/src/main/resources/db/migration/`).

### 2. Backend

Set environment variables (e.g. in `backend/.env` — do not commit secrets). `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD`, and `APP_JWT_SECRET` are required.

| Variable | Description | Local example |
|----------|-------------|---------------|
| `SPRING_DATASOURCE_URL` | JDBC URL | `jdbc:postgresql://localhost:5432/supportticketmanagement` |
| `SPRING_DATASOURCE_USERNAME` | DB user | `postgres` |
| `SPRING_DATASOURCE_PASSWORD` | DB password | `postgres` |
| `APP_JWT_SECRET` | JWT signing secret (≥ 32 characters) | *(set locally)* |
| `APP_BOOTSTRAP_ADMIN_USERNAME` | First admin if none exists | `admin` (default) |
| `APP_BOOTSTRAP_ADMIN_PASSWORD` | First admin password | *(required on empty DB)* |
| `APP_BOOTSTRAP_USER_USERNAME` | Demo USER assignee if none exists | `agent1` (default) |
| `APP_BOOTSTRAP_USER_PASSWORD` | Demo USER password | *(optional)* |

```bash
cd backend
./mvnw spring-boot:run
```

- API: `http://localhost:8080`
- OpenAPI JSON: `http://localhost:8080/api-docs`
- Swagger UI: `http://localhost:8080/swagger-ui.html`

See [`scripts/create-admin-user.sh`](scripts/create-admin-user.sh) for a short env-var reminder.

### 3. Frontend

```bash
cd frontend
npm install
npm run dev
```

App: `http://localhost:5173` — Vite proxies `/api` to the backend ([`frontend/vite.config.ts`](frontend/vite.config.ts)).

## Project layout

```text
backend/     Java 21 + Spring Boot API (JPA, Security, JWT, Flyway)
frontend/    React + TypeScript (Vite, React Router)
scripts/     Local setup helpers
specs/       Feature spec, plan, tasks, OpenAPI contract
```

## Tests

```bash
cd backend
./mvnw test          # unit, domain, service, and WebMvc tests (44 tests)
./mvnw verify        # also runs integration + persistence tests via Testcontainers (Docker required)
```

Integration tests live under `backend/src/test/java/com/supportticket/integration/`; repository integration tests under `.../persistence/`.

## Documentation

- [Quickstart validation checklist](specs/001-support-ticket-management/quickstart.md)
- [OpenAPI contract](specs/001-support-ticket-management/contracts/openapi.yaml)
