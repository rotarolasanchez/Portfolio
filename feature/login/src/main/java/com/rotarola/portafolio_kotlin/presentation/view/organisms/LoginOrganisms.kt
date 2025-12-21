package com.rotarola.portafolio_kotlin.presentation.view.organisms

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.rotarola.portafolio_kotlin.core.R
import com.rotarola.portafolio_kotlin.core.utils.AppInfo
import com.rotarola.portafolio_kotlin.presentation.view.molecules.LoginActions
import com.rotarola.portafolio_kotlin.presentation.view.molecules.LoginForm

private val CONTENT_PADDING = 20.dp
private val LOGO_BOTTOM_PADDING = 24.dp

@Composable
fun LoginContent(
    userCode: String,
    userPassword: String,
    onUserCodeChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLoginClick: (String, String) -> Unit,
    onGuestClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(CONTENT_PADDING),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.mipmap.capibara_family_not_background),
            contentDescription = "Logo",
            modifier = Modifier.padding(bottom = LOGO_BOTTOM_PADDING)
        )

        LoginForm(
            userCode = userCode,
            userPassword = userPassword,
            onUserCodeChange = onUserCodeChange,
            onPasswordChange = onPasswordChange,
            modifier = Modifier.fillMaxWidth()
        )

        LoginActions(
            onLoginClick = { onLoginClick(userCode, userPassword) },
            onGuestClick = onGuestClick,
            modifier = Modifier.padding()
        )

        Text(
            //text = "Vs ${BuildConfig.VERSION_NAME}",
            text = "Vs ${AppInfo.versionName}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = LOGO_BOTTOM_PADDING)
        )
    }
}
