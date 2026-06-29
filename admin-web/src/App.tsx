// src/App.tsx
import { useEffect, useState } from 'react'
import { onAuthStateChanged, type User } from 'firebase/auth'
import { auth } from './firebase'
import { signOut } from 'firebase/auth'
import LoginPage from './pages/LoginPage'
import UploadPage from './pages/UploadPage'
import QueryPage from './pages/QueryPage'

type Page = 'upload' | 'query'

export default function App() {
  const [user, setUser]       = useState<User | null>(null)
  const [checking, setChecking] = useState(true)
  const [page, setPage]       = useState<Page>('upload')

  useEffect(() => {
    return onAuthStateChanged(auth, (u) => {
      setUser(u)
      setChecking(false)
    })
  }, [])

  if (checking) return <p style={{ padding: 40 }}>Cargando...</p>
  if (!user) return <LoginPage />

   return (
      <div>
        {/* Barra de navegación */}
        <nav style={{
          display: 'flex', alignItems: 'center', gap: 12,
          padding: '12px 24px', background: '#1976d2', color: '#fff'
        }}>
          <span style={{ fontWeight: 'bold', fontSize: 18, marginRight: 16 }}>
            📊 Admin Facturas
          </span>
          <button
            onClick={() => setPage('upload')}
            style={{
              padding: '6px 16px', border: 'none', borderRadius: 6, cursor: 'pointer',
              background: page === 'upload' ? '#fff' : 'transparent',
              color: page === 'upload' ? '#1976d2' : '#fff',
              fontWeight: page === 'upload' ? 'bold' : 'normal',
            }}>
            📤 Carga Excel
          </button>
          <button
            onClick={() => setPage('query')}
            style={{
              padding: '6px 16px', border: 'none', borderRadius: 6, cursor: 'pointer',
              background: page === 'query' ? '#fff' : 'transparent',
              color: page === 'query' ? '#1976d2' : '#fff',
              fontWeight: page === 'query' ? 'bold' : 'normal',
            }}>
            🤖 Consulta IA
          </button>
          <span style={{ marginLeft: 'auto', fontSize: 13, opacity: 0.85 }}>
            {user.email}
          </span>
          <button
            onClick={() => signOut(auth)}
            style={{ padding: '6px 14px', background: '#e53935', color: '#fff',
              border: 'none', borderRadius: 6, cursor: 'pointer' }}>
            Cerrar sesión
          </button>
        </nav>

        {/* Contenido */}
        {page === 'upload' ? <UploadPage /> : <QueryPage />}
      </div>
    )
}
