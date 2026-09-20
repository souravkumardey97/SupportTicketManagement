import { useCallback, useEffect, useState, type FormEvent } from 'react'
import { Link, useParams } from 'react-router-dom'
import {
  addComment,
  allowedTransitions,
  getTicket,
  TICKET_PRIORITIES,
  updateTicket,
  updateTicketStatus,
} from '../api/ticketsApi'
import { listUsers } from '../api/usersApi'
import { ErrorBanner } from '../components/ErrorBanner'
import {
  ApiError,
  type Comment,
  type Ticket,
  type TicketPriority,
  type TicketStatus,
  type UserSummary,
} from '../types/api'

export function TicketDetailPage() {
  const { ticketId } = useParams()
  const id = Number(ticketId)

  const [ticket, setTicket] = useState<Ticket | null>(null)
  const [comments, setComments] = useState<Comment[]>([])
  const [users, setUsers] = useState<UserSummary[]>([])
  const [error, setError] = useState<unknown>(null)
  const [loading, setLoading] = useState(true)

  const [title, setTitle] = useState('')
  const [description, setDescription] = useState('')
  const [priority, setPriority] = useState<TicketPriority>('P1')
  const [assigneeId, setAssigneeId] = useState('')
  const [commentBody, setCommentBody] = useState('')
  const [saving, setSaving] = useState(false)

  const load = useCallback(async () => {
    setLoading(true)
    setError(null)
    try {
      const detail = await getTicket(id)
      setTicket(detail.ticket)
      setComments(detail.comments)
      setTitle(detail.ticket.title)
      setDescription(detail.ticket.description)
      setPriority(detail.ticket.priority)
      setAssigneeId(String(detail.ticket.assignee.id))
    } catch (err) {
      setError(err)
    } finally {
      setLoading(false)
    }
  }, [id])

  useEffect(() => {
    load()
    listUsers('USER').then(setUsers).catch((err) => setError(err))
  }, [load])

  async function handleSave(event: FormEvent) {
    event.preventDefault()
    setSaving(true)
    setError(null)
    const trimmedTitle = title.trim()
    const parsedAssigneeId =
      assigneeId === '' ? null : Number(assigneeId)
    if (
      !trimmedTitle ||
      priority == null ||
      parsedAssigneeId == null ||
      Number.isNaN(parsedAssigneeId)
    ) {
      setSaving(false)
      setError(
        new ApiError({
          message: 'Request validation failed',
          error: 'VALIDATION_ERROR',
          status: 400,
          fieldErrors: {
            ...(!trimmedTitle
              ? { title: 'Title must not be null or empty' }
              : {}),
            ...(priority == null
              ? { priority: 'Priority must not be null' }
              : {}),
            ...(parsedAssigneeId == null || Number.isNaN(parsedAssigneeId)
              ? { assigneeId: 'Assignee must not be null' }
              : {}),
          },
        }),
      )
      return
    }
    try {
      const updated = await updateTicket(id, {
        title: trimmedTitle,
        description,
        priority,
        assigneeId: parsedAssigneeId,
      })
      setTicket(updated)
    } catch (err) {
      setError(err)
    } finally {
      setSaving(false)
    }
  }

  async function handleStatusChange(next: TicketStatus) {
    setError(null)
    try {
      const updated = await updateTicketStatus(id, next)
      setTicket(updated)
    } catch (err) {
      setError(err)
    }
  }

  async function handleAddComment(event: FormEvent) {
    event.preventDefault()
    setError(null)
    try {
      const comment = await addComment(id, { body: commentBody })
      setComments((current) => [...current, comment])
      setCommentBody('')
    } catch (err) {
      setError(err)
    }
  }

  if (loading) {
    return <p className="muted">Loading ticket…</p>
  }

  if (!ticket) {
    return (
      <div className="card">
        <ErrorBanner error={error} />
        <Link to="/tickets">Back to list</Link>
      </div>
    )
  }

  const transitions = allowedTransitions(ticket.status)

  return (
    <div className="stack">
      <div className="page-header">
        <h2>Ticket #{ticket.id}</h2>
        <Link to="/tickets">Back to list</Link>
      </div>

      <ErrorBanner error={error} onDismiss={() => setError(null)} />

      <section className="card form-grid">
        <p><strong>Status:</strong> {ticket.status}</p>
        <div className="button-row">
          {transitions.map((status) => (
            <button
              key={status}
              type="button"
              onClick={() => handleStatusChange(status)}
            >
              Move to {status}
            </button>
          ))}
        </div>
      </section>

      <form onSubmit={handleSave} className="card form-grid">
        <h3>Details</h3>
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
          Assignee
          <select
            value={assigneeId}
            onChange={(e) => setAssigneeId(e.target.value)}
            required
          >
            {users.map((user) => (
              <option key={user.id} value={user.id}>{user.username}</option>
            ))}
          </select>
        </label>
        <button type="submit" disabled={saving}>
          {saving ? 'Saving…' : 'Save changes'}
        </button>
      </form>

      <section className="card stack">
        <h3>Comments</h3>
        {comments.length === 0 ? (
          <p className="muted">No comments yet.</p>
        ) : (
          <ul className="comment-list">
            {comments.map((comment) => (
              <li key={comment.id}>
                <p>{comment.body}</p>
                <span className="muted">
                  {comment.author.username} · {new Date(comment.createdAt).toLocaleString()}
                </span>
              </li>
            ))}
          </ul>
        )}
        <form onSubmit={handleAddComment} className="form-grid">
          <label>
            Add comment
            <textarea
              value={commentBody}
              onChange={(e) => setCommentBody(e.target.value)}
              required
              rows={3}
              maxLength={2000}
            />
          </label>
          <button type="submit">Post comment</button>
        </form>
      </section>
    </div>
  )
}
