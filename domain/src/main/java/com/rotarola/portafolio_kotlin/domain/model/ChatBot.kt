package com.rotarola.portafolio_kotlin.domain.model

// Primero creamos un modelo para los mensajes
data class ChatBotMessage(
    val text: String,
    val isFromUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)