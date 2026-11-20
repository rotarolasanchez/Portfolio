# Spec: Panel web de administración de facturas

> Metodología: Spec-First | Estado: Completo | Fecha: 2026-11-20

---

## 1. Visión del Producto

Aplicación web React para que un administrador autenticado cargue facturas y ventas desde Excel a BigQuery y consulte los datos con IA.

**Problema que resuelve:** Alimentar BigQuery con datos de ventas sin herramientas técnicas y consultarlos en lenguaje natural.

---

## 2. Usuarios y Casos de Uso

| Usuario | Acción | Resultado esperado |
| --- | --- | --- |
| Administrador | Inicia sesión con Firebase Auth | Accede al panel |
| Administrador | Sube un Excel de ventas | Filas enviadas a `uploadCuboVentas` |
| Administrador | Hace una pregunta en lenguaje natural | Respuesta de IA con SQL y tabla de resultados |
| Administrador | Cierra sesión | Vuelve al login |

**Usuario principal:** Administrador de facturas

---

## 3. Funcionalidades

### En scope ✅
- Login con Firebase Auth y control de sesión con `onAuthStateChanged` (`LoginPage`, `App`)
- Navegación entre Carga Excel y Consulta IA, con email visible y cierre de sesión
- Carga de Excel con `xlsx` y envío al backend con Bearer token (`UploadPage`, interfaz `CuboVentasRow`)
- Consulta IA a `queryFacturas` con preguntas sugeridas, respuesta, SQL y tabla de filas desplegables (`QueryPage`)
- URL del backend configurable con `VITE_API_BASE_URL`

### Fuera de scope ❌
- Edición o borrado de facturas
- Gestión de usuarios
- Gráficos

---

## 4. Requisitos No Funcionales

admin-web-facturas0

---

## 5. Criterios de Éxito

admin-web-facturas1

---

## 7. Archivos implementados

admin-web-facturas2
