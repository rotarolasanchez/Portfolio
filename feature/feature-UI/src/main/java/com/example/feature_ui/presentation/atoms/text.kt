package com.rotarola.feature_ui.presentation.atoms

import android.widget.Toast
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rotarola.feature_ui.R
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import com.rotarola.feature_ui.presentation.view.theme.BlueVistony
import com.rotarola.feature_ui.presentation.view.theme.Feature_UITheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun TextM3(
    id:Int=0,
    status: Boolean,
    text:String,
    placeholder:String,
    label:String,
    leadingiconResourceId: Painter = rememberVectorPainter(image = Icons.Filled.CheckCircle),
    keyboardType: KeyboardType=KeyboardType.Text,
    statusMaxCharacter:Boolean=true,
    countMaxCharacter:Int=254,
    leadingiconColor: Color = Color.Gray,
    textDownEditext:String="",
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val context = LocalContext.current
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .padding(10.dp)
            .fillMaxWidth()
    ) {
        Column {
            OutlinedTextField(
                enabled = status,
                singleLine = false,
                value = text,
                onValueChange =
                {
                },
                placeholder = {
                    Text(text = placeholder, fontSize = 14.sp)
                },
                label = { Text(label, fontSize = 14.sp) },
                leadingIcon = {
                    Icon(
                        painter = if (text.isNotBlank()) rememberVectorPainter(image = Icons.Filled.CheckCircle) else leadingiconResourceId,
                        contentDescription = null,
                        tint = if (text.isNotBlank()) BlueVistony else (leadingiconColor)
                    )
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = keyboardType,
                    imeAction = ImeAction.Go
                ),
                keyboardActions = KeyboardActions(
                    onGo = {
                        keyboardController?.hide()
                    },
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.End)
            )
            if (statusMaxCharacter) {
                Row {
                    Row(horizontalArrangement = Arrangement.Start) {
                        Text(
                            text = textDownEditext,
                            style = MaterialTheme.typography.bodySmall,
                            color = if (text.length > countMaxCharacter) Color.Red else Color.Gray,
                            modifier = Modifier
                                //.align(Alignment.BottomEnd)
                                .padding(10.dp, 0.dp)
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Text(
                            text = "${text.length}/$countMaxCharacter",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (text.length > countMaxCharacter) Color.Red else Color.Gray,
                            modifier = Modifier
                                //.align(Alignment.BottomEnd)
                                .padding(10.dp, 0.dp)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditextM3(
        id: Int = 0,
        status: Boolean,
        value: String,
        placeholder: String,
        label: String,
        leadingiconChange: Boolean = false,
        leadingiconResourceId: Painter,
        keyboardType: KeyboardType,
        statusMaxCharacter: Boolean = true,
        countMaxCharacter: Int = 254,
        limitNumericStatus: Boolean = false,
        limitNumericNumber: Double = 0.0,
        trailingiconResourceId: Painter,
        //leadingiconColor: Color = MaterialTheme.colorScheme.primary,
        //trailingiconColor: Color = MaterialTheme.colorScheme.primary,
        textDownEditext: String = "",
        trailingiconStatus: Boolean = false,
        trailingiconEvent: (String) -> Unit,
        isPasswordField: Boolean = false,     // Nuevo parámetro para campos de contraseña
        isPasswordVisible: Boolean = false,   // Estado de visibilidad del texto
        onPasswordVisibilityChanged: (Boolean) -> Unit = {}, // Evento para alternar visibilidad
        resultEditText: (String) -> Unit
    ) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val text = remember { mutableStateOf(value) }
    val context = LocalContext.current

    // Debounce implementation
    val debouncedText = remember { mutableStateOf(value) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(text.value) {
        coroutineScope.launch {
            delay(300) // Debounce delay
            if (text.value == debouncedText.value) {
                resultEditText(text.value)
            }
        }
    }
    /*Feature_UITheme(
        darkTheme = isSystemInDarkTheme(),
        dynamicColor = true
    ) {*/
        //val colors = MaterialTheme.colorScheme // Obtén el colorScheme del tema
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth()
        ) {
            Column {
                OutlinedTextField(
                    enabled = status,
                    singleLine = false,
                    value = text.value,
                    onValueChange = { newText ->
                        if (newText.length <= countMaxCharacter) {
                            val cleanedText = when (keyboardType) {
                                KeyboardType.Decimal -> {
                                    newText.filterIndexed { index, char ->
                                        char.isDigit() || (char == '.' && index == newText.indexOf(
                                            '.'
                                        ))
                                    }.let { filteredText ->
                                        if (limitNumericStatus && filteredText.toDoubleOrNull()
                                                ?.let { it > limitNumericNumber } == true
                                        ) {
                                            Toast.makeText(
                                                context,
                                                "El valor debe ser menor o igual a $limitNumericNumber",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            text.value
                                        } else {
                                            filteredText
                                        }
                                    }
                                }

                                else -> newText
                            }
                            text.value = cleanedText
                            debouncedText.value = cleanedText
                        }
                    },
                    placeholder = { Text(text = placeholder, fontSize = 14.sp) },
                    label = { Text(label, fontSize = 14.sp) },
                    leadingIcon = {
                        Icon(
                            painter =
                            if (leadingiconChange) {
                                if (text.value.isNotBlank()) rememberVectorPainter(image = Icons.Filled.CheckCircle) else leadingiconResourceId
                            } else {
                                leadingiconResourceId
                            },
                            contentDescription = null,
                            tint = //if (text.value.isNotBlank()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                            MaterialTheme.colorScheme.primary
                        )
                    },
                    trailingIcon = {
                        if (isPasswordField) {
                            IconButton(onClick = {
                                onPasswordVisibilityChanged(!isPasswordVisible)
                            }) {
                                Icon(
                                    painter = trailingiconResourceId,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        } else if (trailingiconStatus) {
                            // Opción para otros usos del trailingIcon
                            IconButton(onClick = {
                                trailingiconEvent(text.value)
                            }) {
                                Icon(
                                    painter = trailingiconResourceId,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = keyboardType,
                        imeAction = ImeAction.Go
                    ),
                    keyboardActions = KeyboardActions(
                        onGo = { keyboardController?.hide() }
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("myEditText"),
                    visualTransformation =
                    //if (keyboardType == KeyboardType.Password) PasswordVisualTransformation() else VisualTransformation.None
                    if (isPasswordField && !isPasswordVisible) {
                        PasswordVisualTransformation()
                    } else {
                        VisualTransformation.None
                    },
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedTextColor = MaterialTheme.colorScheme.primary,
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        cursorColor = MaterialTheme.colorScheme.primary,
                        //leadingIconColor = leadingiconColor,
                        //trailingIconColor = trailingiconColor
                    ),
                    textStyle = MaterialTheme.typography.bodyMedium,
                )
                if (statusMaxCharacter) {
                    Row {
                        Row(horizontalArrangement = Arrangement.Start) {
                            Text(
                                text = textDownEditext,
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (text.value.length > countMaxCharacter) Color.Red else Color.Gray,
                                modifier = Modifier.padding(10.dp, 0.dp)
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Text(
                                text = "${text.value.length}/$countMaxCharacter",
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (text.value.length > countMaxCharacter) Color.Red else Color.Gray,
                                modifier = Modifier.padding(10.dp, 0.dp)
                            )
                        }
                    }
                }
            }
        }
}

@Composable
fun SimpleText(
    text: String,
    textAlign: TextAlign = TextAlign.Center
) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        textAlign = textAlign,
        color = MaterialTheme.colorScheme.onSurface
    )
}
