import { apiRequest } from './client'
import type { CreateUserRequest, Role, UserSummary } from '../types/api'

export function listUsers(role?: Role): Promise<UserSummary[]> {
  const query = role ? `?role=${role}` : ''
  return apiRequest<UserSummary[]>(`/users${query}`)
}

export function createUser(request: CreateUserRequest): Promise<UserSummary> {
  return apiRequest<UserSummary>('/users', {
    method: 'POST',
    body: JSON.stringify(request),
  })
}

export const USER_ROLES: Role[] = ['USER', 'ADMIN']
