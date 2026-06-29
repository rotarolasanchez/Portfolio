/**
 * Script de una sola ejecución para crear la tabla cubo_ventas en BigQuery.
 * Ejecutar desde la carpeta functions/:
 *   node create_cubo_ventas_table.js
 */
const { BigQuery } = require('@google-cloud/bigquery');:

const bigquery = new BigQuery({ projectId: 'portfolio-app-9a4bc' });

const schema = [
  { name: 'fecha_emision',        type: 'STRING' },
  { name: 'documento',            type: 'STRING' },
  { name: 'tipo_venta',           type: 'STRING' },
  { name: 'termino_pago_resumen', type: 'STRING' },
  { name: 'documento_id',         type: 'STRING' },
  { name: 'moneda',               type: 'STRING' },
  { name: 'tipo_cambio',          type: 'FLOAT64' },
  { name: 'precio',               type: 'FLOAT64' },
  { name: 'almacen',              type: 'STRING' },
  { name: 'anio',                 type: 'INT64' },
  { name: 'mes_texto',            type: 'STRING' },
  { name: 'dia_num',              type: 'INT64' },
  { name: 'numero_legal',         type: 'STRING' },
  { name: 'fecha_vencimiento',    type: 'STRING' },
  { name: 'dias_vencimiento',     type: 'INT64' },
  { name: 'fise',                 type: 'STRING' },
  { name: 'precio_antes_dscto',   type: 'FLOAT64' },
  { name: 'sucursal',             type: 'STRING' },
  { name: 'producto_id',          type: 'STRING' },
  { name: 'unidad_medida',        type: 'STRING' },
  { name: 'producto',             type: 'STRING' },
  { name: 'categoria',            type: 'STRING' },
  { name: 'familia',              type: 'STRING' },
  { name: 'sub_familia',          type: 'STRING' },
  { name: 'linea',                type: 'STRING' },
  { name: 'marca',                type: 'STRING' },
  { name: 'grupo_producto',       type: 'STRING' },
  { name: 'cliente_id',           type: 'STRING' },
  { name: 'cliente',              type: 'STRING' },
  { name: 'zona_id',              type: 'STRING' },
  { name: 'zona',                 type: 'STRING' },
  { name: 'cliente_categoria',    type: 'STRING' },
  { name: 'distrito',             type: 'STRING' },
  { name: 'provincia',            type: 'STRING' },
  { name: 'departamento',         type: 'STRING' },
  { name: 'pais',                 type: 'STRING' },
  { name: 'vendedor',             type: 'STRING' },
  { name: 'analista',             type: 'STRING' },
  { name: 'supervisor',           type: 'STRING' },
  { name: 'gerencia',             type: 'STRING' },
  { name: 'unidad_negocio',       type: 'STRING' },
  { name: 'grupo_unidad_negocio', type: 'STRING' },
  { name: 'tipo_gerencia',        type: 'STRING' },
  { name: 'procedencia',          type: 'STRING' },
  { name: 'total_con_impuesto',   type: 'FLOAT64' },
  { name: 'galones',              type: 'FLOAT64' },
  { name: 'total_costo',          type: 'FLOAT64' },
  { name: 'total_sin_impuesto',   type: 'FLOAT64' },
  { name: 'cantidad',             type: 'FLOAT64' },
  { name: 'descuento',            type: 'FLOAT64' },
  { name: 'descuento_financiero', type: 'FLOAT64' },
  { name: 'descuento_porc',       type: 'FLOAT64' },
  { name: 'otros_descuentos',     type: 'FLOAT64' },
  { name: 'barriles',             type: 'FLOAT64' },
  { name: 'created_at',           type: 'TIMESTAMP' },
];

async function main() {
  const dataset = bigquery.dataset('facturas_dataset');
  const table   = dataset.table('cubo_ventas');

  const [exists] = await table.exists();
  if (exists) {
    console.log('✅ La tabla cubo_ventas ya existe.');
    return;
  }

  await dataset.createTable('cubo_ventas', { schema });
  console.log('🎉 Tabla cubo_ventas creada correctamente en BigQuery.');
}

main().catch(err => {
  console.error('❌ Error:', err.message);
  process.exit(1);
});

