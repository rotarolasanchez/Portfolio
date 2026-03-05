package presentation.state

import androidx.compose.material3.SnackbarHostState
import domain.model.RequestState
import domain.model.UserModel

data class LoginUiState(
    val userCode: String = "",
    val userPassword: String = "",
    val isSnackBarSuccessful: Boolean = false,
    val snackbarHostState: SnackbarHostState = SnackbarHostState(),
    val loginRequest: RequestState<List<UserModel>> = RequestState.Idle,
)