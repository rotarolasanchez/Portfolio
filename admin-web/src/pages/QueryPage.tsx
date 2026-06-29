// src/pages/QueryPage.tsx
import { useState } from 'react'
import { auth } from '../firebase'

const API_BASE = import.meta.env.VITE_API_BASE_URL

interface QueryResult {
  question: string
  sql: string
  rowCount: number
  rows: Record<string, unknown>[]
  answer: string
}

const SUGERENCIAS = [
  '¿Cuánto se vendió en total?',
  '¿Cuáles son los 5 clientes con mayor monto?',
  '¿Cuántas facturas hay por mes?',
  '¿Cuántas facturas están pendientes (estado O)?',
  '¿Cuál es el promedio de factura por cliente?',
]

export default function QueryPage() {
  const [question, setQuestion] = useState('')
  const [loading, setLoading]   = useState(false)
  const [result, setResult]     = useState<QueryResult | null>(null)
  const [error, setError]       = useState<string | null>(null)
  const [showSql, setShowSql]   = useState(false)
  const [showTable, setShowTable] = useState(false)

  const handleQuery = async (q?: string) => {
    const pregunta = q ?? question
    if (!pregunta.trim()) return
    setLoading(true)
    setError(null)
    setResult(null)
    setShowSql(false)
    setShowTable(false)
    try {
      const token = await auth.currentUser?.getIdToken()
      const res = await fetch(`${API_BASE}/queryFacturas`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`,
        },
        body: JSON.stringify({ question: pregunta }),
      })
      const json = await res.json()
      if (res.ok) {
        setResult(json)
        if (q) setQuestion(q)
      } else {
        setError(json.error ?? 'Error desconocido')
      }
    } catch (err) {
      setError(String(err))
    } finally {
      setLoading(false)
    }
  }

  const columns = result?.rows?.length ? Object.keys(result.rows[0]) : []

  return (
    <div style={{ maxWidth: 900, margin: '0 auto', padding: 24 }}>
      <h1>🤖 Consulta de Facturas con IA</h1>

      {/* Sugerencias */}
      <div style={{ marginBottom: 16 }}>
        <p style={{ color: '#666', marginBottom: 8 }}>Preguntas sugeridas:</p>
        <div style={{ display: 'flex', flexWrap: 'wrap', gap: 8 }}>
          {SUGERENCIAS.map(s => (
            <button key={s} onClick={() => handleQuery(s)}
              style={{ padding: '6px 12px', background: '#e3f2fd', border: '1px solid #90caf9',
                borderRadius: 20, cursor: 'pointer', fontSize: 13 }}>
              {s}
            </button>
          ))}
        </div>
      </div>

      {/* Input */}
      <div style={{ display: 'flex', gap: 8, marginBottom: 24 }}>
        <input
          value={question}
          onChange={e => setQuestion(e.target.value)}
          onKeyDown={e => e.key === 'Enter' && handleQuery()}
          placeholder="Escribe tu pregunta sobre las facturas..."
          style={{ flex: 1, padding: '12px 16px', fontSize: 15, borderRadius: 8,
            border: '1px solid #ccc', outline: 'none' }}
        />
        <button onClick={() => handleQuery()} disabled={loading || !question.trim()}
          style={{ padding: '12px 24px', background: '#1976d2', color: '#fff',
            border: 'none', borderRadius: 8, fontSize: 15, cursor: 'pointer' }}>
          {loading ? '⏳' : '🔍 Consultar'}
        </button>
      </div>

      {/* Error */}
      {error && (
        <div style={{ padding: 16, background: '#ffebee', borderRadius: 8,
          border: '1px solid #e57373', marginBottom: 16 }}>
          ❌ {error}
        </div>
      )}

      {/* Respuesta */}
      {result && (
        <div>
          {/* Respuesta IA */}
          <div style={{ padding: 20, background: '#e8f5e9', borderRadius: 8,
            border: '1px solid #81c784', marginBottom: 16 }}>
            <strong>🤖 Respuesta:</strong>
            <p style={{ marginTop: 8, lineHeight: 1.6, whiteSpace: 'pre-wrap' }}>{result.answer}</p>
          </div>

          {/* SQL generado (colapsable) */}
          <button onClick={() => setShowSql(!showSql)}
            style={{ marginRight: 8, padding: '6px 14px', background: '#f5f5f5',
              border: '1px solid #ddd', borderRadius: 6, cursor: 'pointer', fontSize: 13 }}>
            {showSql ? '▲' : '▼'} Ver SQL generado
          </button>

          {/* Tabla de datos (colapsable) */}
          <button onClick={() => setShowTable(!showTable)}
            style={{ padding: '6px 14px', background: '#f5f5f5',
              border: '1px solid #ddd', borderRadius: 6, cursor: 'pointer', fontSize: 13 }}>
            {showTable ? '▲' : '▼'} Ver datos ({result.rowCount} filas)
          </button>

          {showSql && (
            <pre style={{ marginTop: 12, padding: 16, background: '#263238', color: '#80cbc4',
              borderRadius: 8, overflow: 'auto', fontSize: 13 }}>
              {result.sql}
            </pre>
          )}

          {showTable && columns.length > 0 && (
            <div style={{ overflowX: 'auto', marginTop: 12 }}>
              <table style={{ borderCollapse: 'collapse', width: '100%', fontSize: 13 }}>
                <thead>
                  <tr>
                    {columns.map(col => (
                      <th key={col} style={{ border: '1px solid #ddd', padding: '6px 10px',
                        background: '#1976d2', color: '#fff' }}>{col}</th>
                    ))}
                  </tr>
                </thead>
                <tbody>
                  {result.rows.map((row, i) => (
                    <tr key={i} style={{ background: i % 2 === 0 ? '#fff' : '#f5f5f5' }}>
                      {columns.map(col => (
                        <td key={col} style={{ border: '1px solid #ddd', padding: '4px 10px' }}>
                          {String(row[col] ?? '')}
                        </td>
                      ))}
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </div>
      )}
    </div>
  )
}
