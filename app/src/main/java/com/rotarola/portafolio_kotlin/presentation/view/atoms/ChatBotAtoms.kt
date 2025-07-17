package com.rotarola.portafolio_kotlin.presentation.view.atoms

import androidx.compose.foundation.layout.FlowRowScopeInstance.align
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.rotarola.feature_ui.presentation.atoms.EditextM3
import com.rotarola.feature_ui.presentation.atoms.TextM3
import com.rotarola.portafolio_kotlin.R

@Composable
fun ChatBotTextField(
    value: String,
    label: String,
) {
    TextM3(
        id = 0,
        status = true,
        text = value,
        placeholder = "",
        label = label,
        keyboardType = KeyboardType.Text,
        textDownEditext = "",
        trailingiconStatus = false,
        countMaxCharacter = 20,
        resultEditText = { },
        leadingiconColor = MaterialTheme.colorScheme.primary,
        trailingIconOnClick = {},
        trailingiconColor = MaterialTheme.colorScheme.primary,
        leadingiconStatus = false,
        trailingiconResourceId = painterResource(id = R.drawable.baseline_visibility_24),
    )
}

@Composable
fun ChatBotIcon(
    value: String,
    label: String,
) {

    Icon(
        Icons.Default.Person,
        contentDescription = "Usuario",
        modifier = Modifier
            .size(32.dp)
            .padding(start = 8.dp)
            .align(Alignment.Top),
        tint = MaterialTheme.colorScheme.primary
    )
}