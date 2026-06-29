// src/pages/UploadPage.tsx
import { useState, useRef } from 'react'
import * as XLSX from 'xlsx'
import { auth } from '../firebase'
import { signOut } from 'firebase/auth'

const API_BASE = import.meta.env.VITE_API_BASE_URL

interface CuboVentasRow {
  FechaEmision: string
  Documento: string
  TipoVenta: string
  TerminoPago_Resumen: string
  Documento_ID: string
  Moneda: string
  Tipo_Cambio: number
  Precio: number
  Almacen: string
  Anio: number
  Mes_Texto: string
  Dia_Num: number
  NumeroLegal: string
  FechaVencimiento: string
  DiasVencimiento: number
  FISE: string
  PrecioAntesDscto: number
  Sucursal: string
  Producto_ID: string
  UnidadMedida: string
  Producto: string
  Categoria: string
  Familia: string
  SubFamilia: string
  Linea: string
  Marca: string
  GrupoProducto: string
  Cliente_ID: string
  Cliente: string
  Zona_ID: string
  Zona: string
  Cliente_Categoria: string
  Distrito: string
  Provincia: string
  Departamento: string
  Pais: string
  Vendedor: string
  Analista: string
  Supervisor: string
  Gerencia: string
  UnidadNegocio: string
  GrupoUnidadNegocio: string
  TipoGerencia: string
  Procedencia: string
  Total_con_Impuesto: number
  Galones: number
  Total_Costo: number
  Total_sin_Impuesto: number
  Cantidad: number
  Descuento: number
  DescuentoFinanciero: number
  Descuento_Porc: number
  Otros_Descuentos: number
  Barriles: number
  [key: string]: unknown
}

export default function UploadPage() {
  const [rows, setRows]         = useState<CuboVentasRow[]>([])
  const [fileName, setFileName] = useState('')
  const [loading, setLoading]   = useState(false)
  const [result, setResult]     = useState<string | null>(null)
  const inputRef = useRef<HTMLInputElement>(null)

  const handleFile = (file: File) => {
    setFileName(file.name)
    const reader = new FileReader()
    reader.onload = (e) => {
      const data = new Uint8Array(e.target?.result as ArrayBuffer)
      // Separador ; para CSV tipo cubo SAP
      const workbook = XLSX.read(data, { type: 'array', FS: ';' })
      const sheet = workbook.Sheets[workbook.SheetNames[0]]
      const rawOriginal = XLSX.utils.sheet_to_json<Record<string, unknown>>(sheet, { raw: false, defval: '' })

      if (rawOriginal.length === 0) { setResult('❌ Archivo vacío o sin datos'); return }

      // Normalizar claves: eliminar espacios al inicio y al final
      const raw = rawOriginal.map(row =>
        Object.fromEntries(Object.entries(row).map(([k, v]) => [k.trim(), v]))
      )

      console.log('🔍 Claves normalizadas:', Object.keys(raw[0]))
      console.log('🔍 Primera fila:', raw[0])

      const parseN = (v: unknown) => {
        if (v === undefined || v === null || v === '') return 0
        const n = Number(String(v).replace(/,/g, '').trim())
        return isNaN(n) ? 0 : n
      }
      const parseS = (v: unknown) => (v !== undefined && v !== null ? String(v).trim() : '')
      const parseI = (v: unknown) => Math.round(parseN(v))

      const mapped: CuboVentasRow[] = raw.map((f) => ({
        FechaEmision:        parseS(f['FechaEmision']),
        Documento:           parseS(f['Documento']),
        TipoVenta:           parseS(f['TipoVenta']),
        TerminoPago_Resumen: parseS(f['TerminoPago_Resumen']),
        Documento_ID:        parseS(f['Documento_ID']),
        Moneda:              parseS(f['Moneda']),
        Tipo_Cambio:         parseN(f['Tipo_Cambio']),
        Precio:              parseN(f['Precio']),
        Almacen:             parseS(f['Almacen']),
        Anio:                parseI(f['Anio']),
        Mes_Texto:           parseS(f['Mes_Texto']),
        Dia_Num:             parseI(f['Dia_Num']),
        NumeroLegal:         parseS(f['NumeroLegal']),
        FechaVencimiento:    parseS(f['FechaVencimiento']),
        DiasVencimiento:     parseI(f['DiasVencimiento']),
        FISE:                parseS(f['FISE']),
        PrecioAntesDscto:    parseN(f['PrecioAntesDscto']),
        Sucursal:            parseS(f['Sucursal']),
        Producto_ID:         parseS(f['Producto_ID']),
        UnidadMedida:        parseS(f['UnidadMedida']),
        Producto:            parseS(f['Producto']),
        Categoria:           parseS(f['Categoria']),
        Familia:             parseS(f['Familia']),
        SubFamilia:          parseS(f['SubFamilia']),
        Linea:               parseS(f['Linea']),
        Marca:               parseS(f['Marca']),
        GrupoProducto:       parseS(f['GrupoProducto']),
        Cliente_ID:          parseS(f['Cliente_ID']),
        Cliente:             parseS(f['Cliente']),
        Zona_ID:             parseS(f['Zona_ID']),
        Zona:                parseS(f['Zona']),
        Cliente_Categoria:   parseS(f['Cliente_Categoria']),
        Distrito:            parseS(f['Distrito']),
        Provincia:           parseS(f['Provincia']),
        Departamento:        parseS(f['Departamento']),
        Pais:                parseS(f['Pais']),
        Vendedor:            parseS(f['Vendedor']),
        Analista:            parseS(f['Analista']),
        Supervisor:          parseS(f['Supervisor']),
        Gerencia:            parseS(f['Gerencia']),
        UnidadNegocio:       parseS(f['UnidadNegocio']),
        GrupoUnidadNegocio:  parseS(f['GrupoUnidadNegocio']),
        TipoGerencia:        parseS(f['TipoGerencia']),
        Procedencia:         parseS(f['Procedencia']),
        Total_con_Impuesto:  parseN(f['Total_con_Impuesto']),
        Galones:             parseN(f['Galones']),
        Total_Costo:         parseN(f['Total_Costo']),
        Total_sin_Impuesto:  parseN(f['Total_sin_Impuesto']),
        Cantidad:            parseN(f['Cantidad']),
        Descuento:           parseN(f['Descuento']),
        DescuentoFinanciero: parseN(f['DescuentoFinanciero']),
        Descuento_Porc:      parseN(f['Descuento_Porc']),
        Otros_Descuentos:    parseN(f['Otros_Descuentos']),
        Barriles:            parseN(f['Barriles']),
      }))

      console.log('✅ Primera fila mapeada:', mapped[0])
      setRows(mapped)
      setResult(null)
    }
    reader.readAsArrayBuffer(file)
  }


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
      const res = await fetch(`${API_BASE}/uploadCuboVentas`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`,
        },
        body: JSON.stringify({ ventas: rows }),
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
          : '📂 Arrastra tu CSV/Excel aquí o haz clic para seleccionar'}
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
