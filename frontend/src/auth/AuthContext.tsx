import {
  createContext,
  useCallback,
  useContext,
  useMemo,
  useState,
  type ReactNode,
} from 'react'
import { setStoredToken } from '../api/client'
import { login as loginApi } from '../api/ticketsApi'
import type { LoginRequest, Role } from '../types/api'

const ROLE_KEY = 'support_ticket_role'

interface AuthContextValue {
  token: string | null
  role: Role | null
  isAuthenticated: boolean
  isAdmin: boolean
  login: (request: LoginRequest) => Promise<Role>
  logout: () => void
}

const AuthContext = createContext<AuthContextValue | undefined>(undefined)

function readRole(): Role | null {
  const value = localStorage.getItem(ROLE_KEY)
  return value === 'ADMIN' || value === 'USER' ? value : null
}

export function AuthProvider({ children }: { children: ReactNode }) {
  const [token, setToken] = useState<string | null>(
    () => localStorage.getItem('support_ticket_access_token'),
  )
  const [role, setRole] = useState<Role | null>(readRole)

  const logout = useCallback(() => {
    setToken(null)
    setRole(null)
    setStoredToken(null)
    localStorage.removeItem(ROLE_KEY)
  }, [])

  const login = useCallback(async (request: LoginRequest) => {
    const response = await loginApi(request)
    setToken(response.accessToken)
    setRole(response.role)
    setStoredToken(response.accessToken)
    localStorage.setItem(ROLE_KEY, response.role)
    return response.role
  }, [])

  const value = useMemo(
    () => ({
      token,
      role,
      isAuthenticated: Boolean(token),
      isAdmin: role === 'ADMIN',
      login,
      logout,
    }),
    [token, role, login, logout],
  )

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}

export function useAuth(): AuthContextValue {
  const context = useContext(AuthContext)
  if (!context) {
    throw new Error('useAuth must be used within AuthProvider')
  }
  return context
}
