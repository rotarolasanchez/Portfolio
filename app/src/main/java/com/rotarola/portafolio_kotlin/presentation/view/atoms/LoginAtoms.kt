package com.rotarola.portafolio_kotlin.presentation.view.atoms

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.rotarola.feature_ui.presentation.atoms.ButtonM3
import com.rotarola.feature_ui.presentation.atoms.EditextM3
import com.rotarola.portafolio_kotlin.R

@Composable
fun LoginTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    leadingIcon: ImageVector,
    isPassword: Boolean = false,
    isPasswordVisible: Boolean = false,
    onPasswordVisibilityChanged: ((Boolean) -> Unit)? = null,
    modifier: Modifier = Modifier.fillMaxWidth(),
    testTag: String = "login_text_field",
    countMaxCharacter: Int = 20
) {
    EditextM3(
        id = 0,
        status = true,
        value = value,
        placeholder = "",
        label = label,
        leadingiconResourceId = rememberVectorPainter(image = leadingIcon),
        keyboardType = KeyboardType.Text,
        textDownEditext = "",
        trailingIconStatus = isPassword,
        countMaxCharacter = countMaxCharacter,
        resultEditText = onValueChange,
        leadingiconColor = MaterialTheme.colorScheme.primary,
        trailingIconOnClick = {},
        isPasswordField = isPassword,
        isPasswordVisible = isPasswordVisible,
        //onPasswordVisibilityChanged = onPasswordVisibilityChanged,
        modifier = modifier,
        onPasswordVisibilityChanged = { newVisibility ->
            onPasswordVisibilityChanged?.invoke(newVisibility)
        },
        trailingiconColor = MaterialTheme.colorScheme.primary,
        leadingIconStatus = true,
        trailingiconResourceId = painterResource(id = R.drawable.baseline_visibility_24),
    )
}

@Composable
fun LoginButton(
    text: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier.fillMaxWidth()
) {
    ButtonM3(
        title = text,
        enabled = enabled,
        leadingiconResourceId = R.drawable.outline_login_24,
        leadingIconStatus = true,
        onClick = onClick,
        modifier  = modifier
            .height(56.dp)
            .padding(10.dp,0.dp)
    )
}