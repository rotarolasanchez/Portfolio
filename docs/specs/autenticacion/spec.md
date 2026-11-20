# Spec: Autenticación de usuarios (Login, registro y recordar credenciales)

> Metodología: Spec-First | Estado: Completo | Fecha: 2026-11-20

---

## 1. Visión del Producto

Permite a los usuarios iniciar sesión (y registrarse) con email y contraseña mediante Firebase en Android, iOS, Desktop y Web, con opción de recordar credenciales y cerrar sesión.

**Problema que resuelve:** Controlar el acceso a la app y a sus funciones (chatbot, consultas) de forma uniforme en todas las plataformas.

---

## 2. Usuarios y Casos de Uso

| Usuario | Acción | Resultado esperado |
| --- | --- | --- |
| Usuario de la app | Ingresa email y contraseña | Queda autenticado y navega a la app |
| Usuario de la app | Marca "Recordar contraseña" | En el próximo inicio las credenciales se precargan |
| Usuario de la app | Cierra sesión | Se limpia la sesión y las credenciales guardadas |
| Usuario nuevo | Se registra con email, contraseña y nombre | Cuenta creada, o error de validación |

**Usuario principal:** Usuario final de la app

---

## 3. Funcionalidades

### En scope ✅
- Inicio de sesión con email y contraseña (`SignInWithEmailUseCase` → `AuthRepository`), con estados de carga, error y éxito (`RequestState`).
- Registro de usuario con validación: email no vacío y contraseña de al menos 6 caracteres (`SignUpUseCase`).
- Persistencia opcional de credenciales ("Recordar contraseña") mediante `CredentialsStorage`, con implementación por plataforma (Android, iOS, Web).
- Precarga de credenciales guardadas al iniciar `AuthViewModel`.
- Cierre de sesión (`LogoutUseCase`) que limpia credenciales y reinicia el estado.
- Mensajes de snackbar para errores/éxito en el login.
- UI de login con Atomic Design (átomos, moléculas, organismos, template y página).

### Fuera de scope ❌
- Login con proveedores sociales (Google, Apple, etc.).
- Recuperación de contraseña.
- Pantalla de registro en la UI (existe el caso de uso, no se verificó flujo de UI dedicado).

---

## 4. Requisitos No Funcionales

- Multiplataforma (KMP): lógica en `commonMain`, implementaciones específicas por plataforma inyectadas con Koin.
- Estado reactivo con `StateFlow`.
- Cubierto con tests unitarios (`SignInWithEmailUseCaseTest`, `SignUpUseCaseTest`, `LogoutUseCaseTest`, `AuthViewModelTest`, `AuthUiStateTest`).
- Nota: las credenciales recordadas incluyen la contraseña; el almacenamiento seguro depende de cada implementación de plataforma.

---

## 5. Criterios de Éxito

- [ ] Con credenciales válidas, `isAuthenticated` pasa a `true` y `user` se completa; con inválidas se muestra el error.
- [ ] El registro falla con email vacío o contraseña < 6 caracteres sin llamar al repositorio.
- [ ] Con "Recordar" activo, tras un login exitoso las credenciales se guardan y se precargan al reabrir.
- [ ] Al cerrar sesión se borran las credenciales y el estado vuelve al inicial.
- [ ] El flujo funciona en Android, iOS, Desktop y Web.

---

## 7. Archivos implementados

- shared/src/commonMain/kotlin/domain/usecases/SignInWithEmailUseCase.kt
- shared/src/commonMain/kotlin/domain/usecases/SignUpUseCase.kt
- shared/src/commonMain/kotlin/domain/usecases/LogoutUseCase.kt
- shared/src/commonMain/kotlin/domain/repositories/AuthRepository.kt
- shared/src/commonMain/kotlin/domain/model/UserModel.kt
- shared/src/commonMain/kotlin/domain/model/RequestState.kt
- shared/src/commonMain/kotlin/data/datasources/AuthDataSourceI.kt
- shared/src/commonMain/kotlin/data/datasources/UserLocalDataSource.kt
- shared/src/commonMain/kotlin/data/entity/UserApp.kt
- shared/src/commonMain/kotlin/data/entity/UserEntity.kt
- shared/src/commonMain/kotlin/data/mappers/UserMapper.kt
- shared/src/commonMain/kotlin/core/storage/CredentialsStorage.kt
- shared/src/commonMain/kotlin/presentation/viewmodels/AuthViewModel.kt
- shared/src/commonMain/kotlin/presentation/state/AuthUiState.kt
- shared/src/commonMain/kotlin/presentation/state/LoginUiState.kt
- shared/src/commonMain/kotlin/presentation/view/pages/LoginPage.kt
- shared/src/commonMain/kotlin/presentation/view/templates/LoginTemplate.kt
- shared/src/commonMain/kotlin/presentation/view/organisms/LoginContent.kt
- shared/src/commonMain/kotlin/presentation/view/organisms/LoginOrganisms.kt
- shared/src/commonMain/kotlin/presentation/view/molecules/LoginMolecules.kt
- shared/src/commonMain/kotlin/presentation/view/atoms/LoginAtoms.kt
- shared/src/commonMain/kotlin/presentation/view/atoms/snackbar.kt
- shared/src/androidMain/kotlin/data/datasources/AuthDataSource.kt
- shared/src/androidMain/kotlin/data/repository/AuthRepositoryImpl.kt
- shared/src/androidMain/kotlin/core/storage/AndroidCredentialsStorage.kt
- shared/src/iosMain/kotlin/data/repository/IosAuthRepositoryImpl.kt
- shared/src/iosMain/kotlin/core/storage/IosCredentialsStorage.kt
- shared/src/wasmJsMain/kotlin/data/repository/WebAuthRepositoryImpl.kt
- shared/src/wasmJsMain/kotlin/core/storage/WebCredentialsStorage.kt
- shared/src/wasmJsMain/kotlin/core/interop/FirebaseInterop.kt
