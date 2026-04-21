// src/pages/UploadPage.tsx
import { useState, useRef } from 'react'
import * as XLSX from 'xlsx'
import { auth } from '../firebase'
import { signOut } from 'firebase/auth'

const API_BASE = import.meta.env.VITE_API_BASE_URL

interface FacturaRow {
  doc_num: number
  doc_date: string
  card_code: string
  card_name: string
  doc_total: number
  doc_currency: string
  doc_status: string
  [key: string]: unknown
}

export default function UploadPage() {
  const [rows, setRows]         = useState<FacturaRow[]>([])
  const [fileName, setFileName] = useState('')
  const [loading, setLoading]   = useState(false)
  const [result, setResult]     = useState<string | null>(null)
  const inputRef = useRef<HTMLInputElement>(null)

const handleFile = (file: File) => {
  setFileName(file.name)
  const reader = new FileReader()
  reader.onload = (e) => {
    const data = new Uint8Array(e.target?.result as ArrayBuffer)
    const workbook = XLSX.read(data, { type: 'array' })
    const sheet = workbook.Sheets[workbook.SheetNames[0]]

    const raw = XLSX.utils.sheet_to_json<Record<string, unknown>>(sheet, { raw: false })

    if (raw.length === 0) { setResult('❌ Excel vacío o sin datos'); return }

    // Mostrar claves reales del Excel para debug
    const firstRowKeys = Object.keys(raw[0])
    console.log('🔍 Claves reales del Excel:', firstRowKeys)
    console.log('🔍 Primera fila completa:', raw[0])

    const parseN = (v: unknown) => {
      if (v === undefined || v === null) return 0
      const n = Number(String(v).replace(/,/g, '').trim())
      return isNaN(n) ? 0 : n
    }
    const parseS = (v: unknown) => v !== undefined && v !== null ? String(v).trim() : ''

    const mapped = raw.map((f) => ({
      DocNum:    parseN(f['DocNum']    ?? f['doc_num']),
      DocDate:   parseS(f['DocDate']   ?? f['doc_date']),
      CardCode:  parseS(f['CardCode']  ?? f['card_code']),
      CardName:  parseS(f['CardName']  ?? f['card_name']),
      DocTotal:  parseN(f['DocTotal']  ?? f['doc_total']),
      DocCur:    parseS(f['DocCur']    ?? f['DocCurrency'] ?? 'PEN'),
      DocStatus: parseS(f['DocStatus'] ?? f['doc_status']  ?? 'O'),
    }))

    console.log('✅ Primera fila mapeada:', mapped[0])
    setRows(mapped as unknown as FacturaRow[])
    setResult(null)
  }
  reader.readAsArrayBuffer(file)
}

{/* DEBUG TEMPORAL — muestra qué se va a enviar */}
{rows.length > 0 && (
  <div style={{ marginTop: 12, padding: 12, background: '#fff3e0', borderRadius: 6, fontSize: 12, fontFamily: 'monospace' }}>
    <strong>🔍 Debug primera fila (lo que se enviará):</strong>
    <pre>{JSON.stringify(rows[0], null, 2)}</pre>
  </div>
)}

  const handleDrop = (e: React.DragEvent) => {
    e.preventDefault()
    const file = e.dataTransfer.files[0]
    if (file) handleFile(file)
  }

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0]
    if (file) handleFile(file)
  }

  // Enviar a Cloud Function → BigQuery
  const handleUpload = async () => {
    if (!rows.length) return
    setLoading(true)
    setResult(null)
    try {
      const token = await auth.currentUser?.getIdToken()
      const res = await fetch(`${API_BASE}/uploadExcelToBigQuery`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`,
        },
        body: JSON.stringify({ facturas: rows }),
      })
      const json = await res.json()
      if (res.ok) {
        setResult(`✅ ${json.inserted} filas cargadas a BigQuery correctamente.`)
        setRows([])
        setFileName('')
      } else {
        setResult(`❌ Error: ${json.error}`)
      }
    } catch (err) {
      setResult(`❌ Error de red: ${String(err)}`)
    } finally {
      setLoading(false)
    }
  }

  const columns = rows.length > 0 ? Object.keys(rows[0]) : []

  return (
    <div style={{ maxWidth: 1100, margin: '0 auto', padding: 24 }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <h1>📊 Carga de Facturas a BigQuery</h1>
        <button onClick={() => signOut(auth)}
          style={{ padding: '6px 16px', background: '#e53935', color: '#fff', border: 'none', borderRadius: 4, cursor: 'pointer' }}>
          Cerrar sesión
        </button>
      </div>

      {/* Zona de drop */}
      <div
        onDrop={handleDrop}
        onDragOver={e => e.preventDefault()}
        onClick={() => inputRef.current?.click()}
        style={{
          border: '2px dashed #1976d2', borderRadius: 8, padding: 40,
          textAlign: 'center', cursor: 'pointer', background: '#f5f9ff', marginBottom: 24,
        }}
      >
        {fileName
          ? `📄 ${fileName} — ${rows.length} filas detectadas`
          : '📂 Arrastra tu Excel aquí o haz clic para seleccionar'}
        <input ref={inputRef} type="file" accept=".xlsx,.xls,.csv"
          style={{ display: 'none' }} onChange={handleInputChange} />
      </div>

      {/* Vista previa */}
      {rows.length > 0 && (
        <>
          <h3>Vista previa ({Math.min(rows.length, 5)} de {rows.length} filas)</h3>
          <div style={{ overflowX: 'auto', marginBottom: 24 }}>
            <table style={{ borderCollapse: 'collapse', width: '100%', fontSize: 13 }}>
              <thead>
                <tr>
                  {columns.map(col => (
                    <th key={col} style={{ border: '1px solid #ddd', padding: '6px 10px', background: '#1976d2', color: '#fff' }}>
                      {col}
                    </th>
                  ))}
                </tr>
              </thead>
              <tbody>
                {rows.slice(0, 5).map((row, i) => (
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

          <button onClick={handleUpload} disabled={loading}
            style={{ padding: '12px 32px', background: '#43a047', color: '#fff', border: 'none', borderRadius: 6, fontSize: 16, cursor: 'pointer' }}>
            {loading ? '⏳ Cargando a BigQuery...' : `🚀 Cargar ${rows.length} filas a BigQuery`}
          </button>
        </>
      )}

      {result && (
        <div style={{ marginTop: 24, padding: 16, borderRadius: 8,
          background: result.startsWith('✅') ? '#e8f5e9' : '#ffebee',
          border: `1px solid ${result.startsWith('✅') ? '#81c784' : '#e57373'}` }}>
          {result}
        </div>
      )}
    </div>
  )
}
