# Implementation Plan: Support Ticket Management System

**Branch**: `001-support-ticket-management` | **Date**: 2026-09-19 | **Spec**: [spec.md](./spec.md)  
**Input**: Feature specification from `/home/sourav-kumar-dey/Documents/C2_practice/SupportTicketManagement/specs/001-support-ticket-management/spec.md`

## Execution Flow (/plan command scope)
```
1. Load feature spec from Input path → SUCCESS
2. Technical Context filled (web app; no NEEDS CLARIFICATION)
3. Constitution Check → PASS (initial)
4. Phase 0 → research.md
5. Phase 1 → data-model.md, contracts/openapi.yaml, quickstart.md
6. Constitution Check → PASS (post-design)
7. Phase 2 task approach documented (tasks.md NOT created — use /tasks)
8. STOP — Ready for /tasks command
```

## Summary

Deliver a **web application** for support ticket management: **ADMIN** users manage tickets (CRUD, comments, case-insensitive search, status filter, lifecycle transitions) via a **React** UI backed by **Spring Boot (Java 21)** REST APIs and **PostgreSQL**. **USER** accounts exist as assignees only. On startup, ensure **at least one ADMIN** exists via an idempotent bootstrap component (env-driven credentials). Automated tests must cover the status state machine and persistence across restarts.

---

## Technical Context

**Language/Version**: Java 21 (backend), TypeScript/React 18 (frontend)  
**Primary Dependencies**: Spring Boot 3.3.x, Spring Web, Spring Data JPA, Spring Security, Flyway, springdoc-openapi, JJWT (or Spring Security OAuth2 Resource Server JWT); Vite, React Router, fetch/axios  
**Storage**: PostgreSQL 16  
**Testing**: JUnit 5, Mockito, Spring Boot Test, MockMvc, Testcontainers (PostgreSQL)  
**Target Platform**: Linux/macOS dev; browser for UI  
**Project Type**: web (backend + frontend)  
**Performance Goals**: Responsive UI for hundreds of tickets; list API p95 &lt; 500ms local  
**Constraints**: REST `/api/v1`; no secrets in Git; server-side validation; ADMIN-only ticket APIs  
**Scale/Scope**: Single team internal tool; paginated list (default size 20)

---

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

**Simplicity**:
- Deployable apps: **2** (`backend`, `frontend`)
- Using frameworks directly? **Yes** (Spring MVC, JPA, React — no custom web framework wrappers)
- API DTOs only at the HTTP boundary? **Yes** (entities not exposed)
- Avoiding patterns? **Yes** (no microservices; Spring Data repositories only for persistence)

**Architecture**:
- Client–server split clear? **Yes**
- OpenAPI/contract files planned? **Yes** — `contracts/openapi.yaml`
- Business rules enforced on server? **Yes** (state machine, RBAC, validation)

**Testing**:
- Layered automated tests planned? **Yes**
- API + integration coverage? **Yes**
- State transition matrix? **Yes** (parameterized integration tests)
- Real DB in integration tests? **Yes** (Testcontainers)
- Tests independent? **Yes**

**Security**:
- Server-side validation? **Yes** (Bean Validation)
- No secrets in repo; safe errors? **Yes**

**API versioning**:
- `/api/v1` with coordinated changes? **Yes**

**Observability** (minimal):
- Error logging with path/status? **Yes** (`@RestControllerAdvice` + SLF4J)
- Consistent error JSON? **Yes**

**Initial Constitution Check**: PASS  
**Post-Design Constitution Check**: PASS

---

## Project Structure

### Documentation (this feature)

```
specs/001-support-ticket-management/
├── plan.md
├── research.md
├── data-model.md
├── quickstart.md
├── contracts/openapi.yaml
└── tasks.md              # /tasks command (not created yet)
```

### Source Code (repository root)

```
backend/
├── pom.xml (or build.gradle)
├── src/main/java/.../supportticket/
│   ├── SupportTicketApplication.java
│   ├── config/          # Security, OpenAPI, CORS
│   ├── bootstrap/       # AdminUserBootstrapRunner
│   ├── domain/          # enums, state machine service
│   ├── persistence/     # entities, repositories
│   ├── api/             # controllers, DTOs, mappers
│   ├── service/         # ticket, comment, user services
│   └── exception/       # exceptions + RestControllerAdvice
├── src/main/resources/
│   ├── application.yml
│   └── db/migration/    # Flyway V1__...
└── src/test/java/       # unit, web, integration

frontend/
├── package.json
├── vite.config.ts
└── src/
    ├── api/             # client + error parsing
    ├── auth/            # login, token storage
    ├── pages/           # Login, TicketList, TicketDetail, CreateTicket
    ├── components/      # forms, status actions, error banner
    └── types/           # aligned with OpenAPI

scripts/
└── create-admin-user.sh # optional ops helper (documented)
```

