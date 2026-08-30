import { BrowserRouter, Navigate, Route, Routes } from 'react-router-dom'
import { AuthProvider } from './auth/AuthContext'
import ProtectedRoute from './components/ProtectedRoute'
import ExceptionDetail from './pages/ExceptionDetail'
import ExceptionQueue from './pages/ExceptionQueue'
import LoginPage from './pages/LoginPage'
import OperatorDashboard from './pages/OperatorDashboard'
import VerifiedLoanDetail from './pages/VerifiedLoanDetail'
import VerifiedLoans from './pages/VerifiedLoans'

export default function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Routes>
          <Route path="/login" element={<LoginPage />} />
          <Route
            path="/operator"
            element={
              <ProtectedRoute roles={['DATA_OPERATOR']}>
                <OperatorDashboard />
              </ProtectedRoute>
            }
          />
          <Route
            path="/exceptions"
            element={
              <ProtectedRoute roles={['REVIEWER']}>
                <ExceptionQueue />
              </ProtectedRoute>
            }
          />
          <Route
            path="/exceptions/:id"
            element={
              <ProtectedRoute roles={['REVIEWER']}>
                <ExceptionDetail />
              </ProtectedRoute>
            }
          />
          <Route
            path="/verified"
            element={
              <ProtectedRoute roles={['DATA_CONSUMER']}>
                <VerifiedLoans />
              </ProtectedRoute>
            }
          />
          <Route
            path="/verified/:id"
            element={
              <ProtectedRoute roles={['DATA_CONSUMER']}>
                <VerifiedLoanDetail />
              </ProtectedRoute>
            }
          />
          <Route path="/" element={<Navigate to="/login" replace />} />
          <Route path="*" element={<Navigate to="/login" replace />} />
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  )
}
