package com.rotarola.feature_ui.presentation.atoms

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import com.rotarola.feature_ui.presentation.view.theme.BlueVistony
import com.rotarola.portafolio_kotlin.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class,
    ExperimentalMaterial3Api::class
)

@Composable
fun TextM3(
    id:Int=0,
    status: Boolean,
    text:String,
    placeholder:String,
    label:String,
    leadingiconResourceId:Painter = painterResource(id = R.drawable.baseline_home_24),
    keyboardType:KeyboardType,
    statusMaxCharacter:Boolean=true,
    countMaxCharacter:Int=254,
    limitNumericStatus:Boolean=false,
    limitNumericNumber:Double=0.0,
    trailingiconResourceId:Painter,
    leadingiconColor:Color,
    trailingiconColor:Color,
    textDownEditext:String="",
    trailingiconStatus:Boolean=false,
    trailingIconOnClick:(String) -> Unit,
    resultEditText: (String) -> Unit,
    leadingiconStatus:Boolean=true,
    leadingIconOnClick:(String) -> Unit = { _ ->  },
    readOnly:Boolean=false
){
    val keyboardController = LocalSoftwareKeyboardController.current
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .padding(10.dp)
            .fillMaxWidth()
    ) {
        Column {
            if(trailingiconStatus&&leadingiconStatus){
                OutlinedTextField(
                    readOnly = readOnly,
                    enabled = status,
                    singleLine = false,
                    value = text,
                    onValueChange =
                        {
                        },
                    label = {
                        Text(
                            text = label,
                            fontSize = 14.sp,
                            color = if (readOnly) Color.Gray else Color.Unspecified
                        )},
                    leadingIcon = {
                        if (leadingiconStatus) {
                            IconButton(onClick = {
                                leadingIconOnClick("")
                            }) {
                                Icon(
                                    painter = leadingiconResourceId,
                                    contentDescription = null,
                                    tint = if(status){leadingiconColor}else{Color.LightGray}
                                )
                            }
                        } else {
                            null
                        }
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
                        .align(Alignment.End),
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
                    trailingIcon = {
                        if (trailingiconStatus) {
                            IconButton(onClick = {
                                trailingIconOnClick("")
                            }) {
                                Icon(
                                    painter = trailingiconResourceId,
                                    contentDescription = null,
                                    tint = if(status){trailingiconColor}else{Color.LightGray}
                                    //tint = trailingiconColor
                                )
                            }
                        }
                    }
                )
            }else if(!leadingiconStatus&&trailingiconStatus) {
                OutlinedTextField(
                    readOnly = readOnly,
                    enabled = status,
                    singleLine = false,
                    value = text,
                    onValueChange =
                        {
                        },
                    /*)placeholder = {
                        Text(text = placeholder, fontSize = 14.sp)
                    }*/
                    label = {
                        Text(
                            text = label,
                            fontSize = 14.sp,
                            color = if (readOnly) Color.Gray else Color.Unspecified
                        )},
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
                        .align(Alignment.End),
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
                    trailingIcon = {
                        if (trailingiconStatus) {
                            IconButton(onClick = {
                                trailingIconOnClick("")
                            }) {
                                Icon(
                                    painter = trailingiconResourceId,
                                    contentDescription = null,
                                    //tint = trailingiconColor
                                    tint = if(status){trailingiconColor}else{Color.LightGray}
                                )
                            }
                        } else {
                            null
                        }
                    }
                )
            }else if(leadingiconStatus&&!trailingiconStatus) {
                OutlinedTextField(
                    readOnly = readOnly,
                    enabled = status,
                    singleLine = false,
                    value = text,
                    onValueChange =
                        {
                        },
                    /*)placeholder = {
                        Text(text = placeholder, fontSize = 14.sp)
                    }*/
                    label = {
                        Text(
                            text = label,
                            fontSize = 14.sp,
                            color = if (readOnly) Color.Gray else Color.Unspecified
                        )},
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
                        .align(Alignment.End),
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
                    leadingIcon = {
                        if (leadingiconStatus) {
                            IconButton(onClick = {
                                leadingIconOnClick("")
                            }) {
                                Icon(
                                    painter = leadingiconResourceId,
                                    contentDescription = null,
                                    //tint = trailingiconColor
                                    tint = if(status){leadingiconColor}else{Color.LightGray}
                                )
                            }
                        } else {
                            null
                        }
                    },
                )
            }else if(!leadingiconStatus&&!trailingiconStatus) {
                OutlinedTextField(
                    readOnly = readOnly,
                    enabled = status,
                    singleLine = false,
                    value = text,
                    onValueChange =
                        {
                        },
                    /*)placeholder = {
                        Text(text = placeholder, fontSize = 14.sp)
                    }*/
                    label = {
                        Text(
                            text = label,
                            fontSize = 14.sp,
                            color = if (readOnly) Color.Gray else Color.Unspecified
                        )},
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
                        .align(Alignment.End),
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
                    textStyle = MaterialTheme.typography.bodyMedium
                )
            }
            if (statusMaxCharacter)
            {
                Row {
                    Row( horizontalArrangement = Arrangement.Start){
                        Text(
                            text = textDownEditext,
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (text.length > countMaxCharacter) Color.Red else Color.Gray,
                            modifier = Modifier
                                //.align(Alignment.BottomEnd)
                                .padding(10.dp, 0.dp)
                        )
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End){
                        Text(
                            text = "${text.length}/$countMaxCharacter",
                            style = MaterialTheme.typography.bodyMedium,
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

/*
@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun EditextM3(
    id:Int=0,
    status: Boolean,
    value:String,
    placeholder:String,
    label:String,
    leadingiconResourceId:Painter,
    keyboardType:KeyboardType,
    statusMaxCharacter:Boolean=true,
    countMaxCharacter:Int=254,
    limitNumericStatus:Boolean=false,
    limitNumericNumber:Double=0.0,
    trailingiconResourceId:Painter,
    leadingiconColor:Color,
    trailingiconColor:Color,
    textDownEditext:String="",
    trailingIconStatus:Boolean=false,
    trailingIconOnClick:(String) -> Unit,
    resultEditText: (String) -> Unit,
    leadingIconStatus: Boolean=false,
    statusTextDownEditext:Boolean=true,
    readOnly: Boolean= false,
    isPasswordField: Boolean = false,     // Nuevo parámetro para campos de contraseña
    isPasswordVisible: Boolean = false,   // Estado de visibilidad del texto
    onPasswordVisibilityChanged: (Boolean) -> Unit = {}, // Evento para alternar visibilidad,
    testTag: String = "",
    modifier : Modifier = Modifier,
    leadingIconOnClick:(String) -> Unit = { _ ->  },
){
    val keyboardController = LocalSoftwareKeyboardController.current
    val text = remember { mutableStateOf(value) }
    val context = LocalContext.current
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .padding(10.dp)
            .fillMaxWidth()
    ) {
        Column {
            if(leadingIconStatus&&trailingIconStatus)
            {
                OutlinedTextField(
                    readOnly = readOnly,
                    enabled = status,
                    singleLine = false,
                    value = text.value,
                    onValueChange =
                        { newText ->
                            if (newText.length <= countMaxCharacter) {
                                when (keyboardType) {
                                    KeyboardType.Decimal -> {
                                        if ((newText.isNotEmpty() && newText.first() != '.') || newText.length > 1) {
                                            val cleanedText = newText.filterIndexed { index, char ->
                                                char.isDigit() || (char == '.' && index == newText.indexOf(
                                                    '.'
                                                ))
                                            }
                                            if (limitNumericStatus) {
                                                if (cleanedText.toDoubleOrNull() != null && cleanedText.toDoubleOrNull()!! <= limitNumericNumber) {
                                                    text.value = cleanedText
                                                    resultEditText(cleanedText)
                                                } else {
                                                    Toast.makeText(
                                                        context,
                                                        "El valor debe ser menor o igual a $limitNumericNumber",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                            } else {
                                                if (cleanedText.toDoubleOrNull() != null) {
                                                    text.value = cleanedText
                                                    resultEditText(cleanedText)
                                                }
                                            }
                                        } else if (newText == ".") {
                                            text.value = ""
                                            resultEditText("")
                                        } else {
                                            text.value = newText
                                            resultEditText(newText)
                                        }
                                    }

                                    else -> {
                                        text.value = newText
                                        resultEditText(newText)
                                    }
                                }
                            }
                        },
                    /*placeholder = {
                        Text(text = placeholder, fontSize = 14.sp)
                    },*/
                    label = {
                        Text(
                            text = label,
                            fontSize = 14.sp,
                            color = Color.Gray
                        )},
                    leadingIcon = {
                        /*Icon(
                            painter = leadingiconResourceId,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )*/
                        IconButton(onClick = {
                            leadingIconOnClick(text.value)
                        }) {
                            Icon(
                                painter = leadingiconResourceId,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
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
                        } else if (trailingIconStatus) {
                            // Opción para otros usos del trailingIcon
                            IconButton(onClick = {
                                trailingIconOnClick(text.value)
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
                        onGo = {
                            keyboardController?.hide()
                        },
                    ),
                    modifier = modifier,
                    //modifier = Modifier.fillMaxWidth().testTag(testTag),
                    visualTransformation =
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
                    textStyle = MaterialTheme.typography.bodyMedium
                )
            }else if(!leadingIconStatus&&trailingIconStatus){
                OutlinedTextField(
                    readOnly = readOnly,
                    enabled = status,
                    singleLine = false,
                    value = text.value,
                    onValueChange =
                        { newText ->
                            if (newText.length <= countMaxCharacter) {
                                when (keyboardType) {
                                    KeyboardType.Decimal -> {
                                        if ((newText.isNotEmpty() && newText.first() != '.') || newText.length > 1) {
                                            val cleanedText = newText.filterIndexed { index, char ->
                                                char.isDigit() || (char == '.' && index == newText.indexOf(
                                                    '.'
                                                ))
                                            }
                                            if (limitNumericStatus) {
                                                if (cleanedText.toDoubleOrNull() != null && cleanedText.toDoubleOrNull()!! <= limitNumericNumber) {
                                                    text.value = cleanedText
                                                    resultEditText(cleanedText)
                                                } else {
                                                    Toast.makeText(
                                                        context,
                                                        "El valor debe ser menor o igual a $limitNumericNumber",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                            } else {
                                                if (cleanedText.toDoubleOrNull() != null) {
                                                    text.value = cleanedText
                                                    resultEditText(cleanedText)
                                                }
                                            }
                                        } else if (newText == ".") {
                                            text.value = ""
                                            resultEditText("")
                                        } else {
                                            text.value = newText
                                            resultEditText(newText)
                                        }
                                    }

                                    else -> {
                                        text.value = newText
                                        resultEditText(newText)
                                    }
                                }
                            }
                        },
                    /*placeholder = {
                        Text(text = placeholder, fontSize = 14.sp)
                    },*/
                    label = {
                        Text(
                            text = label,
                            fontSize = 14.sp,
                            color = Color.Gray
                        )},
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
                        } else if (trailingIconStatus) {
                            // Opción para otros usos del trailingIcon
                            IconButton(onClick = {
                                trailingIconOnClick(text.value)
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
                        onGo = {
                            keyboardController?.hide()
                        },
                    ),
                    modifier = Modifier.fillMaxWidth().testTag(testTag),
                    visualTransformation =
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
                    textStyle = MaterialTheme.typography.bodyMedium
                )
            }else if(leadingIconStatus&&!trailingIconStatus){
                OutlinedTextField(
                    readOnly = readOnly,
                    enabled = status,
                    singleLine = false,
                    value = text.value,
                    onValueChange =
                        { newText ->
                            if (newText.length <= countMaxCharacter) {
                                when (keyboardType) {
                                    KeyboardType.Decimal -> {
                                        if ((newText.isNotEmpty() && newText.first() != '.') || newText.length > 1) {
                                            val cleanedText = newText.filterIndexed { index, char ->
                                                char.isDigit() || (char == '.' && index == newText.indexOf(
                                                    '.'
                                                ))
                                            }
                                            if (limitNumericStatus) {
                                                if (cleanedText.toDoubleOrNull() != null && cleanedText.toDoubleOrNull()!! <= limitNumericNumber) {
                                                    text.value = cleanedText
                                                    resultEditText(cleanedText)
                                                } else {
                                                    Toast.makeText(
                                                        context,
                                                        "El valor debe ser menor o igual a $limitNumericNumber",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                            } else {
                                                if (cleanedText.toDoubleOrNull() != null) {
                                                    text.value = cleanedText
                                                    resultEditText(cleanedText)
                                                }
                                            }
                                        } else if (newText == ".") {
                                            text.value = ""
                                            resultEditText("")
                                        } else {
                                            text.value = newText
                                            resultEditText(newText)
                                        }
                                    }

                                    else -> {
                                        text.value = newText
                                        resultEditText(newText)
                                    }
                                }
                            }
                        },
                    /*placeholder = {
                        Text(text = placeholder, fontSize = 14.sp)
                    },*/
                    label = {
                        Text(
                            text = label,
                            fontSize = 14.sp,
                            color = Color.Gray
                        )},
                    leadingIcon = {
                        /*Icon(
                            painter = leadingiconResourceId,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )*/
                        IconButton(onClick = {
                            leadingIconOnClick(text.value)
                        }) {
                            Icon(
                                painter = leadingiconResourceId,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
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
                    modifier = Modifier.fillMaxWidth().testTag(testTag),
                    visualTransformation =
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
                    textStyle = MaterialTheme.typography.bodyMedium
                )
            }else if(!leadingIconStatus&&!trailingIconStatus){
                OutlinedTextField(
                    readOnly = readOnly,
                    enabled = status,
                    singleLine = false,
                    value = text.value,
                    onValueChange =
                        { newText ->
                            if (newText.length <= countMaxCharacter) {
                                when (keyboardType) {
                                    KeyboardType.Decimal -> {
                                        if ((newText.isNotEmpty() && newText.first() != '.') || newText.length > 1) {
                                            val cleanedText = newText.filterIndexed { index, char ->
                                                char.isDigit() || (char == '.' && index == newText.indexOf(
                                                    '.'
                                                ))
                                            }
                                            if (limitNumericStatus) {
                                                if (cleanedText.toDoubleOrNull() != null && cleanedText.toDoubleOrNull()!! <= limitNumericNumber) {
                                                    text.value = cleanedText
                                                    resultEditText(cleanedText)
                                                } else {
                                                    Toast.makeText(
                                                        context,
                                                        "El valor debe ser menor o igual a $limitNumericNumber",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                            } else {
                                                if (cleanedText.toDoubleOrNull() != null) {
                                                    text.value = cleanedText
                                                    resultEditText(cleanedText)
                                                }
                                            }
                                        } else if (newText == ".") {
                                            text.value = ""
                                            resultEditText("")
                                        } else {
                                            text.value = newText
                                            resultEditText(newText)
                                        }
                                    }

                                    else -> {
                                        text.value = newText
                                        resultEditText(newText)
                                    }
                                }
                            }
                        },
                    /*placeholder = {
                        Text(text = placeholder, fontSize = 14.sp)
                    },*/
                    label = {
                        Text(
                            text = label,
                            fontSize = 14.sp,
                            color = Color.Gray
                        )},
                    keyboardOptions = KeyboardOptions(
                        keyboardType = keyboardType,
                        imeAction = ImeAction.Go
                    ),
                    keyboardActions = KeyboardActions(
                        onGo = {
                            keyboardController?.hide()
                        },
                    ),
                    modifier = Modifier.fillMaxWidth().testTag(testTag),
                    visualTransformation =
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
                    textStyle = MaterialTheme.typography.bodyMedium
                )
            }

            Row {
                if(statusTextDownEditext) {
                    Row(horizontalArrangement = Arrangement.Start) {
                        Text(
                            text = textDownEditext,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray,
                            modifier = Modifier
                                //.align(Alignment.BottomEnd)
                                .padding(10.dp, 0.dp)
                        )
                    }
                }
                if (statusMaxCharacter)
                {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End){
                        Text(
                            text = "${text.value.length}/$countMaxCharacter",
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (text.value.length > countMaxCharacter) Color.Red else Color.Gray,
                            modifier = Modifier
                                //.align(Alignment.BottomEnd)
                                .padding(10.dp, 0.dp)
                        )
                    }
                }
            }
        }
    }
}*/
@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun EditextM3(
    id: Int = 0,
    status: Boolean,
    value: String, // Usar directamente el valor del padre
    placeholder: String,
    label: String,
    leadingiconResourceId: Painter,
    keyboardType: KeyboardType,
    statusMaxCharacter: Boolean = true,
    countMaxCharacter: Int = 254,
    limitNumericStatus: Boolean = false,
    limitNumericNumber: Double = 0.0,
    trailingiconResourceId: Painter,
    leadingiconColor: Color,
    trailingiconColor: Color,
    textDownEditext: String = "",
    trailingIconStatus: Boolean = false,
    trailingIconOnClick: (String) -> Unit,
    resultEditText: (String) -> Unit,
    leadingIconStatus: Boolean = false,
    statusTextDownEditext: Boolean = true,
    readOnly: Boolean = false,
    isPasswordField: Boolean = false,
    isPasswordVisible: Boolean = false,
    onPasswordVisibilityChanged: (Boolean) -> Unit = {},
    testTag: String = "",
    modifier: Modifier = Modifier,
    leadingIconOnClick: (String) -> Unit = { _ -> },
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val context = LocalContext.current

    // ELIMINADO: val text = remember { mutableStateOf(value) }
    // Ahora usa directamente 'value' del padre

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .padding(10.dp)
            .fillMaxWidth()
    ) {
        Column {
            OutlinedTextField(
                readOnly = readOnly,
                enabled = status,
                singleLine = false,
                value = value, // Usar directamente el valor del padre
                onValueChange = { newText ->
                    if (statusMaxCharacter) {
                        if (newText.length <= countMaxCharacter) {
                            resultEditText(newText) // Notificar al padre
                        }
                    } else {
                        resultEditText(newText) // Notificar al padre
                    }
                },
                placeholder = if (placeholder.isNotEmpty()) {
                    { Text(text = placeholder, fontSize = 14.sp) }
                } else null,
                label = {
                    Text(
                        text = label,
                        fontSize = 14.sp,
                        color = if (readOnly) Color.Gray else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                leadingIcon = if (leadingIconStatus) {
                    {
                        IconButton(onClick = {
                            leadingIconOnClick(value)
                        }) {
                            Icon(
                                painter = leadingiconResourceId,
                                contentDescription = "Leading icon",
                                tint = leadingiconColor
                            )
                        }
                    }
                } else null,
                trailingIcon = if (trailingIconStatus) {
                    {
                        if (isPasswordField) {
                            IconButton(onClick = {
                                onPasswordVisibilityChanged(!isPasswordVisible)
                            }) {
                                Icon(
                                    painter = painterResource(
                                        if (isPasswordVisible) R.drawable.baseline_visibility_24
                                        else R.drawable.baseline_visibility_24
                                    ),
                                    contentDescription = if (isPasswordVisible) "Ocultar contraseña" else "Mostrar contraseña",
                                    tint = trailingiconColor
                                )
                            }
                        } else {
                            IconButton(onClick = {
                                trailingIconOnClick(value)
                            }) {
                                Icon(
                                    painter = trailingiconResourceId,
                                    contentDescription = "Trailing icon",
                                    tint = trailingiconColor
                                )
                            }
                        }
                    }
                } else null,
                keyboardOptions = KeyboardOptions(
                    keyboardType = keyboardType,
                    imeAction = ImeAction.Go
                ),
                keyboardActions = KeyboardActions(
                    onGo = {
                        keyboardController?.hide()
                    }
                ),
                modifier = modifier
                    .fillMaxWidth()
                    .testTag(testTag),
                visualTransformation = if (isPasswordField && !isPasswordVisible) {
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
                    cursorColor = MaterialTheme.colorScheme.primary
                ),
                textStyle = MaterialTheme.typography.bodyMedium
            )

            // Textos de soporte
            if (statusTextDownEditext || statusMaxCharacter) {
                Row {
                    if (statusTextDownEditext && textDownEditext.isNotEmpty()) {
                        Row(horizontalArrangement = Arrangement.Start) {
                            Text(
                                text = textDownEditext,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(10.dp, 0.dp),
                                textAlign = TextAlign.Start
                            )
                        }
                    }
                    if (statusMaxCharacter) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Text(
                                text = "${value.length}/$countMaxCharacter",
                                fontSize = 12.sp,
                                color = if (value.length > countMaxCharacter) {
                                    MaterialTheme.colorScheme.error
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                },
                                modifier = Modifier.padding(10.dp, 0.dp),
                                textAlign = TextAlign.End
                            )
                        }
                    }
                }
            }
        }
    }
}


/*
@Composable
fun TextWithDivider(text: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Divider(
            modifier = Modifier
                .weight(1f)
                .height(1.dp),
            color = Color.Gray
        )
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 8.dp), color = Color.Gray
        )
        Divider(
            modifier = Modifier
                .weight(1f)
                .height(1.dp),
            color = Color.Gray
        )
    }
}*/
