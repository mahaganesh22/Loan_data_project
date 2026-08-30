export default function StatusBadge({ value }) {
  const tone = String(value || '').toLowerCase()
  return <span className={`badge ${tone}`}>{value || '—'}</span>
}
