import { ApiError, type ErrorResponse } from '../types/api'

export async function parseApiError(response: Response): Promise<ApiError> {
  let body: ErrorResponse
  try {
    body = (await response.json()) as ErrorResponse
  } catch {
    body = {
      status: response.status,
      error: 'UNKNOWN_ERROR',
      message: response.statusText || 'Request failed',
    }
  }
  if (!body.status) {
    body.status = response.status
  }
  return new ApiError(body)
}
