export type Role = 'ADMIN' | 'USER'

export type TicketStatus =
  | 'OPEN'
  | 'IN_PROGRESS'
  | 'RESOLVED'
  | 'CLOSED'
  | 'CANCELLED'

export type TicketPriority = 'P0' | 'P1' | 'P2'

export interface LoginRequest {
  username: string
  password: string
}

export interface LoginResponse {
  accessToken: string
  tokenType: string
  role: Role
}

export interface UserSummary {
  id: number
  username: string
  role: Role
}

export interface Ticket {
  id: number
  title: string
  description: string
  priority: TicketPriority
  status: TicketStatus
  assignee: UserSummary
  createdAt: string
  updatedAt: string
}

export interface Comment {
  id: number
  body: string
  author: UserSummary
  createdAt: string
}

export interface TicketPage {
  content: Ticket[]
  page: number
  size: number
  totalElements: number
  totalPages: number
}

export interface TicketDetail {
  ticket: Ticket
  comments: Comment[]
}

export interface CreateTicketRequest {
  title: string
  description: string
  priority?: TicketPriority
  assigneeId: number
}

export interface UpdateTicketRequest {
  title?: string
  description?: string
  priority?: TicketPriority
  assigneeId?: number
}

export interface CreateCommentRequest {
  body: string
}

export interface ErrorResponse {
  timestamp?: string
  status: number
  error: string
  message: string
  path?: string
  fieldErrors?: Record<string, string>
}

export class ApiError extends Error {
  status: number
  error: string
  fieldErrors?: Record<string, string>

  constructor(body: ErrorResponse) {
    super(body.message || 'Request failed')
    this.status = body.status
    this.error = body.error
    this.fieldErrors = body.fieldErrors
  }
}
