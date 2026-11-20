# Spec: Autenticación de usuarios

> Metodología: Spec-First | Estado: Completo | Fecha: 2026-11-20

---

## 1. Visión del Producto

Permite a los usuarios iniciar sesión, registrarse y cerrar sesión en la app multiplataforma del portafolio mediante Firebase Auth.

**Problema que resuelve:** Controlar el acceso a la app y a los servicios de IA mediante identidad verificada.

---

## 2. Usuarios y Casos de Uso

| Usuario | Acción | Resultado esperado |
| --- | --- | --- |
| Usuario final | Inicia sesión con email y contraseña | Accede a la app y se guarda el usuario en el estado |
| Usuario final | Activa "recordar credenciales" | Las credenciales se guardan en almacenamiento por plataforma |
| Usuario nuevo | Se registra con email, contraseña y nombre | Cuenta creada o error de validación |
| Usuario autenticado | Cierra sesión | Sesión terminada y retorno al login |

**Usuario principal:** Usuario final de la app (Android, iOS, Web, Desktop)

---

## 3. Funcionalidades

### En scope ✅
- Login con email y contraseña (`SignInWithEmailUseCase`, `AuthViewModel.signInWithEmail`)
- Registro con validación: email no vacío y contraseña de mínimo 6 caracteres (`SignUpUseCase`)
- Cierre de sesión (`LogoutUseCase`, `AuthViewModel.logout`)
- Contrato `AuthRepository` con implementaciones Android, iOS y Web, e `signInWithGoogle` y `getCurrentUser` en la interfaz
- Opción de recordar credenciales con `CredentialsStorage` por plataforma
- Estado de UI de autenticación (carga, error, snackbar, `RequestState`)
- Pantalla de login con átomos, moléculas, organismos y plantilla, con animación Lottie

### Fuera de scope ❌
- Recuperación de contraseña
- Roles y permisos de usuario
- Verificar que el flujo de Google Sign-In esté completo en todas las plataformas (solo consta en la interfaz)

---

## 4. Requisitos No Funcionales

autenticacion-usuarios0

---

## 5. Criterios de Éxito

autenticacion-usuarios1

---

## 7. Archivos implementados

autenticacion-usuarios2
