import { useEffect, useState } from 'react'
import { Link, useParams } from 'react-router-dom'
import { apiRequest, readError } from '../api/client'

export default function VerifiedLoanDetail() {
  const { id } = useParams()
  const [loan, setLoan] = useState(null)
  const [audits, setAudits] = useState([])
  const [error, setError] = useState('')

  useEffect(() => {
    const load = async () => {
      setError('')
      const response = await apiRequest(`/api/verified-loans/${id}`)
      if (!response.ok) {
        setError(await readError(response))
        return
      }
      const data = await response.json()
      setLoan(data)
      if (data.sourceLoanId) {
        const auditResponse = await apiRequest(`/api/audits/loan/${data.sourceLoanId}`)
        if (auditResponse.ok) {
          setAudits(await auditResponse.json())
        }
      }
    }
    load()
  }, [id])

  if (!loan && !error) {
    return <p className="muted">Loading verified loan…</p>
  }

  const fields = loan
    ? [
        ['Canonical loan id', loan.loanId],
        ['Borrower', loan.borrowerId],
        ['Loan type', loan.loanType],
        ['Origination', loan.originationDate],
        ['Maturity', loan.maturityDate],
        ['Original principal', loan.originalPrincipal],
        ['Current balance', loan.currentBalance],
        ['Interest rate', loan.interestRate],
        ['Term (months)', loan.termMonths],
        ['State', loan.borrowerState],
        ['Purpose', loan.loanPurpose],
        ['Credit grade', loan.creditGrade],
        ['Payment status', loan.paymentStatus],
        ['Days past due', loan.daysPastDue],
        ['Servicer', loan.servicerName],
        ['Document status', loan.documentStatus],
        ['Source system', loan.sourceSystem],
        ['Status', loan.status],
        ['Verified by', loan.verifiedBy],
        ['Verified at', loan.verifiedAt ? new Date(loan.verifiedAt).toLocaleString() : '—'],
        ['Record hash', loan.recordHash],
        ['AI recommendation (if used)', loan.aiRecommendation],
      ]
    : []

  return (
    <div>
      <p>
        <Link to="/verified">← Verified loans</Link>
      </p>
      <header className="page-header">
        <div>
          <p className="eyebrow">Trusted record</p>
          <h1>{loan?.loanId || `Verified #${id}`}</h1>
          <p className="muted">Canonical snapshot with hash and source loan lineage.</p>
        </div>
      </header>
      {error ? <p className="error-text">{error}</p> : null}

      <section className="panel">
        <h2>Canonical fields</h2>
        <dl className="kv two-col">
          {fields.map(([label, value]) => (
            <div key={label}>
              <dt>{label}</dt>
              <dd className={label === 'Record hash' ? 'hash' : undefined}>{value ?? '—'}</dd>
            </div>
          ))}
        </dl>
      </section>

      <section className="panel">
        <h2>Audit trail</h2>
        {audits.length === 0 ? (
          <p className="muted">No loan-level audit events were returned for this source loan.</p>
        ) : (
          <ol className="timeline">
            {audits.map((item) => (
              <li key={item.id}>
                <strong>{item.action}</strong>
                <span>{item.details}</span>
                <small>
                  {item.performedBy || 'system'} · {item.createdAt ? new Date(item.createdAt).toLocaleString() : ''}
                </small>
              </li>
            ))}
          </ol>
        )}
      </section>
    </div>
  )
}
