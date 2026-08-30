import { NavLink, useNavigate } from 'react-router-dom'
import { useAuth } from '../auth/AuthContext'

const LINKS = {
  DATA_OPERATOR: [{ to: '/operator', label: 'Ingestion' }],
  REVIEWER: [{ to: '/exceptions', label: 'Exception queue' }],
  DATA_CONSUMER: [
    { to: '/verified', label: 'Verified loans' },
  ],
}

const ROLE_LABEL = {
  DATA_OPERATOR: 'Data Operator',
  REVIEWER: 'Reviewer',
  DATA_CONSUMER: 'Data Consumer',
}

export default function Layout({ children }) {
  const { user, logout } = useAuth()
  const navigate = useNavigate()
  const links = LINKS[user?.role] || []

  return (
    <div className="app-shell">
      <aside className="sidebar">
        <div className="brand">
          <span className="brand-mark">LDV</span>
          <div>
            <strong>Loan Verification</strong>
            <p>Copilot console</p>
          </div>
        </div>
        <nav>
          {links.map((link) => (
            <NavLink key={link.to} to={link.to} className={({ isActive }) => (isActive ? 'nav-link active' : 'nav-link')}>
              {link.label}
            </NavLink>
          ))}
        </nav>
        <div className="sidebar-footer">
          <div className="user-chip">
            <span>{user?.username}</span>
            <small>{ROLE_LABEL[user?.role] || user?.role}</small>
          </div>
          <button
            type="button"
            className="ghost-btn"
            onClick={() => {
              logout()
              navigate('/login')
            }}
          >
            Sign out
          </button>
        </div>
      </aside>
      <main className="content">{children}</main>
    </div>
  )
}
