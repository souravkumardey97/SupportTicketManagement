# Quickstart: Support Ticket Management System

Validate the feature end-to-end after implementation.

## Prerequisites

- Java 21, Maven or Gradle
- Node.js 20+ (frontend)
- Docker (PostgreSQL + Testcontainers optional for tests)
- PostgreSQL 16 running locally

## Environment

Create `backend/.env` or export variables (do not commit secrets):

| Variable | Purpose | Example (local only) |
|----------|---------|----------------------|
| `SPRING_DATASOURCE_URL` | JDBC URL | `jdbc:postgresql://localhost:5432/support_tickets` |
| `SPRING_DATASOURCE_USERNAME` | DB user | `postgres` |
| `SPRING_DATASOURCE_PASSWORD` | DB password | `postgres` |
| `APP_BOOTSTRAP_ADMIN_USERNAME` | First ADMIN if none exists | `admin` |
| `APP_BOOTSTRAP_ADMIN_PASSWORD` | First ADMIN password | *(set locally)* |
| `APP_BOOTSTRAP_USER_USERNAME` | Demo USER assignee if none exists | `agent1` |

On first startup with an empty database, the application creates the ADMIN (and demo USER) when no ADMIN exists.

## 1. Start database

```bash
docker run --name support-pg -e POSTGRES_PASSWORD=postgres -e POSTGRES_DB=support_tickets -p 5432:5432 -d postgres:16
```

## 2. Start backend

```bash
cd backend
./mvnw spring-boot:run
```

Verify: `GET http://localhost:8080/swagger-ui.html` (or springdoc UI path).

## 3. Start frontend

```bash
cd frontend
npm install
npm run dev
```

Open the Vite dev URL (typically `http://localhost:5173`).

## 4. Manual validation checklist

| Step | Action | Expected |
|------|--------|----------|
| 1 | Login as bootstrap ADMIN | Success; JWT stored |
| 2 | Login as USER (`agent1`) | Login may succeed but ticket APIs return 403 with clear message |
| 3 | Create ticket with assignee `agent1`, priority P1 | 201; status OPEN |
| 4 | List tickets | Ticket visible |
| 5 | Open detail | All fields + empty or existing comments |
| 6 | Update title/description/priority/assignee | Changes persisted |
| 7 | Add comment | Appears in chronological order |
| 8 | Search keyword (mixed case) | Case-insensitive matches |
| 9 | Filter status OPEN | Only OPEN tickets |
| 10 | Transition OPEN → IN_PROGRESS → RESOLVED → CLOSED | Each step succeeds |
| 11 | On another ticket, OPEN → CANCELLED | Success |
| 12 | Attempt CLOSED → OPEN via API/UI | 409 + meaningful error |
| 13 | Create ticket without assignee | 400 validation error in UI |
| 14 | Stop backend and PostgreSQL, restart both | Data unchanged |
| 15 | Run backend test suite | Unit + integration + state-machine tests pass |

## 5. Restart persistence check

1. Note ticket ID and comment count.
2. `docker stop support-pg` and `docker start support-pg` (or restart Spring only if DB kept up).
3. Restart Spring Boot.
4. Reload ticket in UI — data must match step 1.

## 6. Bootstrap idempotency

1. Restart backend twice without wiping DB.
2. Confirm only one bootstrap ADMIN exists (no duplicate admins on each start).

## Optional: manual admin script

If documented in `scripts/create-admin-user.sh`, run only when DB was wiped and startup bootstrap is disabled—otherwise prefer startup bootstrap.
