import { Navigate } from 'react-router-dom'
import { HOME_BY_ROLE, useAuth } from '../auth/AuthContext'
import Layout from './Layout'

export default function ProtectedRoute({ roles, children }) {
  const { user } = useAuth()

  if (!user?.token) {
    return <Navigate to="/login" replace />
  }

  if (roles && !roles.includes(user.role)) {
    return <Navigate to={HOME_BY_ROLE[user.role] || '/login'} replace />
  }

  return <Layout>{children}</Layout>
}
