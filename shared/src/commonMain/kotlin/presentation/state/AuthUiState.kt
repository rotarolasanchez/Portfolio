package presentation.state

import androidx.compose.material3.SnackbarHostState
import domain.model.RequestState
import domain.model.UserModel

// presentation/state/AuthUiState.kt
data class AuthUiState(
    val userCode: String = "",
    val isAuthenticated: Boolean = false,
    val isLoading: Boolean = false,
    val error: String="",
    val userPassword: String = "",
    val snackbarHostState: SnackbarHostState = SnackbarHostState(),
    val loginRequest: RequestState<List<UserModel>> = RequestState.Idle,
    val isSnackBarSuccessful: Boolean = false,
    val snackbarMessage: String = "",
    val user: UserModel = UserModel()
)