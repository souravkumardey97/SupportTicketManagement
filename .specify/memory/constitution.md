# Support Ticket Management Constitution

## Core Principles

### I. Specification Before Build
Every feature starts with a written spec: actors, scenarios, and testable functional requirements. Planning (`plan.md`) must resolve ambiguities before implementation. Scope and acceptance criteria are fixed in the spec, not invented during coding.

### II. Client–Server Architecture
The system is a web application with a **backend** (API + persistence) and **frontend** (UI). The UI calls only documented HTTP APIs. Business rules and validation live on the server; the client may mirror validation for UX but must not be the sole enforcer.

### III. API Contracts
Public behavior is defined by versioned REST APIs (base path `/api/v1`), OpenAPI/Swagger documentation, consistent error shapes, and request/response DTOs—never raw persistence entities on the wire. Breaking API changes require coordinated updates to docs, clients, and tests.

### IV. Automated Tests for Changed Behavior
New or changed behavior MUST include automated tests appropriate to the layer (unit for services, API tests for controllers, integration for persistence and cross-layer flows). Critical domain rules (e.g. ticket status transitions) MUST have explicit positive and negative tests. Tests MUST be independent and runnable without manual setup.

### V. Simplicity (YAGNI)
Prefer the smallest design that meets the spec: at most **two** deployable applications (`backend/`, `frontend/`) unless documented in plan **Complexity Tracking**. Avoid extra patterns (generic repositories, event buses, microservices) until a concrete need is proven.

## Additional Constraints

- **Structure**: Use the web layout in the plan template (`backend/` + `frontend/`) unless the feature spec explicitly requires otherwise.
- **Standards**: Detailed API and testing conventions live in `.cursor/rules/api-standards.md` and `.cursor/rules/testing.md`; features must comply unless a justified exception is recorded in the plan.
- **Security**: No secrets in source control; validate all inputs on the server; do not expose stack traces, credentials, or tokens in API responses.
- **Data**: Use a real database in integration tests where persistence matters (e.g. Testcontainers for PostgreSQL when Docker is available).

## Development Workflow

1. **Spec** → **Plan** (constitution check gates Phase 0 and post-design) → **Tasks** → **Implement** → **Validate** (tests + quickstart).
2. Work on feature branches; keep changes scoped to the feature.
3. A feature is not complete until required tests pass and the plan’s quickstart scenarios succeed.

## Governance

This constitution defines non-negotiable gates for planning and delivery. If a feature cannot comply, document the violation, rationale, and rejected simpler options in the plan **Complexity Tracking** table before proceeding.

Amendments require updating this file’s version and dates, and syncing dependent templates per `.specify/memory/constitution_update_checklist.md`.

**Version**: 1.0.0 | **Ratified**: 2026-09-19 | **Last Amended**: 2026-09-19
