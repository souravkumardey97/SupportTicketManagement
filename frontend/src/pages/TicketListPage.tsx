import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { listTickets, TICKET_STATUSES } from '../api/ticketsApi'
import { ErrorBanner } from '../components/ErrorBanner'
import type { Ticket, TicketStatus } from '../types/api'

export function TicketListPage() {
  const [keywordInput, setKeywordInput] = useState('')
  const [keyword, setKeyword] = useState('')
  const [status, setStatus] = useState<TicketStatus | ''>('')
  const [page, setPage] = useState(0)
  const [tickets, setTickets] = useState<Ticket[]>([])
  const [totalPages, setTotalPages] = useState(0)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<unknown>(null)

  useEffect(() => {
    const timer = window.setTimeout(() => setKeyword(keywordInput.trim()), 300)
    return () => window.clearTimeout(timer)
  }, [keywordInput])

  useEffect(() => {
    setPage(0)
  }, [keyword, status])

  useEffect(() => {
    let cancelled = false
    setLoading(true)
    setError(null)
    listTickets({ keyword, status, page, size: 20 })
      .then((result) => {
        if (!cancelled) {
          setTickets(result.content)
          setTotalPages(result.totalPages)
        }
      })
      .catch((err) => {
        if (!cancelled) {
          setError(err)
        }
      })
      .finally(() => {
        if (!cancelled) {
          setLoading(false)
        }
      })
    return () => {
      cancelled = true
    }
  }, [keyword, status, page])

  const hasFilters = keyword !== '' || status !== ''

  return (
    <div className="stack">
      <div className="page-header">
        <h2>Tickets</h2>
        <Link to="/tickets/new" className="button-link">Create ticket</Link>
      </div>

      <div className="filters card">
        <label>
          Search
          <input
            value={keywordInput}
            onChange={(e) => setKeywordInput(e.target.value)}
            placeholder="Title, description, assignee, or priority"
          />
        </label>
        <label>
          Status
          <select
            value={status}
            onChange={(e) => setStatus(e.target.value as TicketStatus | '')}
          >
            <option value="">All statuses</option>
            {TICKET_STATUSES.map((value) => (
              <option key={value} value={value}>{value}</option>
            ))}
          </select>
        </label>
      </div>

      <ErrorBanner error={error} onDismiss={() => setError(null)} />

      {loading ? (
        <p className="muted">Loading tickets…</p>
      ) : tickets.length === 0 ? (
        <p className="muted">
          {hasFilters ? 'No tickets match your filters.' : 'No tickets created yet.'}
        </p>
      ) : (
        <div className="table-wrap card">
          <table>
            <thead>
              <tr>
                <th>ID</th>
                <th>Title</th>
                <th>Status</th>
                <th>Priority</th>
                <th>Assignee</th>
              </tr>
            </thead>
            <tbody>
              {tickets.map((ticket) => (
                <tr key={ticket.id}>
                  <td>{ticket.id}</td>
                  <td>
                    <Link to={`/tickets/${ticket.id}`}>{ticket.title}</Link>
                  </td>
                  <td>{ticket.status}</td>
                  <td>{ticket.priority}</td>
                  <td>{ticket.assignee.username}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      <div className="pagination">
        <button
          type="button"
          disabled={page <= 0}
          onClick={() => setPage((current) => current - 1)}
        >
          Previous
        </button>
        <span className="muted">
          Page {page + 1} of {Math.max(totalPages, 1)}
        </span>
        <button
          type="button"
          disabled={page + 1 >= totalPages}
          onClick={() => setPage((current) => current + 1)}
        >
          Next
        </button>
      </div>
    </div>
  )
}
