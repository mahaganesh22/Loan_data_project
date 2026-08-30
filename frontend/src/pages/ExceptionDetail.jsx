import { useEffect, useState } from 'react'
import { Link, useParams } from 'react-router-dom'
import { apiRequest, readError } from '../api/client'
import StatusBadge from '../components/StatusBadge'

function hydrateAi(data) {
  if (!data?.aiReview) {
    return { recommendation: null, decision: data?.aiDecision || '' }
  }
  return {
    recommendation: data.aiReview,
    decision: data.aiDecision || '',
  }
}

export default function ExceptionDetail() {
  const { id } = useParams()
  const [exception, setException] = useState(null)
  const [audits, setAudits] = useState([])
  const [ai, setAi] = useState(null)
  const [aiDecision, setAiDecision] = useState('')
  const [correctedValue, setCorrectedValue] = useState('')
  const [reviewerComment, setReviewerComment] = useState('')
  const [error, setError] = useState('')
  const [notice, setNotice] = useState('')
  const [busy, setBusy] = useState(false)
  const [aiBusy, setAiBusy] = useState(false)

  const load = async () => {
    setError('')
    const response = await apiRequest(`/api/exceptions/${id}`)
    if (!response.ok) {
      setError(await readError(response))
      return
    }
    const data = await response.json()
    setException(data)
    setCorrectedValue(data.correctedValue || '')
    setReviewerComment(data.reviewerComment || '')
    const hydrated = hydrateAi(data)
    setAi(hydrated.recommendation)
    setAiDecision(hydrated.decision)

    const auditResponse = await apiRequest(`/api/audits/exception/${id}`)
    if (auditResponse.ok) {
      setAudits(await auditResponse.json())
    }
  }

  useEffect(() => {
    load()
  }, [id])

  const generateAi = async () => {
    setAiBusy(true)
    setError('')
    setNotice('')
    try {
      const response = await apiRequest(`/api/exceptions/${id}/ai-review`, { method: 'POST' })
      if (!response.ok) {
        throw new Error(await readError(response))
      }
      const recommendation = await response.json()
      setAi(recommendation)
      setAiDecision('')
      setNotice('AI recommendation generated. It has not changed any loan data.')
      const auditResponse = await apiRequest(`/api/audits/exception/${id}`)
      if (auditResponse.ok) {
        setAudits(await auditResponse.json())
      }
    } catch (err) {
      setError(err.message)
    } finally {
      setAiBusy(false)
    }
  }

  const recordAiDecision = async (decision) => {
    const response = await apiRequest(`/api/exceptions/${id}/ai-decision`, {
      method: 'PATCH',
      body: JSON.stringify({ decision }),
    })
    if (!response.ok) {
      throw new Error(await readError(response))
    }
    setAiDecision(decision)
    const auditResponse = await apiRequest(`/api/audits/exception/${id}`)
    if (auditResponse.ok) {
      setAudits(await auditResponse.json())
    }
  }

  const applyAiSuggestion = async () => {
    if (ai?.suggestedCorrection == null || String(ai.suggestedCorrection).trim() === '') return
    setError('')
    try {
      setCorrectedValue(String(ai.suggestedCorrection))
      setReviewerComment(ai.reviewerNote || reviewerComment)
      await recordAiDecision('ACCEPTED')
      setNotice('AI suggestion copied into Corrected value. Click Apply correction to write it to the loan.')
    } catch (err) {
      setError(err.message)
    }
  }

  const rejectAi = async () => {
    setError('')
    try {
      await recordAiDecision('REJECTED')
      setNotice('AI suggestion rejected. Your human decision is the source of truth.')
    } catch (err) {
      setError(err.message)
    }
  }

  const submitReview = async (event) => {
    event.preventDefault()
    const form = event.currentTarget
    const formData = new FormData(form)
    const value = String(formData.get('correctedValue') || correctedValue || ai?.suggestedCorrection || '').trim()
    const comment = String(formData.get('reviewerComment') || reviewerComment || '').trim()
    setBusy(true)
    setError('')
    setNotice('')
    try {
      let decision = aiDecision
      if (ai && ai.suggestedCorrection && value && value !== String(ai.suggestedCorrection)) {
        decision = 'EDITED'
      } else if (!decision && ai?.suggestedCorrection && value === String(ai.suggestedCorrection)) {
        decision = 'ACCEPTED'
      }
      const response = await apiRequest(`/api/exceptions/${id}`, {
        method: 'PATCH',
        body: JSON.stringify({
          correctedValue: value,
          reviewerComment: comment,
          aiDecision: decision || undefined,
        }),
      })
      if (!response.ok) {
        throw new Error(await readError(response))
      }
      const saved = await response.json()
      if (saved.status === 'RESOLVED') {
        setNotice('Correction applied and this exception is now resolved.')
      } else {
        setNotice(
          'Correction was saved on the loan, but this exception is still open because the same field still fails validation. Check the suggested value against the exception message.'
        )
      }
      await load()
    } catch (err) {
      setError(err.message)
    } finally {
      setBusy(false)
    }
  }

  const verifyLoan = async () => {
    if (!exception?.sourceLoanId) return
    setBusy(true)
    setError('')
    try {
      const response = await apiRequest(`/api/verified-loans/${exception.sourceLoanId}/verify`, {
        method: 'POST',
      })
      if (!response.ok) {
        throw new Error(await readError(response))
      }
      const verified = await response.json()
      setNotice(`Verified record created (hash ${verified.recordHash}). Sign in as Data Consumer to export it.`)
    } catch (err) {
      setError(err.message)
    } finally {
      setBusy(false)
    }
  }

  if (!exception && !error) {
    return <p className="muted">Loading exception…</p>
  }

  return (
    <div>
      <p>
        <Link to="/exceptions">← Queue</Link>
      </p>
      <header className="page-header">
        <div>
          <p className="eyebrow">Loan {exception?.loanId}</p>
          <h1>Exception #{id}</h1>
          <p className="muted">{exception?.message}</p>
        </div>
        <div className="badge-row">
          <StatusBadge value={exception?.severity} />
          <StatusBadge value={exception?.status} />
        </div>
      </header>

      {error ? <p className="error-text">{error}</p> : null}
      {notice ? <p className="success-text">{notice}</p> : null}

      <div className="split">
        <section className="panel">
          <h2>Loan detail</h2>
          <dl className="kv">
            <div>
              <dt>Internal loan id</dt>
              <dd>{exception?.sourceLoanId}</dd>
            </div>
            <div>
              <dt>Borrower</dt>
              <dd>{exception?.borrowerId || '—'}</dd>
            </div>
            <div>
              <dt>Type</dt>
              <dd>{exception?.exceptionType}</dd>
            </div>
            <div>
              <dt>Field</dt>
              <dd>{exception?.fieldName || '—'}</dd>
            </div>
            <div>
              <dt>Original value</dt>
              <dd>{exception?.originalValue || '—'}</dd>
            </div>
            <div>
              <dt>Last correction</dt>
              <dd>{exception?.correctedValue || '—'}</dd>
            </div>
          </dl>

          <form onSubmit={submitReview} className="stack">
            <h3>Human review</h3>
            <label>
              Corrected value
              <input
                name="correctedValue"
                value={correctedValue}
                onChange={(e) => setCorrectedValue(e.target.value)}
                required
              />
            </label>
            <label>
              Reviewer comment
              <textarea
                name="reviewerComment"
                rows={4}
                value={reviewerComment}
                onChange={(e) => setReviewerComment(e.target.value)}
                required
              />
            </label>
            <div className="button-row">
              <button type="submit" disabled={busy || exception?.status !== 'OPEN'}>
                {busy ? 'Saving…' : 'Apply correction'}
              </button>
              <button type="button" className="secondary" disabled={busy} onClick={verifyLoan}>
                Create verified record
              </button>
            </div>
            {exception?.status !== 'OPEN' ? (
              <p className="muted">This exception is no longer open. Further field edits are blocked by the API.</p>
            ) : null}
          </form>
        </section>

        <section className="panel ai-panel">
          <h2>AI review assistant</h2>
          <p className="muted">Recommendations are advisory. They never write to the database until you submit a human review.</p>
          <button type="button" className="secondary" onClick={generateAi} disabled={aiBusy}>
            {aiBusy ? 'Asking LangChain…' : 'Explain this exception'}
          </button>
          {ai ? (
            <>
              <dl className="kv">
                <div>
                  <dt>Model</dt>
                  <dd>{ai.model}</dd>
                </div>
                <div>
                  <dt>Generated</dt>
                  <dd>{ai.generatedAt ? new Date(ai.generatedAt).toLocaleString() : '—'}</dd>
                </div>
                <div>
                  <dt>Prompt</dt>
                  <dd>{ai.prompt}</dd>
                </div>
                <div>
                  <dt>Why it failed</dt>
                  <dd>{ai.explanation}</dd>
                </div>
                <div>
                  <dt>Suggested value</dt>
                  <dd>{ai.suggestedCorrection || 'No automatic value — edit manually.'}</dd>
                </div>
                <div>
                  <dt>Draft reviewer note</dt>
                  <dd>{ai.reviewerNote}</dd>
                </div>
                <div>
                  <dt>Severity rationale</dt>
                  <dd>{ai.severityRationale}</dd>
                </div>
                {ai.conflictComparison ? (
                  <div>
                    <dt>Conflict comparison</dt>
                    <dd>{ai.conflictComparison}</dd>
                  </div>
                ) : null}
                {ai.classifiedSeverity ? (
                  <div>
                    <dt>AI severity</dt>
                    <dd>{ai.classifiedSeverity}</dd>
                  </div>
                ) : null}
                <div>
                  <dt>Your AI decision</dt>
                  <dd>{aiDecision || 'Pending'}</dd>
                </div>
              </dl>
              <div className="button-row">
                <button type="button" onClick={applyAiSuggestion} disabled={ai.suggestedCorrection == null || String(ai.suggestedCorrection).trim() === ''}>
                  Accept suggestion
                </button>
                <button type="button" className="secondary" onClick={rejectAi}>
                  Reject suggestion
                </button>
              </div>
            </>
          ) : (
            <p className="muted">Generate an explanation to compare against your own judgement.</p>
          )}
        </section>
      </div>

      <section className="panel">
        <h2>Exception audit trail</h2>
        {audits.length === 0 ? (
          <p className="muted">No exception-level audit events yet.</p>
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
