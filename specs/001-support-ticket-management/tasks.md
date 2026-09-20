# Tasks: Support Ticket Management System

**Input**: Design documents from `/home/sourav-kumar-dey/Documents/C2_practice/SupportTicketManagement/specs/001-support-ticket-management/`  
**Prerequisites**: plan.md, research.md, data-model.md, contracts/openapi.yaml, quickstart.md

**Base Java package**: `com.supportticket`  
**API base path**: `/api/v1` (see `contracts/openapi.yaml`)

## Execution Flow (main)
```
1. Load plan.md → tech stack: Java 21, Spring Boot 3.3, PostgreSQL, React/Vite
2. Load data-model.md → User, Ticket, Comment + enums
3. Load contracts/openapi.yaml → 8 endpoint groups
4. Generate dependency-ordered tasks (TDD: failing tests before implementation)
5. SUCCESS — execute T001 → T042 in order unless [P] noted
```

## Format: `[ID] [P?] Description`

- **[P]**: Safe to run in parallel (different files, no unfinished dependency on another [P] in same phase)

---

## Phase 3.1: Setup

- [x] **T001** Create `backend/pom.xml` and `backend/src/main/java/com/supportticket/SupportTicketApplication.java` with Spring Boot 3.3, Java 21, dependencies: web, validation, data-jpa, security, flyway, postgresql, springdoc-openapi-starter-webmvc-ui, jjwt (or equivalent), spring-boot-starter-test, testcontainers postgresql/junit-jupiter. Add Maven wrapper `backend/mvnw`.
- [x] **T002** [P] Scaffold `frontend/` with Vite + React 18 + TypeScript; add `react-router-dom`; configure `frontend/vite.config.ts` dev proxy `/api` → `http://localhost:8080`.
- [x] **T003** [P] Add repository root `README.md` (prereqs, env vars from quickstart.md), `.gitignore` (`target/`, `node_modules/`, `.env`, `dist/`), and `docker-compose.yml` for PostgreSQL 16 on port 5432 / database `support_tickets`.
- [x] **T004** Add `backend/src/main/resources/application.yml` (datasource, flyway, `app.bootstrap.admin.*`, `app.bootstrap.user.*`, JWT secret via env) and `backend/src/test/resources/application-test.yml` for tests.

---

## Phase 3.2: Tests First (TDD) — MUST FAIL BEFORE Phase 3.4 IMPLEMENTATION

**CRITICAL**: Run `./mvnw test` after this phase; tests should fail (missing beans/controllers) until Phase 3.4–3.5.

- [x] **T005** [P] `backend/src/test/java/com/supportticket/domain/TicketStatusTransitionServiceTest.java` — parameterized tests for all allowed transitions and forbidden transitions from `data-model.md` (pure unit test, no Spring).
- [x] **T006** [P] `backend/src/test/java/com/supportticket/api/AuthControllerWebMvcTest.java` — `POST /api/v1/auth/login` expects 200 + token shape on valid credentials, 401 on bad password (MockMvc + `@WebMvcTest`, security test config).
- [x] **T007** [P] `backend/src/test/java/com/supportticket/api/UserControllerWebMvcTest.java` — `GET /api/v1/users?role=USER` returns 200 for ADMIN, 403 for USER role principal.
- [x] **T008** [P] `backend/src/test/java/com/supportticket/api/TicketControllerListCreateWebMvcTest.java` — `GET /api/v1/tickets` (pagination query params), `POST /api/v1/tickets` (201, validation 400 for missing assignee/title).
- [x] **T009** [P] `backend/src/test/java/com/supportticket/api/TicketControllerDetailUpdateWebMvcTest.java` — `GET/PATCH /api/v1/tickets/{id}` (200/404).
- [x] **T010** [P] `backend/src/test/java/com/supportticket/api/TicketControllerStatusWebMvcTest.java` — `PATCH /api/v1/tickets/{id}/status` valid transition 200; invalid transition 409 + `INVALID_STATE_TRANSITION` error body shape.
- [x] **T011** [P] `backend/src/test/java/com/supportticket/api/CommentControllerWebMvcTest.java` — `POST /api/v1/tickets/{id}/comments` 201; 400 blank body; 404 unknown ticket.
- [x] **T012** [P] `backend/src/test/java/com/supportticket/api/GlobalExceptionHandlerWebMvcTest.java` — validation `fieldErrors`, not-found 404, forbidden 403 response JSON matches plan error contract.
- [x] **T013** [P] `backend/src/test/java/com/supportticket/integration/AbstractIntegrationTest.java` — Testcontainers PostgreSQL + `@SpringBootTest` + `@AutoConfigureMockMvc` base class.
- [x] **T014** [P] `backend/src/test/java/com/supportticket/integration/AuthAndAuthorizationIT.java` — bootstrap ADMIN login; USER token denied on `GET /api/v1/tickets` (403).
- [x] **T015** [P] `backend/src/test/java/com/supportticket/integration/TicketStateMachineIT.java` — full happy path OPEN→IN_PROGRESS→RESOLVED→CLOSED; OPEN→CANCELLED; reject CLOSED→OPEN (409).
- [x] **T016** [P] `backend/src/test/java/com/supportticket/integration/TicketSearchFilterIT.java` — case-insensitive keyword search; status filter; combined keyword+status.
- [x] **T017** [P] `backend/src/test/java/com/supportticket/integration/TicketPersistenceIT.java` — create ticket+comment, restart Spring context (or new `@DirtiesContext`), data still readable.
- [x] **T018** [P] `backend/src/test/java/com/supportticket/integration/AdminBootstrapIT.java` — empty DB gets one ADMIN on startup; second context start does not duplicate ADMIN.

