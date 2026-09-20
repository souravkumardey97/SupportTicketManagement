# Research: Support Ticket Management System

**Feature**: `001-support-ticket-management`  
**Date**: 2026-09-19

## 1. Authentication & authorization

**Decision**: Spring Security 6 with **stateless JWT** for the React SPA; role claims map to `ADMIN` and `USER` enums; all `/api/v1/tickets/**` and ticket comment routes require `ROLE_ADMIN`.

**Rationale**: Clear separation for REST + SPA; easy to test with MockMvc and integration tests; `403` for USER role on ticket APIs matches FR-002/FR-003.

**Alternatives considered**: Server-side HTTP session cookies (simpler CSRF story but harder cross-origin); API keys (not suitable for browser UI).

## 2. Default ADMIN bootstrap

**Decision**: On application startup, an `ApplicationRunner` (or `@Transactional` startup component) checks `SELECT COUNT(*) FROM users WHERE role = 'ADMIN'`. If zero, create one ADMIN from configuration (`APP_BOOTSTRAP_ADMIN_USERNAME`, `APP_BOOTSTRAP_ADMIN_PASSWORD`) with BCrypt-encoded password. If no USER exists, seed one demo **USER** assignee (`APP_BOOTSTRAP_USER_USERNAME`) so ticket creation is testable immediately.

**Rationale**: Meets requirement to guarantee at least one ADMIN without manual SQL; idempotent on restart; credentials from environment (not hardcoded in Git).

**Alternatives considered**: Flyway SQL seed only (fails if password rotation needed); manual shell script only (user explicitly asked for startup check).

**Supplement**: Optional `scripts/create-admin-user.sh` documented in quickstart for ops (calls documented bootstrap env vars or documents re-run after DB wipe).

## 3. API style & versioning

**Decision**: REST under `/api/v1`; plural resources; OpenAPI 3 via springdoc-openapi; DTOs + Jakarta Bean Validation; unified error body (`timestamp`, `status`, `error`, `message`, `path`, optional `fieldErrors`).

**Rationale**: Aligns with constitution and `.cursor/rules/api-standards.md`.

**Alternatives considered**: GraphQL (out of scope for MVP).

## 4. Priority & status models

**Decision**: Enums `TicketPriority { P0, P1, P2 }` (default **P1** on create when omitted); `TicketStatus { OPEN, IN_PROGRESS, RESOLVED, CLOSED, CANCELLED }`; status changes only via `PATCH /api/v1/tickets/{id}/status` with transition rules in a dedicated domain service.

**Rationale**: Matches spec FR-014–FR-017; keeps field updates separate from lifecycle (FR-007).

## 5. Search & filter

**Decision**: `GET /api/v1/tickets?keyword=&status=&page=&size=&sort=createdAt,desc`. Keyword: case-insensitive `ILIKE` on title and description (partial match). Empty keyword: no text filter. Status omitted: no status filter. Both may be combined.

**Rationale**: Resolves spec review ambiguities; pagination per API standards.

## 6. Persistence

**Decision**: PostgreSQL; Spring Data JPA; schema migrations with **Flyway**; `Instant` timestamps (UTC) in API as ISO-8601.

**Rationale**: User-mandated stack; Flyway gives repeatable schema for restart/persistence tests.

## 7. Frontend

**Decision**: React 18 + Vite + TypeScript; React Router; fetch/axios client with JWT in `Authorization: Bearer`; centralized API error parsing for meaningful UI messages.

**Rationale**: User-mandated React; TypeScript reduces contract drift with OpenAPI.

## 8. Testing

**Decision**: JUnit 5, Mockito, `@WebMvcTest`, `@SpringBootTest` + MockMvc; Testcontainers PostgreSQL for integration and state-machine matrix tests.

**Rationale**: Matches `.cursor/rules/testing.md` and constitution.

## 9. Field validation limits (planning defaults)

**Decision**: Title max 200 chars; description max 5000; comment body max 2000; all required non-blank after trim.

**Rationale**: Spec requires validation but not lengths; needed for implementation and tests.

## 10. Closed/cancelled tickets

**Decision**: ADMIN may still update fields and add comments on **CLOSED** and **CANCELLED** tickets unless product forbids—spec silent; allow updates for operational corrections; status still governed by state machine only.

**Rationale**: Avoid blocking support workflows; status transitions remain strict.
