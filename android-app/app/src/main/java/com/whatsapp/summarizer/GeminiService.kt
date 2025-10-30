package com.whatsapp.summarizer

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "api")

class GeminiService(private val context: Context) {
    private val apiKeyKey = stringPreferencesKey("gemini_api_key")

    fun getApiKeyFlow(): Flow<String?> {
        return context.dataStore.data.map { preferences ->
            preferences[apiKeyKey]
        }
    }

    suspend fun saveApiKey(apiKey: String) {
        context.dataStore.edit { preferences ->
            preferences[apiKeyKey] = apiKey
        }
    }

    suspend fun summarizeMessages(messages: List<Message>, apiKey: String): String {
        if (apiKey.isBlank()) {
            throw IllegalArgumentException("API key is required")
        }

        if (messages.isEmpty()) {
            return "No messages to summarize"
        }

        return try {
            val model = GenerativeModel(
                modelName = "gemini-1.5-flash",
                apiKey = apiKey
            )

            val messageText = messages.joinToString("\n") { message ->
                "${message.sender}: ${message.text}"
            }

            val prompt = """
                Please provide a concise summary of these WhatsApp chat messages. 
                Focus on the key points and main topics discussed.
                
                Messages:
                $messageText
                
                Summary:
            """.trimIndent()

            val response = model.generateContent(prompt)
            response.text ?: "Unable to generate summary"
        } catch (e: Exception) {
            "Error: ${e.message}"
        }
    }
}