**Structure Decision**: **Option 2 — Web application** (`backend/` + `frontend/`)

---

## Detailed Implementation Roadmap (Step-by-Step)

Execute in order unless noted `[P]` (parallel). Each step should end with a verifiable checkpoint (test or manual).

### Phase A — Repository & infrastructure (Steps 1–4)

| Step | Action | Checkpoint |
|------|--------|------------|
| **A1** | Create `backend/` Spring Boot 3 project (Java 21): web, validation, data-jpa, security, flyway, postgresql, springdoc-openapi, test, testcontainers. | `./mvnw verify` compiles (empty app). |
| **A2** | Create `frontend/` with Vite + React + TypeScript; configure proxy to `localhost:8080` for `/api`. | `npm run dev` serves shell page. |
| **A3** | Add root `README.md` sections: prerequisites, env vars, how to run backend/frontend/DB. Add `.gitignore` for `node_modules`, `target`, `.env`. | No secrets in tree. |
| **A4** | Add `docker-compose.yml` (optional) for PostgreSQL only. | DB accepts connections on 5432. |

### Phase B — Database & domain model (Steps 5–8)

| Step | Action | Checkpoint |
|------|--------|------------|
| **B1** | Flyway `V1__init_schema.sql`: tables `users`, `tickets`, `comments` per [data-model.md](./data-model.md). | Migration runs on startup. |
| **B2** | JPA entities + enums (`Role`, `TicketStatus`, `TicketPriority`) with relationships and `@PrePersist`/`@PreUpdate` timestamps. | Context loads without mapping errors. |
| **B3** | Spring Data repositories: `UserRepository`, `TicketRepository` (custom query for keyword + status + pageable), `CommentRepository`. | Repository slice test or `@DataJpaTest`. |
| **B4** | Implement `TicketStatusTransitionService` with explicit allowed map; throw domain exception on invalid transition. | Unit tests for full transition matrix (allow + deny). |

### Phase C — Security, auth & ADMIN bootstrap (Steps 9–12)

| Step | Action | Checkpoint |
|------|--------|------------|
| **C1** | `UserDetailsService` loading users by username; BCrypt password encoder. | — |
| **C2** | JWT issue on `POST /api/v1/auth/login`; filter validates JWT on protected routes; expose `ROLE_ADMIN` / `ROLE_USER`. | Login returns token; wrong password → 401. |
| **C3** | Security rules: permit login + swagger; `/api/v1/tickets/**` and comment routes require `ADMIN`; `/api/v1/users` GET requires `ADMIN`. | USER token → 403 on ticket list. |
| **C4** | **`AdminUserBootstrapRunner`** (`ApplicationRunner`): if `userRepository.countByRole(ADMIN) == 0`, create ADMIN from `app.bootstrap.admin.*` env properties (password encoded). If no USER exists, create demo USER assignee from `app.bootstrap.user.username` with random or configured password (not used for ticket UI). Log info only (no password in logs). Idempotent on restart. | Empty DB → one ADMIN; second start → no duplicate ADMIN. |
| **C5** | Document env vars in `application.yml` with placeholders; add `scripts/create-admin-user.sh` as **optional** fallback (prints instructions to set env and restart) — primary path remains startup bootstrap. | quickstart.md steps pass. |

### Phase D — Backend APIs (Steps 13–18)

| Step | Action | Checkpoint |
|------|--------|------------|
| **D1** | DTOs + validation annotations matching [contracts/openapi.yaml](./contracts/openapi.yaml). | — |
| **D2** | `TicketService`: create (default priority P1), get, update fields, list with keyword/status/pageable. | Service unit tests. |
| **D3** | `TicketController`: REST mapping under `/api/v1/tickets`; 201 on create, 404 not found. | `@WebMvcTest` per endpoint. |
| **D4** | `PATCH .../status` delegates to transition service; map exception → **409** `INVALID_STATE_TRANSITION`. | WebMvc tests for valid/invalid transitions. |
| **D5** | `CommentService` + `POST .../comments`; include author from security context (ADMIN). | Integration test: create ticket → add comment. |
| **D6** | `UserController`: `GET /api/v1/users?role=USER` for assignee dropdown. | Returns only USER role summaries. |
| **D7** | `@RestControllerAdvice`: map validation → 400 + `fieldErrors`; auth → 401/403; not found → 404. | Exception tests. |
| **D8** | Enable springdoc; verify OpenAPI matches contract file. | Swagger UI lists all endpoints. |

### Phase E — Backend tests (Steps 19–21)

