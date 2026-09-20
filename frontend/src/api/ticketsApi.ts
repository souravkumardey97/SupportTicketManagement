import { apiRequest } from './client'
import type {
  Comment,
  CreateCommentRequest,
  CreateTicketRequest,
  LoginRequest,
  LoginResponse,
  Ticket,
  TicketDetail,
  TicketPage,
  TicketPriority,
  TicketStatus,
  UpdateTicketRequest,
} from '../types/api'

export function login(request: LoginRequest): Promise<LoginResponse> {
  return apiRequest<LoginResponse>('/auth/login', {
    method: 'POST',
    body: JSON.stringify(request),
  })
}

export function listTickets(params: {
  keyword?: string
  status?: TicketStatus | ''
  page: number
  size: number
}): Promise<TicketPage> {
  const search = new URLSearchParams()
  if (params.keyword) {
    search.set('keyword', params.keyword)
  }
  if (params.status) {
    search.set('status', params.status)
  }
  search.set('page', String(params.page))
  search.set('size', String(params.size))
  search.set('sort', 'createdAt,desc')
  return apiRequest<TicketPage>(`/tickets?${search.toString()}`)
}

export function createTicket(request: CreateTicketRequest): Promise<Ticket> {
  return apiRequest<Ticket>('/tickets', {
    method: 'POST',
    body: JSON.stringify(request),
  })
}

export function getTicket(ticketId: number): Promise<TicketDetail> {
  return apiRequest<TicketDetail>(`/tickets/${ticketId}`)
}

export function updateTicket(
  ticketId: number,
  request: UpdateTicketRequest,
): Promise<Ticket> {
  return apiRequest<Ticket>(`/tickets/${ticketId}`, {
    method: 'PATCH',
    body: JSON.stringify(request),
  })
}

export function updateTicketStatus(
  ticketId: number,
  status: TicketStatus,
): Promise<Ticket> {
  return apiRequest<Ticket>(`/tickets/${ticketId}/status`, {
    method: 'PATCH',
    body: JSON.stringify({ status }),
  })
}

export function addComment(
  ticketId: number,
  request: CreateCommentRequest,
): Promise<Comment> {
  return apiRequest<Comment>(`/tickets/${ticketId}/comments`, {
    method: 'POST',
    body: JSON.stringify(request),
  })
}

export const TICKET_PRIORITIES: TicketPriority[] = ['P0', 'P1', 'P2']

export const TICKET_STATUSES: TicketStatus[] = [
  'OPEN',
  'IN_PROGRESS',
  'RESOLVED',
  'CLOSED',
  'CANCELLED',
]

export function allowedTransitions(status: TicketStatus): TicketStatus[] {
  switch (status) {
    case 'OPEN':
      return ['IN_PROGRESS', 'CANCELLED']
    case 'IN_PROGRESS':
      return ['RESOLVED', 'CANCELLED']
    case 'RESOLVED':
      return ['CLOSED']
    default:
      return []
  }
}