---

## Phase 3.3: Data Layer & Domain (minimal to unblock services)

- [x] **T019** Add `backend/src/main/resources/db/migration/V1__init_schema.sql` for `users`, `tickets`, `comments` per `data-model.md` (FKs, indexes on `status`, `assignee_id`).
- [x] **T020** [P] `backend/src/main/java/com/supportticket/persistence/entity/UserEntity.java` with `Role` enum (`ADMIN`, `USER`).
- [x] **T021** [P] `backend/src/main/java/com/supportticket/persistence/entity/TicketEntity.java` with `TicketStatus`, `TicketPriority` enums and `@ManyToOne` assignee.
- [x] **T022** [P] `backend/src/main/java/com/supportticket/persistence/entity/CommentEntity.java` with ticket and author relations.
- [x] **T023** [P] `backend/src/main/java/com/supportticket/persistence/UserRepository.java` including `long countByRole(Role role)`.
- [x] **T024** [P] `backend/src/main/java/com/supportticket/persistence/TicketRepository.java` with case-insensitive keyword + optional status + `Pageable` query.
- [x] **T025** [P] `backend/src/main/java/com/supportticket/persistence/CommentRepository.java`.
- [x] **T026** Implement `backend/src/main/java/com/supportticket/domain/TicketStatusTransitionService.java` + `InvalidTicketStateTransitionException` (used by T005).

---

## Phase 3.4: Security, Bootstrap & API Infrastructure

- [x] **T027** `backend/src/main/java/com/supportticket/config/SecurityConfig.java` — JWT filter, BCrypt, permit `/api/v1/auth/login` + swagger, require `ROLE_ADMIN` for `/api/v1/tickets/**` and ticket comments, `ROLE_ADMIN` for `GET /api/v1/users`.
- [x] **T028** `backend/src/main/java/com/supportticket/security/JwtService.java`, `JwtAuthenticationFilter.java`, `CustomUserDetailsService.java` loading `UserEntity` by username.
- [x] **T029** `backend/src/main/java/com/supportticket/bootstrap/AdminUserBootstrapRunner.java` — if no ADMIN, create from `app.bootstrap.admin.*`; if no USER, seed `app.bootstrap.user.username` assignee; idempotent; never log passwords.
- [x] **T030** [P] Request/response DTOs under `backend/src/main/java/com/supportticket/api/dto/` matching `contracts/openapi.yaml` with Jakarta validation (title/description/comment limits, assigneeId required).
- [x] **T031** `backend/src/main/java/com/supportticket/exception/GlobalExceptionHandler.java` + `ErrorResponse` record (400/401/403/404/409).
- [x] **T032** [P] `backend/src/main/java/com/supportticket/config/OpenApiConfig.java` and `CorsConfig.java` for React dev origin.

---

## Phase 3.5: Services & Controllers (make tests pass)

- [x] **T033** `backend/src/main/java/com/supportticket/service/TicketService.java` — create (default priority P1), get, update fields, list with keyword/status/pageable; validate assignee is USER.
- [x] **T034** `backend/src/main/java/com/supportticket/service/CommentService.java` — add comment with ADMIN author from security context.
- [x] **T035** `backend/src/main/java/com/supportticket/service/UserService.java` — list users filtered by role for assignee picker.
- [x] **T036** `backend/src/main/java/com/supportticket/api/AuthController.java` — `POST /api/v1/auth/login`.
- [x] **T037** `backend/src/main/java/com/supportticket/api/UserController.java` — `GET /api/v1/users`.
- [x] **T038** `backend/src/main/java/com/supportticket/api/TicketController.java` — all ticket endpoints including `PATCH .../status` delegating to transition service.
- [x] **T039** `backend/src/main/java/com/supportticket/api/CommentController.java` (or ticket-nested controller) — `POST /api/v1/tickets/{ticketId}/comments`.
- [x] **T040** Run `cd backend && ./mvnw test` — fix failures until all backend unit, WebMvc, and integration tests pass.

