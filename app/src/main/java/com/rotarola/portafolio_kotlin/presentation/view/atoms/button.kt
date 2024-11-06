package com.rotarola.feature_ui.presentation.atoms

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp

@Composable
fun ElevatedButtonM3(
    title:String
    ,leadingiconResourceId:Painter= rememberVectorPainter(image = Icons.Filled.CheckCircle)
    , onClick: () -> Unit
    ,enabled: Boolean
    ,iconAcceptedStatus: MutableState<Boolean> = mutableStateOf(false)
    ,iconAcceopted: Painter = rememberVectorPainter(image = Icons.Filled.CheckCircle)
) {
    ElevatedButton(
        onClick = { onClick() },
        enabled = enabled,
        shape = CircleShape, // Forma circular
        //border = BorderStroke(1.dp, Color.White), // Borde circular de 1dp
        colors = ButtonDefaults.elevatedButtonColors(
            containerColor = MaterialTheme.colorScheme.primary, // Color del botón
            contentColor = MaterialTheme.colorScheme.onPrimary
        ),
        modifier = Modifier
            .padding(10.dp)
            .fillMaxWidth()
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = if(iconAcceptedStatus.value){iconAcceopted}else{rememberVectorPainter(image = Icons.Filled.CheckCircle)},
                contentDescription = "Agregar", tint = Color.White
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = title, color = Color.White)
        }
    }
}