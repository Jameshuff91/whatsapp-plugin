package com.whatsapp.summarizer

import java.time.LocalDateTime

data class Message(
    val sender: String,
    val text: String,
    val timestamp: LocalDateTime = LocalDateTime.now(),
    val chatName: String = "Unknown"
) {
    override fun toString(): String = "$sender: $text"
}
