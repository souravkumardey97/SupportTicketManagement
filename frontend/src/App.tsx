import { BrowserRouter, Navigate, Route, Routes } from 'react-router-dom'
import { AuthProvider } from './auth/AuthContext'
import { Layout } from './components/Layout'
import { ProtectedRoute } from './components/ProtectedRoute'
import { CreateTicketPage } from './pages/CreateTicketPage'
import { LoginPage } from './pages/LoginPage'
import { NotAuthorizedPage } from './pages/NotAuthorizedPage'
import { TicketDetailPage } from './pages/TicketDetailPage'
import { TicketListPage } from './pages/TicketListPage'

export default function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Routes>
          <Route path="/login" element={<LoginPage />} />
          <Route element={<Layout />}>
            <Route path="/not-authorized" element={<NotAuthorizedPage />} />
            <Route element={<ProtectedRoute />}>
              <Route path="/tickets" element={<TicketListPage />} />
              <Route path="/tickets/new" element={<CreateTicketPage />} />
              <Route path="/tickets/:ticketId" element={<TicketDetailPage />} />
            </Route>
            <Route path="/" element={<Navigate to="/tickets" replace />} />
          </Route>
          <Route path="*" element={<Navigate to="/login" replace />} />
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  )
}
