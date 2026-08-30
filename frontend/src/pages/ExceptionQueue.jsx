import { useEffect, useMemo, useState } from 'react'
import { Link } from 'react-router-dom'
import { apiRequest, readError } from '../api/client'
import StatusBadge from '../components/StatusBadge'

export default function ExceptionQueue() {
  const [items, setItems] = useState([])
  const [status, setStatus] = useState('')
  const [severity, setSeverity] = useState('')
  const [query, setQuery] = useState('')
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(true)
  const [summary, setSummary] = useState(null)
  const [summaryBusy, setSummaryBusy] = useState(false)

  useEffect(() => {
    const load = async () => {
      setLoading(true)
      setError('')
      const params = new URLSearchParams()
      if (status) params.set('status', status)
      else if (severity) params.set('severity', severity)
      const suffix = params.toString() ? `?${params}` : ''
      const response = await apiRequest(`/api/exceptions${suffix}`)
      if (!response.ok) {
        setError(await readError(response))
        setLoading(false)
        return
      }
      setItems(await response.json())
      setLoading(false)
    }
    load()
  }, [status, severity])

  const filtered = useMemo(() => {
    const needle = query.trim().toLowerCase()
    if (!needle) {
      return items
    }
    return items.filter(
      (item) =>
        String(item.loanId || '').toLowerCase().includes(needle) ||
        String(item.borrowerId || '').toLowerCase().includes(needle),
    )
  }, [items, query])

  const openCount = items.filter((item) => item.status === 'OPEN').length

  const summarizeQueue = async () => {
    setSummaryBusy(true)
    setError('')
    try {
      const params = new URLSearchParams()
      if (status) params.set('status', status)
      else if (severity) params.set('severity', severity)
      const suffix = params.toString() ? `?${params}` : ''
      const response = await apiRequest(`/api/ai/summary${suffix}`)
      if (!response.ok) {
        throw new Error(await readError(response))
      }
      setSummary(await response.json())
    } catch (err) {
      setError(err.message)
    } finally {
      setSummaryBusy(false)
    }
  }

  return (
    <div>
      <header className="page-header">
        <div>
          <p className="eyebrow">Reviewer</p>
          <h1>Exception queue</h1>
          <p className="muted">Filter, search, and open a loan exception. AI suggestions stay separate from your decision.</p>
        </div>
        <div className="stat-card compact">
          <span>Open exceptions</span>
          <strong>{openCount}</strong>
        </div>
      </header>

      <section className="panel ai-panel">
        <h2>AI queue summary</h2>
        <p className="muted">LangChain summarizes the current filter set. It does not change any records.</p>
        <button type="button" className="secondary" onClick={summarizeQueue} disabled={summaryBusy || loading}>
          {summaryBusy ? 'Summarizing…' : 'Summarize this queue'}
        </button>
        {summary ? (
          <dl className="kv">
            <div>
              <dt>Model</dt>
              <dd>{summary.model}</dd>
            </div>
            <div>
              <dt>Prompt</dt>
              <dd>{summary.prompt}</dd>
            </div>
            <div>
              <dt>Batch size</dt>
              <dd>{summary.openCount}</dd>
            </div>
            <div>
              <dt>AI severity</dt>
              <dd>{summary.classifiedSeverity || '—'}</dd>
            </div>
            <div>
              <dt>Summary</dt>
              <dd>{summary.summary}</dd>
            </div>
          </dl>
        ) : null}
      </section>

      <section className="toolbar">
        <input
          placeholder="Search loan ID or borrower ID"
          value={query}
          onChange={(e) => setQuery(e.target.value)}
        />
        <select
          value={status}
          onChange={(e) => {
            setStatus(e.target.value)
            if (e.target.value) setSeverity('')
          }}
        >
          <option value="">All statuses</option>
          <option value="OPEN">OPEN</option>
          <option value="REVIEWING">REVIEWING</option>
          <option value="RESOLVED">RESOLVED</option>
          <option value="REJECTED">REJECTED</option>
        </select>
        <select
          value={severity}
          onChange={(e) => {
            setSeverity(e.target.value)
            if (e.target.value) setStatus('')
          }}
        >
          <option value="">All severities</option>
          <option value="LOW">LOW</option>
          <option value="MEDIUM">MEDIUM</option>
          <option value="HIGH">HIGH</option>
          <option value="CRITICAL">CRITICAL</option>
        </select>
      </section>

      {error ? <p className="error-text">{error}</p> : null}
      {loading ? <p className="muted">Loading queue…</p> : null}

      <section className="panel">
        <table>
          <thead>
            <tr>
              <th>ID</th>
              <th>Loan</th>
              <th>Borrower</th>
              <th>Type</th>
              <th>Field</th>
              <th>Severity</th>
              <th>Status</th>
              <th></th>
            </tr>
          </thead>
          <tbody>
            {filtered.map((item) => (
              <tr key={item.id}>
                <td>{item.id}</td>
                <td>{item.loanId}</td>
                <td>{item.borrowerId || '—'}</td>
                <td>{item.exceptionType}</td>
                <td>{item.fieldName || '—'}</td>
                <td>
                  <StatusBadge value={item.severity} />
                </td>
                <td>
                  <StatusBadge value={item.status} />
                </td>
                <td>
                  <Link to={`/exceptions/${item.id}`}>Review</Link>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
        {!loading && filtered.length === 0 ? <p className="muted">No exceptions match the current filters.</p> : null}
      </section>
    </div>
  )
}
