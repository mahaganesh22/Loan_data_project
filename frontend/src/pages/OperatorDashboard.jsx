import { useMemo, useState } from 'react'
import { apiRequest, readError } from '../api/client'

const HISTORY_KEY = 'ldv_upload_history'

function loadHistory() {
  try {
    return JSON.parse(localStorage.getItem(HISTORY_KEY) || '[]')
  } catch {
    return []
  }
}

export default function OperatorDashboard() {
  const [file, setFile] = useState(null)
  const [busy, setBusy] = useState(false)
  const [error, setError] = useState('')
  const [result, setResult] = useState(null)
  const [history, setHistory] = useState(loadHistory)
  const [ruleText, setRuleText] = useState('')
  const [ruleResult, setRuleResult] = useState(null)
  const [ruleBusy, setRuleBusy] = useState(false)
  const [ruleError, setRuleError] = useState('')

  const summary = useMemo(() => {
    if (!history.length) {
      return { uploads: 0, failed: 0, successful: 0 }
    }
    return history.reduce(
      (acc, item) => ({
        uploads: acc.uploads + 1,
        failed: acc.failed + (item.failedRows || 0),
        successful: acc.successful + (item.successfulRows || 0),
      }),
      { uploads: 0, failed: 0, successful: 0 },
    )
  }, [history])

  const onUpload = async (event) => {
    event.preventDefault()
    if (!file) {
      setError('Choose a CSV loan tape first.')
      return
    }
    setBusy(true)
    setError('')
    try {
      const form = new FormData()
      form.append('file', file)
      const response = await apiRequest('/api/uploads', {
        method: 'POST',
        body: form,
      })
      if (!response.ok) {
        throw new Error(await readError(response))
      }
      const data = await response.json()
      const entry = { ...data, uploadedAt: new Date().toISOString() }
      const nextHistory = [entry, ...history].slice(0, 12)
      localStorage.setItem(HISTORY_KEY, JSON.stringify(nextHistory))
      setHistory(nextHistory)
      setResult(data)
      setFile(null)
    } catch (err) {
      setError(err.message || 'Upload failed')
    } finally {
      setBusy(false)
    }
  }

  const generateRule = async (event) => {
    event.preventDefault()
    setRuleBusy(true)
    setRuleError('')
    setRuleResult(null)
    try {
      const response = await apiRequest('/api/ai/rules', {
        method: 'POST',
        body: JSON.stringify({ instruction: ruleText }),
      })
      if (!response.ok) {
        throw new Error(await readError(response))
      }
      setRuleResult(await response.json())
    } catch (err) {
      setRuleError(err.message)
    } finally {
      setRuleBusy(false)
    }
  }

  return (
    <div>
      <header className="page-header">
        <div>
          <p className="eyebrow">Data Operator</p>
          <h1>Ingestion desk</h1>
          <p className="muted">Upload a loan tape CSV. The backend stores raw rows, normalizes them, and runs validation.</p>
        </div>
      </header>

      <section className="stat-grid">
        <article className="stat-card">
          <span>Session uploads</span>
          <strong>{summary.uploads}</strong>
        </article>
        <article className="stat-card">
          <span>Rows imported</span>
          <strong>{summary.successful}</strong>
        </article>
        <article className="stat-card">
          <span>Failed rows</span>
          <strong>{summary.failed}</strong>
        </article>
      </section>

      <section className="panel">
        <h2>Upload loan tape</h2>
        <form className="upload-form" onSubmit={onUpload}>
          <label className="dropzone">
            <input
              type="file"
              accept=".csv,text/csv"
              onChange={(e) => setFile(e.target.files?.[0] || null)}
            />
            <span>{file ? file.name : 'Drop or choose a .csv file'}</span>
          </label>
          <button type="submit" disabled={busy}>
            {busy ? 'Parsing…' : 'Upload and validate'}
          </button>
        </form>
        {error ? <p className="error-text">{error}</p> : null}
        {result ? (
          <div className="result-grid">
            <div>
              <span>File</span>
              <strong>{result.fileName}</strong>
            </div>
            <div>
              <span>Total rows</span>
              <strong>{result.totalRows}</strong>
            </div>
            <div>
              <span>Successful</span>
              <strong>{result.successfulRows}</strong>
            </div>
            <div>
              <span>Failed</span>
              <strong>{result.failedRows}</strong>
            </div>
            <div>
              <span>Status</span>
              <strong>{result.status}</strong>
            </div>
          </div>
        ) : null}
      </section>

      <section className="panel">
        <h2>Import history (this browser)</h2>
        {history.length === 0 ? (
          <p className="muted">No uploads yet in this session.</p>
        ) : (
          <table>
            <thead>
              <tr>
                <th>When</th>
                <th>File</th>
                <th>Total</th>
                <th>OK</th>
                <th>Failed</th>
                <th>Status</th>
              </tr>
            </thead>
            <tbody>
              {history.map((item, index) => (
                <tr key={`${item.fileName}-${index}`}>
                  <td>{new Date(item.uploadedAt).toLocaleString()}</td>
                  <td>{item.fileName}</td>
                  <td>{item.totalRows}</td>
                  <td>{item.successfulRows}</td>
                  <td>{item.failedRows}</td>
                  <td>{item.status}</td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </section>

      <section className="panel ai-panel">
        <h2>AI validation-rule helper</h2>
        <p className="muted">Describe a rule in plain language. LangChain drafts a rule and a test idea. It does not change the engine.</p>
        <form className="stack" onSubmit={generateRule}>
          <label>
            Natural-language instruction
            <textarea
              rows={3}
              value={ruleText}
              onChange={(e) => setRuleText(e.target.value)}
              placeholder="Flag loans where current balance is greater than original principal"
              required
            />
          </label>
          <button type="submit" disabled={ruleBusy}>
            {ruleBusy ? 'Generating…' : 'Generate rule draft'}
          </button>
        </form>
        {ruleError ? <p className="error-text">{ruleError}</p> : null}
        {ruleResult ? (
          <dl className="kv">
            <div>
              <dt>Model</dt>
              <dd>{ruleResult.model}</dd>
            </div>
            <div>
              <dt>Prompt</dt>
              <dd>{ruleResult.prompt}</dd>
            </div>
            <div>
              <dt>Suggested rule</dt>
              <dd>{ruleResult.suggestedRule}</dd>
            </div>
            <div>
              <dt>Test idea</dt>
              <dd>{ruleResult.testIdea}</dd>
            </div>
          </dl>
        ) : null}
      </section>
    </div>
  )
}
