# Spec: CI/CD y despliegue

> Metodología: Spec-First | Estado: Completo | Fecha: 2026-11-20

---

## 1. Visión del Producto

Pipelines de GitHub Actions que validan el código, compilan iOS y despliegan la versión web, además de generar specs SDD automáticamente.

**Problema que resuelve:** Automatizar la verificación y entrega para no depender de procesos manuales.

---

## 2. Usuarios y Casos de Uso

| Usuario | Acción | Resultado esperado |
| --- | --- | --- |
| Desarrollador | Hace push/PR | CI ejecuta lint y pruebas |
| Desarrollador | Publica un tag | CD compila y despliega la web |
| Desarrollador | Ejecuta el build iOS | Se compila con xcodebuild en macOS |

**Usuario principal:** Desarrollador

---

## 3. Funcionalidades

### En scope ✅
- CI (`ci.yml`): JDK 17, instalación de `google-services.json` desde secret, lint con Gradle, subida del reporte y limpieza de archivos sensibles.
- CD (`cd.yml`): disparado por tags; genera `firebase-config.js` desde secrets, usa JDK 17 y Node 20, compila la web y limpia la config sensible.
- Build iOS (`ios-build.yml`): runner macOS-14 con `xcodebuild`.
- Generación de specs SDD (`sdd-generar-specs.yml`).
- Plantilla de issues y `copilot-instructions.md`.

### Fuera de scope ❌
- Publicación automática a Play Store / App Store.

---

## 4. Requisitos No Funcionales

- Los secretos nunca se versionan; se inyectan en ejecución y se eliminan al terminar.
- Uso de caché de Gradle para acelerar los builds.

---

## 5. Criterios de Éxito

- [ ] CI ejecuta lint y publica el reporte.
- [ ] CD se dispara con un tag y despliega la web.
- [ ] El workflow iOS compila el proyecto en macOS.
- [ ] Ningún archivo de credenciales queda en el repositorio tras la ejecución.

---

## 7. Archivos implementados

- .github/workflows/ci.yml
- .github/workflows/cd.yml
- .github/workflows/ios-build.yml
- .github/workflows/sdd-generar-specs.yml
- .github/ISSUE_TEMPLATE/issue_template.md
- .github/copilot-instructions.md
- .firebaserc
