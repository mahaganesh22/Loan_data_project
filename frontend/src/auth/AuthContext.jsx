import { createContext, useContext, useMemo, useState } from 'react'
import { apiRequest, readError } from '../api/client'

const AuthContext = createContext(null)

const HOME_BY_ROLE = {
  DATA_OPERATOR: '/operator',
  REVIEWER: '/exceptions',
  DATA_CONSUMER: '/verified',
}

export function AuthProvider({ children }) {
  const [user, setUser] = useState(() => {
    const raw = localStorage.getItem('ldv_user')
    if (!raw) {
      return null
    }
    try {
      return JSON.parse(raw)
    } catch {
      return null
    }
  })

  const value = useMemo(() => {
    const login = async (username, password) => {
      const response = await apiRequest('/api/auth/login', {
        method: 'POST',
        body: JSON.stringify({ username, password }),
      })

      if (!response.ok) {
        throw new Error(await readError(response))
      }

      const data = await response.json()
      const nextUser = {
        token: data.token,
        username: data.username,
        role: data.role,
      }
      localStorage.setItem('ldv_user', JSON.stringify(nextUser))
      setUser(nextUser)
      return nextUser
    }

    const logout = () => {
      localStorage.removeItem('ldv_user')
      setUser(null)
    }

    return {
      user,
      login,
      logout,
      homePath: user?.role ? HOME_BY_ROLE[user.role] || '/login' : '/login',
    }
  }, [user])

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}

export function useAuth() {
  const context = useContext(AuthContext)
  if (!context) {
    throw new Error('useAuth must be used inside AuthProvider')
  }
  return context
}

export { HOME_BY_ROLE }
