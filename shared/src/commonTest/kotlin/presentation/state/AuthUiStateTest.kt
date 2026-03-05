package presentation.state

import domain.model.RequestState
import domain.model.UserModel
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class AuthUiStateTest {

    @Test
    fun `default state has empty values`() {
        val state = AuthUiState()

        assertEquals("", state.userCode)
        assertFalse(state.isAuthenticated)
        assertFalse(state.isLoading)
        assertEquals("", state.error)
        assertEquals("", state.userPassword)
        assertIs<RequestState.Idle>(state.loginRequest)
        assertFalse(state.isSnackBarSuccessful)
        assertEquals("", state.snackbarMessage)
        assertNotNull(state.snackbarHostState) // Solo verificar que existe
    }

    @Test
    fun `update userCode`() {
        val state = AuthUiState()
        val newState = state.copy(userCode = "test@example.com")

        assertEquals("test@example.com", newState.userCode)
    }

    @Test
    fun `update loading state`() {
        val state = AuthUiState()
        val loadingState = state.copy(
            isLoading = true,
            loginRequest = RequestState.Loading
        )

        assertTrue(loadingState.isLoading)
        assertIs<RequestState.Loading>(loadingState.loginRequest)
    }

    @Test
    fun `successful login state`() {
        val user = UserModel(id = "1", email = "test@test.com", userName = "Test")
        val state = AuthUiState().copy(
            isAuthenticated = true,
            user = user,
            loginRequest = RequestState.Success(listOf(user)),
            isLoading = false
        )

        assertTrue(state.isAuthenticated)
        assertEquals("test@test.com", state.user.email)
        assertIs<RequestState.Success<List<UserModel>>>(state.loginRequest)
    }

    @Test
    fun `error login state`() {
        val state = AuthUiState().copy(
            isLoading = false,
            error = "Credenciales incorrectas",
            loginRequest = RequestState.Error(Exception("Credenciales incorrectas"))
        )

        assertFalse(state.isLoading)
        assertEquals("Credenciales incorrectas", state.error)
        assertIs<RequestState.Error>(state.loginRequest)
    }

    @Test
    fun `snackbar message update`() {
        val state = AuthUiState()
        val withMessage = state.copy(snackbarMessage = "Sesión iniciada correctamente")

        assertEquals("Sesión iniciada correctamente", withMessage.snackbarMessage)

        val cleared = withMessage.copy(snackbarMessage = "")
        assertEquals("", cleared.snackbarMessage)
    }
}


