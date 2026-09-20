# Data Model: Support Ticket Management System

**Feature**: `001-support-ticket-management`

## Entity relationship (logical)

```text
User (1) ──< (0..*) Ticket (assignee)
Ticket (1) ──< (0..*) Comment
User (1) ──< (0..*) Comment (author, ADMIN)
```

## `users`

| Column | Type | Constraints | Notes |
|--------|------|-------------|--------|
| id | BIGSERIAL | PK | |
| username | VARCHAR(100) | UNIQUE, NOT NULL | Login identifier |
| password_hash | VARCHAR(255) | NOT NULL | BCrypt; never exposed via API |
| role | VARCHAR(20) | NOT NULL | `ADMIN` or `USER` |
| created_at | TIMESTAMPTZ | NOT NULL | |
| updated_at | TIMESTAMPTZ | NOT NULL | |

**Rules**: At least one `ADMIN` must exist in production operation (enforced by startup bootstrap). Assignee references must point to `role = USER`.

## `tickets`

| Column | Type | Constraints | Notes |
|--------|------|-------------|--------|
| id | BIGSERIAL | PK | |
| title | VARCHAR(200) | NOT NULL | |
| description | TEXT | NOT NULL | Max 5000 in validation |
| priority | VARCHAR(2) | NOT NULL | `P0`, `P1`, `P2`; default `P1` |
| status | VARCHAR(20) | NOT NULL | Enum; default `OPEN` on create |
| assignee_id | BIGINT | FK → users.id, NOT NULL | Must be USER role |
| created_at | TIMESTAMPTZ | NOT NULL | |
| updated_at | TIMESTAMPTZ | NOT NULL | |

**Indexes**: `status`; `assignee_id`; full-text or `lower(title)`, `lower(description)` for search (implementation: `ILIKE` acceptable for MVP).

## `comments`

| Column | Type | Constraints | Notes |
|--------|------|-------------|--------|
| id | BIGSERIAL | PK | |
| ticket_id | BIGINT | FK → tickets.id, NOT NULL | |
| author_id | BIGINT | FK → users.id, NOT NULL | ADMIN in this feature |
| body | TEXT | NOT NULL | Max 2000 in validation |
| created_at | TIMESTAMPTZ | NOT NULL | Order comments ASC |

## Enumerations

### TicketStatus

`OPEN`, `IN_PROGRESS`, `RESOLVED`, `CLOSED`, `CANCELLED`

### TicketPriority

`P0` (highest), `P1`, `P2` (lowest)

### Role

`ADMIN`, `USER`

## State machine (ticket.status)

Allowed transitions:

| From | To |
|------|-----|
| OPEN | IN_PROGRESS, CANCELLED |
| IN_PROGRESS | RESOLVED, CANCELLED |
| RESOLVED | CLOSED |
| CLOSED | *(none)* |
| CANCELLED | *(none)* |

All other transitions → business error (`INVALID_STATE_TRANSITION`, HTTP 409).

## Validation summary

| Field | Rules |
|-------|--------|
| title | Not blank; ≤ 200 |
| description | Not blank; ≤ 5000 |
| priority | P0/P1/P2; default P1 on create if null |
| assignee | Required; user exists; role USER |
| comment.body | Not blank; ≤ 2000 |
| status (transition) | Target must be allowed from current state |
