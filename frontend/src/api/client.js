const API_BASE = import.meta.env.VITE_API_URL ?? ''

const SESSION_ERROR_CODES = new Set(['SESSION_EXPIRED', 'AUTHENTICATION_REQUIRED', 'AUTHENTICATION_FAILED'])

function getStoredUser() {
  const raw = localStorage.getItem('ldv_user')
  if (!raw) {
    return null
  }
  try {
    return JSON.parse(raw)
  } catch {
    return null
  }
}

function isLoginRequest(path) {
  return typeof path === 'string' && path.includes('/api/auth/login')
}

function messageFromBody(data) {
  if (!data || typeof data !== 'object') {
    return ''
  }
  if (typeof data.message === 'string' && data.message.trim()) {
    return data.message.trim()
  }
  if (typeof data.detail === 'string' && data.detail.trim()) {
    return data.detail.trim()
  }
  if (Array.isArray(data.detail) && data.detail.length) {
    return data.detail
      .map((item) => (typeof item?.msg === 'string' ? item.msg : ''))
      .filter(Boolean)
      .join(' ')
  }
  if (typeof data.error === 'string' && data.error.trim()) {
    return data.error.trim()
  }
  return ''
}

function fallbackMessage(status, code) {
  switch (code) {
    case 'INVALID_CREDENTIALS':
      return 'Username or password is incorrect. Please check your credentials and try again.'
    case 'AI_API_EXPIRED':
      return 'The AI API key has expired. Update OPENAI_API_KEY in ai_review/.env and restart the AI service.'
    case 'AI_API_INVALID_KEY':
      return 'The AI API key is invalid. Check OPENAI_API_KEY in ai_review/.env, then restart the AI service.'
    case 'AI_API_KEY_MISSING':
      return 'The AI API key is not configured. Add OPENAI_API_KEY in ai_review/.env and restart the AI service.'
    case 'AI_API_QUOTA_EXCEEDED':
      return 'The AI API quota or billing limit has been reached. Check your OpenAI account billing and try again later.'
    case 'AI_API_RATE_LIMIT':
      return 'The AI API rate limit was reached. Wait a moment and try again.'
    case 'SESSION_EXPIRED':
      return 'Your session is invalid or has expired. Please sign in again.'
    case 'ACCESS_DENIED':
      return 'You do not have permission to perform this action with your current role.'
    default:
      break
  }
  if (status === 401) {
    return 'Username or password is incorrect, or your session has expired.'
  }
  if (status === 403) {
    return 'You do not have permission to perform this action.'
  }
  if (status === 404) {
    return 'The requested record was not found.'
  }
  if (status === 503) {
    return 'A required service is unavailable. Try again in a moment.'
  }
  return `Request failed (${status})`
}

export async function apiRequest(path, options = {}) {
  const user = getStoredUser()
  const headers = new Headers(options.headers || {})

  if (options.body && !(options.body instanceof FormData) && !headers.has('Content-Type')) {
    headers.set('Content-Type', 'application/json')
  }

  if (user?.token) {
    headers.set('Authorization', `Bearer ${user.token}`)
  }

  const response = await fetch(`${API_BASE}${path}`, {
    ...options,
    headers,
  })

  if (response.status === 401 && !isLoginRequest(path)) {
    const cloned = response.clone()
    let code = ''
    try {
      const data = await cloned.json()
      code = data?.code || ''
    } catch {
      code = ''
    }
    if (!code || SESSION_ERROR_CODES.has(code)) {
      localStorage.removeItem('ldv_user')
      if (!window.location.pathname.startsWith('/login')) {
        window.location.assign('/login')
      }
    }
  }

  return response
}

export async function readError(response) {
  try {
    const data = await response.json()
    return messageFromBody(data) || fallbackMessage(response.status, data?.code)
  } catch {
    return fallbackMessage(response.status)
  }
}

export { getStoredUser }
