# 🤖 Configuración del buscador automático de vacantes Android Senior

Este documento explica cómo configurar y usar el workflow de búsqueda automática de empleo
incluido en este repositorio.

---

## ¿Qué hace el workflow?

Todos los días a las **8:00 AM UTC** (3:00 AM hora Perú / Lima), GitHub Actions ejecuta
automáticamente un script de Python que:

1. Consulta tres fuentes gratuitas de vacantes remotas:
   - **Arbeitnow** — trabajos remotos internacionales
   - **Remotive** — trabajos remotos de tecnología
   - **Jobicy** — feed RSS de trabajos remotos
2. Filtra las ofertas por:
   - **Keywords:** Android, Kotlin, Jetpack Compose, Mobile Developer, Android Engineer
   - **Perú:** salario > S/ 7,000 soles (cuando está disponible)
   - **Internacional/Remoto:** salario > $1,900 USD mensuales (cuando está disponible)
   - **Modalidad:** remoto o Perú
3. Envía un reporte HTML por correo a **rotarolasanchez@gmail.com**

---

## Requisitos previos

Debes configurar **dos GitHub Secrets** en tu repositorio antes de que el workflow funcione.

---

## 1. Cómo configurar los GitHub Secrets

### Paso 1 — Abre la configuración del repositorio

1. Ve a <https://github.com/rotarolasanchez/Portfolio>
2. Haz clic en la pestaña **Settings** (Configuración)
3. En el menú lateral izquierdo, selecciona **Secrets and variables → Actions**

### Paso 2 — Crea el secret `GMAIL_USER`

1. Haz clic en **New repository secret**
2. En **Name** escribe: `GMAIL_USER`
3. En **Secret** escribe tu correo Gmail: `rotarolasanchez@gmail.com`
4. Haz clic en **Add secret**

### Paso 3 — Crea el secret `GMAIL_APP_PASSWORD`

1. Haz clic en **New repository secret**
2. En **Name** escribe: `GMAIL_APP_PASSWORD`
3. En **Secret** pega la contraseña de aplicación de Gmail (ver sección siguiente)
4. Haz clic en **Add secret**

---

## 2. Cómo obtener una App Password de Gmail

> ⚠️ **Importante:** No uses tu contraseña normal de Gmail. Google requiere una
> *contraseña de aplicación* (App Password) para acceso SMTP desde scripts externos.

### Paso a paso

1. Abre <https://myaccount.google.com/security>
2. Asegúrate de tener la **Verificación en 2 pasos** activada. Si no la tienes:
   - Haz clic en **Verificación en 2 pasos**
   - Sigue el asistente para activarla (requiere número de teléfono)
3. Una vez activada la verificación en 2 pasos, regresa a
   <https://myaccount.google.com/apppasswords>
   - Si no ves la opción, busca directamente en Google "App Passwords" e inicia sesión
4. En el campo **Nombre**, escribe un nombre descriptivo, por ejemplo: `GitHub Job Search`
5. Haz clic en **Crear**
6. Google te mostrará una contraseña de 16 caracteres, como: `abcd efgh ijkl mnop`
7. **Copia esa contraseña sin los espacios**: `abcdefghijklmnop`
8. Pégala en el secret `GMAIL_APP_PASSWORD` del paso anterior

> 💡 Una vez cerrado ese diálogo, no podrás ver la contraseña de nuevo. Si la pierdes,
> deberás generar una nueva.

---

## 3. Cómo ejecutar el workflow manualmente

Si quieres probar el workflow en cualquier momento sin esperar a las 8:00 AM UTC:

1. Ve a <https://github.com/rotarolasanchez/Portfolio/actions>
2. En el menú lateral izquierdo, selecciona el workflow
   **"🤖 Daily Job Search — Android Senior"**
3. Haz clic en el botón **Run workflow** (lado derecho)
4. Selecciona la rama `main` y haz clic en **Run workflow**
5. En unos minutos recibirás el email con el reporte

---

## 4. Cómo modificar los filtros de búsqueda

El archivo a editar es `.github/scripts/job_search.py`.

### Cambiar las keywords de búsqueda

Busca la variable `KEYWORDS` cerca del inicio del archivo:

```python
KEYWORDS = [
    "android",
    "kotlin",
    "jetpack compose",
    "mobile developer",
    "android engineer",
]
```

Agrega o elimina palabras clave según necesites. La búsqueda es insensible a mayúsculas.

### Cambiar el salario mínimo

Busca las variables de umbral de salario:

```python
MIN_SALARY_PEN = 7000   # S/ soles (Peru)
MIN_SALARY_USD = 1900   # USD per month (international/remote)
```

Modifica los valores según tus expectativas salariales.

### Cambiar el correo destinatario

Busca la variable:

```python
RECIPIENT_EMAIL = "rotarolasanchez@gmail.com"
```

Cámbiala por el correo que desees.

### Cambiar la hora de ejecución

Edita el archivo `.github/workflows/job_search.yml` y modifica la línea:

```yaml
- cron: '0 8 * * *'
```

El formato es `minuto hora día mes día_semana` en UTC.  
Ejemplos:
- `'0 8 * * *'` → todos los días a las 8:00 AM UTC
- `'0 13 * * 1-5'` → lunes a viernes a las 1:00 PM UTC
- `'0 8 * * 1'` → solo los lunes a las 8:00 AM UTC

---

## Resumen de archivos creados

| Archivo | Descripción |
|---|---|
| `.github/workflows/job_search.yml` | Workflow de GitHub Actions (disparador diario) |
| `.github/scripts/job_search.py` | Script Python de búsqueda y envío de email |
| `JOB_SEARCH_SETUP.md` | Este archivo de documentación |

---

## Resolución de problemas

| Problema | Solución |
|---|---|
| El email no llega | Verifica que los secrets `GMAIL_USER` y `GMAIL_APP_PASSWORD` estén correctamente configurados |
| Error de autenticación SMTP | Regenera la App Password de Gmail y actualiza el secret |
| No se encontraron vacantes | Es normal si ese día no hay nuevas ofertas que cumplan los criterios; también se envía un email indicándolo |
| El workflow no aparece en Actions | Asegúrate de que el archivo `.github/workflows/job_search.yml` esté en la rama `main` |
