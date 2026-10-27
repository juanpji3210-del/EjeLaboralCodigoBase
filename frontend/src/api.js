const BASE = '/api/auth'

async function request(path, options = {}) {
  const token = localStorage.getItem('token')
  const res = await fetch(BASE + path, {
    headers: {
      'Content-Type': 'application/json',
      ...(token ? { Authorization: `Bearer ${token}` } : {}),
    },
    ...options,
  })

  const data = await res.json().catch(() => ({}))
  if (!res.ok) {
    const fieldMsg = data.fieldErrors ? Object.values(data.fieldErrors).join(' · ') : null
    const err = new Error(data.message || fieldMsg || 'Error inesperado')
    err.status = res.status
    throw err
  }
  return data
}

// Cliente de la API según specs/001-autenticacion/contracts/auth-api.md
export const api = {
  register: (body) => request('/register', { method: 'POST', body: JSON.stringify(body) }),
  login: (body) => request('/login', { method: 'POST', body: JSON.stringify(body) }),
  me: () => request('/me'),
}