---

## Phase 3.6: Frontend

- [x] **T041** [P] `frontend/src/types/api.ts` — types aligned with OpenAPI schemas (Ticket, Comment, ErrorResponse, Page).
- [x] **T042** [P] `frontend/src/api/client.ts` — fetch wrapper with JWT header; `frontend/src/api/parseApiError.ts` for `message` and `fieldErrors`.
- [x] **T043** `frontend/src/auth/AuthContext.tsx` — login, logout, token storage, expose role.
- [x] **T044** `frontend/src/pages/LoginPage.tsx` — form posting to login API; show auth errors.
- [x] **T045** `frontend/src/pages/TicketListPage.tsx` — list with pagination, debounced keyword search, status filter dropdown.
- [x] **T046** `frontend/src/pages/CreateTicketPage.tsx` — title, description, priority (default P1), assignee select from `GET /users?role=USER`; display validation errors.
- [x] **T047** `frontend/src/pages/TicketDetailPage.tsx` — view/edit PATCH fields, status actions for allowed transitions, comments list + add comment form.
- [x] **T048** `frontend/src/App.tsx` + router — protected routes; if USER role logs in, show meaningful not-authorized on 403 from ticket routes.
- [x] **T049** Wire `frontend/src/main.tsx`, basic layout/navigation between list, create, detail.

---

## Phase 3.7: Polish & Validation

- [ ] **T050** [P] `scripts/create-admin-user.sh` — document env vars and restart bootstrap (optional ops helper per plan).
- [ ] **T051** Execute manual checklist in `quickstart.md` end-to-end; fix gaps found.
- [x] **T052** [P] Add `backend/src/test/java/com/supportticket/service/TicketServiceTest.java` Mockito unit tests for assignee validation and default P1 (if not fully covered by IT).

---

## Dependencies

| Task | Blocked by |
|------|------------|
| T004 | T001 |
| T005–T018 | T001, T004 (skeleton tests may use `@WebMvcTest` without DB; IT needs T013 after T001) |
| T019–T026 | T001 |
| T027–T032 | T020–T026 (entities/repos), T005 (transition service) |
| T033–T039 | T027–T032, T019 |
| T040 | T033–T039 |
| T041–T049 | T040 (backend running API contract stable) |
| T051 | T049, T050 |
| T052 | T033 |

**Ordering rule**: Complete T005–T018 (failing) → T019–T039 (implementation) → T040 green → frontend T041–T049.

---

## Parallel Execution Examples

### Batch 1 — After T001 + T004 (contract/unit tests, different files)

```text
T005 TicketStatusTransitionServiceTest.java
T006 AuthControllerWebMvcTest.java
T007 UserControllerWebMvcTest.java
T008 TicketControllerListCreateWebMvcTest.java
T009 TicketControllerDetailUpdateWebMvcTest.java
T010 TicketControllerStatusWebMvcTest.java
T011 CommentControllerWebMvcTest.java
T012 GlobalExceptionHandlerWebMvcTest.java
```

### Batch 2 — After T013 base class

```text
T014 AuthAndAuthorizationIT.java
T015 TicketStateMachineIT.java
T016 TicketSearchFilterIT.java
T017 TicketPersistenceIT.java
T018 AdminBootstrapIT.java
```

### Batch 3 — Entities & repositories (after T019 Flyway)

```text
T020 UserEntity.java
T021 TicketEntity.java
T022 CommentEntity.java
T023 UserRepository.java
T024 TicketRepository.java
T025 CommentRepository.java
```

### Batch 4 — Frontend foundations (after T040)

```text
T041 frontend/src/types/api.ts
T042 frontend/src/api/client.ts + parseApiError.ts
```

### Batch 5 — Scaffold in parallel at start

```text
T002 frontend Vite scaffold
T003 README + docker-compose + .gitignore
```

---

## Validation Checklist

- [x] All OpenAPI paths covered by WebMvc or integration tests (T006–T011, T014–T017)
- [x] All entities have model tasks (T020–T022)
- [x] Tests (T005–T018) ordered before controller implementation (T036–T039)
- [x] Parallel tasks use distinct file paths
- [x] ADMIN bootstrap covered (T018, T029)
- [x] State machine + persistence covered (T005, T015, T017)
- [x] quickstart validation task (T051)

---

## Notes

- Verify `./mvnw test` fails after Phase 3.2 before implementing controllers.
- JWT secret and bootstrap passwords **only** via environment variables — never commit.
- Assignee must reference `USER` role; priority default **P1** when omitted on create.
- Do not mark feature complete until T040 and T051 pass.
