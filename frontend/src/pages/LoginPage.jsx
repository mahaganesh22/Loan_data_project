import { useState } from 'react'
import { Navigate, useNavigate } from 'react-router-dom'
import { HOME_BY_ROLE, useAuth } from '../auth/AuthContext'

export default function LoginPage() {
  const { user, login } = useAuth()
  const navigate = useNavigate()
  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState('')
  const [busy, setBusy] = useState(false)

  if (user?.token) {
    return <Navigate to={HOME_BY_ROLE[user.role] || '/login'} replace />
  }

  const onSubmit = async (event) => {
    event.preventDefault()
    setError('')
    setBusy(true)
    try {
      const nextUser = await login(username.trim(), password)
      navigate(HOME_BY_ROLE[nextUser.role] || '/login')
    } catch (err) {
      setError(err.message || 'Unable to sign in')
    } finally {
      setBusy(false)
    }
  }

  return (
    <div className="login-screen">
      <section className="login-hero">
        <p className="eyebrow">Intain Campus FinTech Challenge</p>
        <h1>Loan Data Verification Copilot</h1>
        <p>
          Ingest messy loan tapes, resolve exceptions with a human-in-the-loop review, and publish
          hashed verified records with a full audit trail.
        </p>
      </section>
      <form className="login-card" onSubmit={onSubmit}>
        <h2>Sign in</h2>
        <p className="muted">Use a Data Operator, Reviewer, or Data Consumer account from the backend.</p>
        <label>
          Username
          <input value={username} onChange={(e) => setUsername(e.target.value)} autoComplete="username" required />
        </label>
        <label>
          Password
          <input
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            autoComplete="current-password"
            required
          />
        </label>
        {error ? <p className="error-text">{error}</p> : null}
        <button type="submit" disabled={busy}>
          {busy ? 'Signing in…' : 'Enter console'}
        </button>
      </form>
    </div>
  )
}