| Step | Action | Checkpoint |
|------|--------|------------|
| **E1** | Unit tests: `TicketService`, transition service, validation edge cases. | `mvn test` green. |
| **E2** | `@SpringBootTest` + Testcontainers: full flows create → persist → search → filter → status path → invalid transition. | Integration suite green. |
| **E3** | Parameterized state-machine test matrix (all allowed + sample forbidden transitions from spec). | Meets success criterion for state-machine tests. |
| **E4** | Persistence test: create data, restart context or new container, read back same IDs. | FR-011 satisfied. |

### Phase F — Frontend (Steps 22–28)

| Step | Action | Checkpoint |
|------|--------|------------|
| **F1** | Login page → store JWT; attach `Authorization` header on API calls. | ADMIN can reach app shell. |
| **F2** | Ticket list: table/cards, pagination controls, search input (debounced), status filter dropdown. | Matches list API query params. |
| **F3** | Create ticket form: title, description, priority (default P1), assignee select (fetch USERs). | Validation errors from API shown in UI. |
| **F4** | Ticket detail: view/edit fields, save PATCH; show comments list. | Updates reflected after save. |
| **F5** | Status action buttons/dropdown driven by allowed transitions from current status (UX); still rely on API 409 for illegal moves. | Valid transitions work; invalid shows message. |
| **F6** | Add comment form on detail page. | New comment appears without full page reload (or refetch). |
| **F7** | Central `parseApiError()` for `message`, `fieldErrors`, `error` code — toast or inline banner. | No stack traces shown. |
| **F8** | Optional: route guard — if USER logs in, show “not authorized” when API returns 403. | Scenario #15 from spec. |

### Phase G — Final validation (Steps 29–30)

| Step | Action | Checkpoint |
|------|--------|------------|
| **G1** | Run full [quickstart.md](./quickstart.md) manual checklist. | All rows pass. |
| **G2** | Run `/tasks` generated implementation audit: lint, format, CI workflow (if added). | Ready to merge. |

---

## Phase 0: Outline & Research

Completed in [research.md](./research.md). All planning unknowns resolved (auth, bootstrap, search, pagination, field limits).

**Output**: research.md ✓

---

## Phase 1: Design & Contracts

| Artifact | Location | Status |
|----------|----------|--------|
| Data model | [data-model.md](./data-model.md) | ✓ |
| OpenAPI | [contracts/openapi.yaml](./contracts/openapi.yaml) | ✓ |
| Quickstart | [quickstart.md](./quickstart.md) | ✓ |

**Contract tests** (to implement in `/tasks` phase): one MockMvc test class per controller verifying status codes and JSON shape against OpenAPI examples; start **red** before service implementation.

**Agent context**: Run `bash .specify/scripts/bash/update-agent-context.sh cursor` after initial git commit (script requires valid `git` HEAD).

**Output**: Phase 1 complete ✓

---

## Phase 2: Task Planning Approach

*Executed by `/tasks` — not run during `/plan`.*

**Task generation strategy**:
- Load `.specify/templates/tasks-template.md`
- One task group per roadmap phase (A–G)
- Each OpenAPI path → contract/MockMvc test task **[P]**
- Each entity → JPA + Flyway task
- Bootstrap + security → dedicated tasks before ticket APIs
- Each acceptance scenario in spec → integration or E2E task
- Frontend page per major UI surface

**Ordering**: TDD — failing API tests before controllers/services; backend before frontend; state-machine tests before marking feature done.

**Estimated output**: 28–35 ordered tasks in `tasks.md`

---

## Phase 3+: Future Implementation

| Phase | Owner | Description |
|-------|--------|-------------|
| 3 | `/tasks` | Generate `tasks.md` |
| 4 | Implementation | Follow roadmap steps A→G |
| 5 | Validation | quickstart + CI tests |

---

## Complexity Tracking

No violations — two deployable apps only; standard Spring Data repositories.

| Violation | Why Needed | Simpler Alternative Rejected Because |
|-----------|------------|-------------------------------------|
| — | — | — |

---

## Progress Tracking

**Phase Status**:
- [x] Phase 0: Research complete (/plan command)
- [x] Phase 1: Design complete (/plan command)
- [x] Phase 2: Task planning complete (/plan command — approach only)
- [x] Phase 3: Tasks generated (/tasks command)
- [ ] Phase 4: Implementation complete
- [ ] Phase 5: Validation passed

**Gate Status**:
- [x] Initial Constitution Check: PASS
- [x] Post-Design Constitution Check: PASS
- [x] All NEEDS CLARIFICATION resolved (in research.md)
- [x] Complexity deviations documented (none)

---

*Based on Constitution v1.0.0 - See `.specify/memory/constitution.md`*
