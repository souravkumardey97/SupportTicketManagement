import { Link, Outlet } from 'react-router-dom'
import { useAuth } from '../auth/AuthContext'

export function Layout() {
  const { isAdmin, logout, role } = useAuth()

  return (
    <div className="app-shell">
      <header className="app-header">
        <div>
          <h1>Support Tickets</h1>
          {role && <span className="muted">Signed in as {role}</span>}
        </div>
        <nav className="app-nav">
          {isAdmin && (
            <>
              <Link to="/tickets">Tickets</Link>
              <Link to="/tickets/new">Create ticket</Link>
              <Link to="/users/new">Create user</Link>
            </>
          )}
          <button type="button" onClick={logout}>Log out</button>
        </nav>
      </header>
      <main className="app-main">
        <Outlet />
      </main>
    </div>
  )
}
