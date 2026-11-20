# Spec: Menú, navegación y UI responsiva

> Metodología: Spec-First | Estado: Parcial | Fecha: 2026-11-20

---

## 1. Visión del Producto

Pantalla principal con menú de navegación entre secciones de la app, tema visual común y componentes que se adaptan a distintos tamaños de pantalla.

**Problema que resuelve:** Dar acceso ordenado a las secciones de la app con una experiencia consistente en móvil, escritorio y web.

---

## 2. Usuarios y Casos de Uso

| Usuario | Acción | Resultado esperado |
| --- | --- | --- |
| Usuario autenticado | Abre el menú y selecciona "ChatBot" | Navega al chatbot |
| Usuario en pantalla grande o pequeña | Usa la app | La interfaz se adapta al tamaño |

**Usuario principal:** Usuario autenticado de la app

---

## 3. Funcionalidades

### En scope ✅
- Menú con ítems "ChatBot", "Perfil", "Configuración" y "Ayuda", con íconos y selección (`MenuViewModel`).
- Estructura Atomic Design de la página de menú (`MenuPage`, `MenuTemplate`, `MenuOrganisms`, `MenuTopBar`, `CustomMenu`).
- Navegación principal y raíz de la app con tema (`App`, `AppPage` con variantes por plataforma).
- Tema (colores, tipografía) y componentes responsivos (`ResponsiveUtils`, `ResponsiveContainer`).
- Inyección de dependencias con Koin y proveedores de ViewModels.

### Fuera de scope ❌
- Contenido de las secciones "Perfil", "Configuración" y "Ayuda" (solo existen como ítems de menú).
- Acción del botón flotante (`onFabClick` está vacío).

---

## 4. Requisitos No Funcionales

- Compose Multiplatform compartido en `commonMain`, con `expect/actual` por plataforma.
- Diseño responsivo (ver RESPONSIVE_IMPROVEMENTS.md).
- Tests: `MenuUiStateTest`.

---

## 5. Criterios de Éxito

- [ ] El menú muestra los cuatro ítems con su ícono.
- [ ] Seleccionar un ítem actualiza `selectedItem` en el estado.
- [ ] La app aplica el tema `FeatureUITheme` y muestra la navegación principal.
- [ ] El layout se adapta a distintos tamaños de pantalla.
- [ ] Perfil, Configuración y Ayuda tienen contenido real (pendiente).

---

## 7. Archivos implementados

- shared/src/commonMain/kotlin/App.kt
- shared/src/commonMain/kotlin/presentation/viewmodels/MenuViewModel.kt
- shared/src/commonMain/kotlin/presentation/state/MenuUiState.kt
- shared/src/commonMain/kotlin/presentation/view/pages/MenuPage.kt
- shared/src/commonMain/kotlin/presentation/view/pages/AppPage.kt
- shared/src/commonMain/kotlin/presentation/view/pages/ViewModelProviders.kt
- shared/src/commonMain/kotlin/presentation/view/templates/MenuTemplate.kt
- shared/src/commonMain/kotlin/presentation/view/organisms/MenuOrganisms.kt
- shared/src/commonMain/kotlin/presentation/view/molecules/MenuTopBar.kt
- shared/src/commonMain/kotlin/presentation/view/atoms/CustomMenu.kt
- shared/src/commonMain/kotlin/presentation/view/atoms/CustomButton.kt
- shared/src/commonMain/kotlin/presentation/view/atoms/CustomText.kt
- shared/src/commonMain/kotlin/presentation/view/atoms/CustomAnimation.kt
- shared/src/commonMain/kotlin/presentation/view/atoms/SafeImage.kt
- shared/src/commonMain/kotlin/presentation/view/atoms/SafeIconPainter.kt
- shared/src/commonMain/kotlin/presentation/view/atoms/ResponsiveContainer.kt
- shared/src/commonMain/kotlin/presentation/view/atoms/theme/Color.kt
- shared/src/commonMain/kotlin/presentation/view/atoms/theme/Theme.kt
- shared/src/commonMain/kotlin/presentation/view/atoms/theme/Type.kt
- shared/src/commonMain/kotlin/presentation/utils/ResponsiveUtils.kt
- shared/src/commonMain/kotlin/di/CommonModule.kt
- shared/src/androidMain/kotlin/presentation/view/pages/AppPage.android.kt
- shared/src/iosMain/kotlin/presentation/view/pages/AppPage.ios.kt
- shared/src/desktopMain/kotlin/presentation/view/pages/AppPage.desktop.kt
- shared/src/wasmJsMain/kotlin/presentation/view/pages/AppPage.wasm.kt
- shared/src/wasmJsMain/kotlin/Main.kt
- shared/src/iosMain/kotlin/MainiOS.kt
- app/src/main/java/com/rotarola/portafolio_kotlin/MainActivity.kt
- app/src/main/java/com/rotarola/portafolio_kotlin/PortafolioApplication.kt
- iosApp/iosApp/ContentView.swift
- iosApp/iosApp/iOSApp.swift
