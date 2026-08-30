import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { apiRequest, readError } from '../api/client'

export default function VerifiedLoans() {
  const [page, setPage] = useState(null)
  const [loanId, setLoanId] = useState('')
  const [borrowerId, setBorrowerId] = useState('')
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(true)

  const load = async (pageNumber = 0) => {
    setLoading(true)
    setError('')
    const params = new URLSearchParams({ page: String(pageNumber), size: '12' })
    if (loanId.trim()) params.set('loanId', loanId.trim())
    if (borrowerId.trim()) params.set('borrowerId', borrowerId.trim())
    const response = await apiRequest(`/api/verified-loans?${params}`)
    if (!response.ok) {
      setError(await readError(response))
      setLoading(false)
      return
    }
    setPage(await response.json())
    setLoading(false)
  }

  useEffect(() => {
    load(0)
  }, [])

  const exportCsv = async () => {
    setError('')
    const response = await apiRequest('/api/verified-loans/export')
    if (!response.ok) {
      setError(await readError(response))
      return
    }
    const blob = await response.blob()
    const url = URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = 'verified_loans.csv'
    link.click()
    URL.revokeObjectURL(url)
  }

  const content = page?.content || []
  const quality =
    page?.totalElements != null
      ? `${page.totalElements} verified records in the canonical store`
      : 'No verified records yet'

  return (
    <div>
      <header className="page-header">
        <div>
          <p className="eyebrow">Data Consumer</p>
          <h1>Verified loan records</h1>
          <p className="muted">{quality}</p>
        </div>
        <button type="button" onClick={exportCsv}>
          Export CSV
        </button>
      </header>

      <form
        className="toolbar"
        onSubmit={(e) => {
          e.preventDefault()
          load(0)
        }}
      >
        <input placeholder="Loan ID" value={loanId} onChange={(e) => setLoanId(e.target.value)} />
        <input placeholder="Borrower ID" value={borrowerId} onChange={(e) => setBorrowerId(e.target.value)} />
        <button type="submit">Search</button>
      </form>

      {error ? <p className="error-text">{error}</p> : null}
      {loading ? <p className="muted">Loading verified loans…</p> : null}

      <section className="panel">
        <table>
          <thead>
            <tr>
              <th>Loan</th>
              <th>Borrower</th>
              <th>Type</th>
              <th>Balance</th>
              <th>Verified by</th>
              <th>Verified at</th>
              <th></th>
            </tr>
          </thead>
          <tbody>
            {content.map((loan) => (
              <tr key={loan.id}>
                <td>{loan.loanId}</td>
                <td>{loan.borrowerId}</td>
                <td>{loan.loanType}</td>
                <td>{loan.currentBalance}</td>
                <td>{loan.verifiedBy}</td>
                <td>{loan.verifiedAt ? new Date(loan.verifiedAt).toLocaleString() : '—'}</td>
                <td>
                  <Link to={`/verified/${loan.id}`}>Open</Link>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
        {!loading && content.length === 0 ? <p className="muted">No verified loans found.</p> : null}
      </section>

      {page ? (
        <div className="button-row">
          <button type="button" className="secondary" disabled={page.first} onClick={() => load(page.number - 1)}>
            Previous
          </button>
          <span className="muted">
            Page {page.number + 1} of {Math.max(page.totalPages, 1)}
          </span>
          <button type="button" className="secondary" disabled={page.last} onClick={() => load(page.number + 1)}>
            Next
          </button>
        </div>
      ) : null}
    </div>
  )
}
