import type { ApiError } from '../types/api'

interface ErrorBannerProps {
  error: unknown
  onDismiss?: () => void
}

function formatError(error: unknown): { title: string; details: string[] } {
  if (error && typeof error === 'object' && 'message' in error) {
    const apiError = error as ApiError
    const details: string[] = []
    if (apiError.fieldErrors) {
      details.push(
        ...Object.entries(apiError.fieldErrors).map(
          ([field, message]) => `${field}: ${message}`,
        ),
      )
    }
    const hasFieldErrors = details.length > 0
    const title =
      apiError.error === 'VALIDATION_ERROR' || hasFieldErrors
        ? apiError.message || 'Request validation failed'
        : apiError.error
          ? `${apiError.error}: ${apiError.message}`
          : apiError.message
    return { title, details }
  }
  if (error instanceof Error) {
    return { title: error.message, details: [] }
  }
  return { title: 'Something went wrong', details: [] }
}

export function ErrorBanner({ error, onDismiss }: ErrorBannerProps) {
  if (!error) {
    return null
  }
  const { title, details } = formatError(error)
  return (
    <div className="error-banner" role="alert">
      <div>
        <strong>{title}</strong>
        {details.length > 0 && (
          <ul>
            {details.map((detail) => (
              <li key={detail}>{detail}</li>
            ))}
          </ul>
        )}
      </div>
      {onDismiss && (
        <button type="button" className="link-button" onClick={onDismiss}>
          Dismiss
        </button>
      )}
    </div>
  )
}
