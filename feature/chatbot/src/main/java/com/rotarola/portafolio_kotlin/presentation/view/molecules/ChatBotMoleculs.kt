package com.rotarola.portafolio_kotlin.presentation.view.molecules

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.rotarola.portafolio_kotlin.domain.model.ChatMessage
import com.rotarola.portafolio_kotlin.feature.chatbot.R

// 4. Actualiza el ChatMessageBubble para mejor presentación
@Composable
fun ChatMessageBubble(message: ChatMessage) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = if (message.isFromUser) Arrangement.End else Arrangement.Start
    ) {
        if (!message.isFromUser) {
            Icon(
                //Icons.Default.Star,
                painter = painterResource(R.drawable.outline_robot_2_24),
                contentDescription = "AI",
                modifier = Modifier
                    .size(32.dp)
                    .padding(end = 8.dp)
                    .align(Alignment.Top),
                tint = MaterialTheme.colorScheme.primary
            )
        }

        Box(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .background(
                    color = if (message.isFromUser)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.secondaryContainer,
                    shape = RoundedCornerShape(
                        topStart = 12.dp,
                        topEnd = 12.dp,
                        bottomStart = if (message.isFromUser) 12.dp else 4.dp,
                        bottomEnd = if (message.isFromUser) 4.dp else 12.dp
                    )
                )
                .padding(12.dp)
        ) {
            //ChatBotTextField(message.text,message.text)
            Text(
                text = message.text,
                color = if (message.isFromUser)
                    MaterialTheme.colorScheme.onPrimary
                else
                    MaterialTheme.colorScheme.onSecondaryContainer,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        if (message.isFromUser) {
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
    }
}