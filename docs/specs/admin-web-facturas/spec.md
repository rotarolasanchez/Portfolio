# Spec: Admin Web de facturas (carga a BigQuery y consulta con IA)

> Metodología: Spec-First | Estado: Completo | Fecha: 2026-11-20

---

## 1. Visión del Producto

Aplicación web de administración (React + Vite + Firebase) para cargar archivos CSV/Excel de ventas a BigQuery y consultar esos datos en lenguaje natural con IA.

**Problema que resuelve:** Alimentar y consultar la base de facturación sin herramientas técnicas ni SQL.

---

## 2. Usuarios y Casos de Uso

| Usuario | Acción | Resultado esperado |
| --- | --- | --- |
| Administrador | Inicia sesión en el panel | Accede a las páginas de carga y consulta |
| Administrador | Arrastra o selecciona un CSV/Excel | Se previsualizan las filas procesadas |
| Administrador | Pulsa "Cargar filas a BigQuery" | Filas insertadas y se informa la cantidad |
| Administrador | Hace una pregunta en "Consulta IA" | Recibe respuesta basada en los datos |

**Usuario principal:** Administrador

---

## 3. Funcionalidades

### En scope ✅
- Login con Firebase Auth y barra de navegación (Carga Excel / Consulta IA / cerrar sesión).
- Lectura de `.xlsx`, `.xls` y `.csv` (separador `;` tipo cubo SAP) con la librería `xlsx` y normalización de filas en el navegador.
- Envío de filas a la Cloud Function `uploadCuboVentas`, que valida el token Firebase y el cuerpo (`ventas` no vacío) e inserta en BigQuery.
- Cloud Function `uploadExcelToBigQuery` y script `create_cubo_ventas_table.js` para crear la tabla.
- Consulta en lenguaje natural vía `queryFacturas` (Gemini + BigQuery).

### Fuera de scope ❌
- Edición o borrado de registros ya cargados.
- Gestión de usuarios/roles administradores.

---

## 4. Requisitos No Funcionales

- Endpoints exigen `Authorization: Bearer <ID token>`; 401 sin token o inválido, 405 si no es POST, 400 sin datos.
- Timeout de carga 300 s, memoria 512MiB.
- Despliegue en Firebase Hosting (`.firebaserc`).

---

## 5. Criterios de Éxito

- [ ] Solo usuarios autenticados ven las páginas de carga y consulta.
- [ ] Un CSV/Excel válido se previsualiza y se carga mostrando "N filas cargadas".
- [ ] Una petición sin token o con `ventas` vacío es rechazada (401/400).
- [ ] Una pregunta en "Consulta IA" devuelve una respuesta de `queryFacturas`.
- [ ] La tabla de BigQuery puede crearse con el script provisto.

---

## 7. Archivos implementados

- admin-web/src/main.tsx
- admin-web/src/App.tsx
- admin-web/src/firebase.ts
- admin-web/src/pages/LoginPage.tsx
- admin-web/src/pages/UploadPage.tsx
- admin-web/src/pages/QueryPage.tsx
- admin-web/index.html
- admin-web/vite.config.ts
- functions/index.js (uploadCuboVentas, uploadExcelToBigQuery, queryFacturas)
- functions/create_cubo_ventas_table.js
- .firebaserc
