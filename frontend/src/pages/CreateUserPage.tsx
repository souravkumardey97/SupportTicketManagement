import { useState, type FormEvent } from 'react'
import { Link } from 'react-router-dom'
import { createUser, USER_ROLES } from '../api/usersApi'
import { ErrorBanner } from '../components/ErrorBanner'
import type { Role } from '../types/api'

export function CreateUserPage() {
  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')
  const [role, setRole] = useState<Role>('USER')
  const [error, setError] = useState<unknown>(null)
  const [submitting, setSubmitting] = useState(false)
  const [successMessage, setSuccessMessage] = useState<string | null>(null)

  async function handleSubmit(event: FormEvent) {
    event.preventDefault()
    setError(null)
    setSuccessMessage(null)
    setSubmitting(true)
    try {
      const created = await createUser({
        username: username.trim(),
        password,
        role,
      })
      setSuccessMessage(`User "${created.username}" (${created.role}) was created.`)
      setUsername('')
      setPassword('')
      setRole('USER')
    } catch (err) {
      setError(err)
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <div className="stack">
      <div className="page-header">
        <h2>Create user</h2>
        <Link to="/tickets">Back to tickets</Link>
      </div>

      <ErrorBanner error={error} onDismiss={() => setError(null)} />

      {successMessage && (
        <p className="card success-banner" role="status">{successMessage}</p>
      )}

      <form onSubmit={handleSubmit} className="card form-grid">
        <label>
          Username
          <input
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            required
            maxLength={100}
            autoComplete="off"
          />
        </label>
        <label>
          Password
          <input
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
            minLength={8}
            maxLength={128}
            autoComplete="new-password"
          />
          <span className="muted">At least 8 characters</span>
        </label>
        <label>
          Role
          <select
            value={role}
            onChange={(e) => setRole(e.target.value as Role)}
            required
          >
            {USER_ROLES.map((value) => (
              <option key={value} value={value}>{value}</option>
            ))}
          </select>
        </label>
        <button type="submit" disabled={submitting}>
          {submitting ? 'Creating…' : 'Create user'}
        </button>
      </form>
    </div>
  )
}
