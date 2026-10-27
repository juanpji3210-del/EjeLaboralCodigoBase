import { useEffect, useState } from 'react'
import { api } from './api'

const EMPTY_FORM = { email: '', password: '', fullName: '' }

export default function App() {
  const [user, setUser] = useState(null)
  const [checking, setChecking] = useState(true)
  const [mode, setMode] = useState('login') // 'login' | 'register'
  const [form, setForm] = useState(EMPTY_FORM)
  const [error, setError] = useState('')
  const [busy, setBusy] = useState(false)

  // US3-AC3: al recargar, si hay token guardado se recupera la sesión
  useEffect(() => {
    const token = localStorage.getItem('token')
    if (!token) {
      setChecking(false)
      return
    }
    api
      .me()
      .then(setUser)
      .catch(() => localStorage.removeItem('token'))
      .finally(() => setChecking(false))
  }, [])

  function handleChange(e) {
    setForm({ ...form, [e.target.name]: e.target.value })
  }

  function switchMode(next) {
    setMode(next)
    setError('')
    setForm(EMPTY_FORM)
  }

  async function handleSubmit(e) {
    e.preventDefault()
    setBusy(true)
    setError('')

    // Validación básica en cliente (FR-12)
    if (!form.email || !form.password || (mode === 'register' && !form.fullName)) {
      setError('Completa todos los campos.')
      setBusy(false)
      return
    }
    if (mode === 'register' && form.password.length < 8) {
      setError('La contraseña debe tener al menos 8 caracteres.')
      setBusy(false)
      return
    }

    try {
      const data =
        mode === 'login'
          ? await api.login({ email: form.email, password: form.password })
          : await api.register({
              email: form.email,
              password: form.password,
              fullName: form.fullName,
            })
      localStorage.setItem('token', data.accessToken)
      setUser(data.user)
    } catch (err) {
      setError(err.message)
    } finally {
      setBusy(false)
    }
  }

  function logout() {
    localStorage.removeItem('token')
    setUser(null)
    setForm(EMPTY_FORM)
    setMode('login')
    setError('')
  }

  if (checking) {
    return (
      <div className="page">
        <div className="card">
          <p className="muted">Cargando…</p>
        </div>
      </div>
    )
  }

  return (
    <div className="page">
      {user ? (
        <Dashboard user={user} onLogout={logout} />
      ) : (
        <div className="card">
          <h1 className="brand">EjeLaboral</h1>
          <p className="muted">
            {mode === 'login' ? 'Inicia sesión para continuar' : 'Crea tu cuenta para empezar'}
          </p>

          <div className="tabs" role="tablist">
            <button
              role="tab"
              aria-selected={mode === 'login'}
              className={mode === 'login' ? 'tab active' : 'tab'}
              onClick={() => switchMode('login')}
            >
              Iniciar sesión
            </button>
            <button
              role="tab"
              aria-selected={mode === 'register'}
              className={mode === 'register' ? 'tab active' : 'tab'}
              onClick={() => switchMode('register')}
            >
              Registrarse
            </button>
          </div>

          <form onSubmit={handleSubmit} noValidate>
            {mode === 'register' && (
              <label>
                Nombre completo
                <input
                  type="text"
                  name="fullName"
                  value={form.fullName}
                  onChange={handleChange}
                  placeholder="Ana Pérez"
                  autoComplete="name"
                />
              </label>
            )}

            <label>
              Email
              <input
                type="email"
                name="email"
                value={form.email}
                onChange={handleChange}
                placeholder="tu@email.com"
                autoComplete="email"
              />
            </label>

            <label>
              Contraseña
              <input
                type="password"
                name="password"
                value={form.password}
                onChange={handleChange}
                placeholder={mode === 'register' ? 'Mínimo 8 caracteres' : '••••••••'}
                autoComplete={mode === 'register' ? 'new-password' : 'current-password'}
              />
            </label>

            {error && <div className="error-banner">{error}</div>}

            <button type="submit" className="primary" disabled={busy}>
              {busy ? 'Procesando…' : mode === 'login' ? 'Entrar' : 'Crear cuenta'}
            </button>
          </form>
        </div>
      )}
    </div>
  )
}

function Dashboard({ user, onLogout }) {
  return (
    <div className="card">
      <div className="avatar">{user.fullName.charAt(0).toUpperCase()}</div>
      <h1 className="brand">Hola, {user.fullName.split(' ')[0]} 👋</h1>
      <p className="muted">Sesión iniciada correctamente con JWT</p>

      <dl className="profile">
        <div>
          <dt>Email</dt>
          <dd>{user.email}</dd>
        </div>
        <div>
          <dt>Nombre</dt>
          <dd>{user.fullName}</dd>
        </div>
        <div>
          <dt>Rol</dt>
          <dd>{user.role}</dd>
        </div>
        <div>
          <dt>Cuenta creada</dt>
          <dd>{new Date(user.createdAt).toLocaleString('es')}</dd>
        </div>
      </dl>

      <button className="primary" onClick={onLogout}>
        Cerrar sesión
      </button>
    </div>
  )
}
