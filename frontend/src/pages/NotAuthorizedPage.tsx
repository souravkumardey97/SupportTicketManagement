import { Link } from 'react-router-dom'
import { useAuth } from '../auth/AuthContext'

export function NotAuthorizedPage() {
  const { role, logout } = useAuth()

  return (
    <div className="card narrow">
      <h2>Not authorized</h2>
      <p>
        Your account has the <strong>{role}</strong> role. Ticket management is
        only available to <strong>ADMIN</strong> users.
      </p>
      <p className="muted">
        USER accounts can be assigned to tickets but cannot create or manage them
        in this application.
      </p>
      <div className="button-row">
        <button type="button" onClick={logout}>Log out</button>
        <Link to="/login">Switch account</Link>
      </div>
    </div>
  )
}
