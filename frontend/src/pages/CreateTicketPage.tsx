import { useEffect, useState, type FormEvent } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { createTicket, TICKET_PRIORITIES } from '../api/ticketsApi'
import { listUsers } from '../api/usersApi'
import { ErrorBanner } from '../components/ErrorBanner'
import type { TicketPriority, UserSummary } from '../types/api'

export function CreateTicketPage() {
  const navigate = useNavigate()
  const [title, setTitle] = useState('')
  const [description, setDescription] = useState('')
  const [priority, setPriority] = useState<TicketPriority>('P1')
  const [assigneeId, setAssigneeId] = useState('')
  const [users, setUsers] = useState<UserSummary[]>([])
  const [error, setError] = useState<unknown>(null)
  const [submitting, setSubmitting] = useState(false)

  useEffect(() => {
    listUsers('USER')
      .then(setUsers)
      .catch((err) => setError(err))
  }, [])

  async function handleSubmit(event: FormEvent) {
    event.preventDefault()
    setError(null)
    setSubmitting(true)
    try {
      const ticket = await createTicket({
        title,
        description,
        priority,
        assigneeId: Number(assigneeId),
      })
      navigate(`/tickets/${ticket.id}`)
    } catch (err) {
      setError(err)
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <div className="stack">
      <div className="page-header">
        <h2>Create ticket</h2>
        <Link to="/tickets">Back to list</Link>
      </div>
      <ErrorBanner error={error} onDismiss={() => setError(null)} />
      <form onSubmit={handleSubmit} className="card form-grid">
        <label>
          Title
          <input value={title} onChange={(e) => setTitle(e.target.value)} required maxLength={200} />
        </label>
        <label>
          Description
          <textarea
            value={description}
            onChange={(e) => setDescription(e.target.value)}
            required
            rows={5}
            maxLength={5000}
          />
        </label>
        <label>
          Priority
          <select
            value={priority}
            onChange={(e) => setPriority(e.target.value as TicketPriority)}
          >
            {TICKET_PRIORITIES.map((value) => (
              <option key={value} value={value}>{value}</option>
            ))}
          </select>
        </label>
        <label>
          Assignee (USER)
          <select
            value={assigneeId}
            onChange={(e) => setAssigneeId(e.target.value)}
            required
          >
            <option value="">Select assignee</option>
            {users.map((user) => (
              <option key={user.id} value={user.id}>{user.username}</option>
            ))}
          </select>
        </label>
        <button type="submit" disabled={submitting}>
          {submitting ? 'Creating…' : 'Create ticket'}
        </button>
      </form>
    </div>
  )
}
