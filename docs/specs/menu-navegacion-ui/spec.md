# Spec: Menú, navegación y UI responsiva multiplataforma

> Metodología: Spec-First | Estado: Parcial | Fecha: 2026-11-20

---

## 1. Visión del Producto

Provee la estructura de navegación, el menú principal, el tema visual y los componentes responsivos compartidos en Android, iOS, Web y Desktop con Compose Multiplatform.

**Problema que resuelve:** Dar una experiencia de navegación y visual consistente en todas las plataformas y tamaños de pantalla.

---

## 2. Usuarios y Casos de Uso

| Usuario | Acción | Resultado esperado |
| --- | --- | --- |
| Usuario final | Abre la app | Ve el tema y la navegación principal |
| Usuario final | Selecciona un ítem del menú | El ítem queda seleccionado en el estado |
| Usuario final | Usa la app en distintos tamaños de pantalla | La interfaz se adapta |

**Usuario principal:** Usuario final de la app

---

## 3. Funcionalidades

### En scope ✅
- Punto de entrada `App()` con tema `FeatureUITheme` y `NavigationMain`
- Menú con ítems Home, Profile, Settings y Help y selección (`MenuViewModel.selectMenuItem`), además de `onFabClick`
- Barra superior, menú y plantilla (`MenuTopBar`, `CustomMenu`, `MenuOrganisms`, `MenuTemplate`, `MenuPage`)
- Átomos reutilizables (botón, texto, animación, snackbar, imágenes e íconos seguros por plataforma)
- Tema (colores, tipografía) y utilidades responsivas (`ResponsiveUtils`, `ResponsiveContainer`)
- Entradas por plataforma: Android (`MainActivity`, `PortafolioApplication`), iOS (`MainiOS`, SwiftUI) y Web (wasmJs `Main.kt`)
- Inyección de dependencias con Koin por plataforma
- Las acciones de los ítems del menú no tienen lógica propia más allá de la selección (Parcial)

### Fuera de scope ❌
- Contenido funcional de Profile, Settings y Help
- Internacionalización

---

## 4. Requisitos No Funcionales

menu-navegacion-ui0

---

## 5. Criterios de Éxito

menu-navegacion-ui1

---

## 7. Archivos implementados

menu-navegacion-ui2
