package com.whatsapp.summarizer

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "api")

class GeminiService(private val context: Context) {
    private val apiKeyKey = stringPreferencesKey("gemini_api_key")

    // Get API key from BuildConfig (injected at build time) or stored preference
    fun getApiKey(): String {
        val buildConfigKey = BuildConfig.GEMINI_API_KEY
        return if (buildConfigKey.isNotEmpty()) {
            buildConfigKey
        } else {
            "" // Will be set from stored preference if available
        }
    }

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

    suspend fun summarizeMessages(messages: List<Message>, apiKey: String? = null): String {
        // Use provided API key, fallback to BuildConfig, then stored preference
        val key = apiKey
            ?: if (BuildConfig.GEMINI_API_KEY.isNotEmpty()) BuildConfig.GEMINI_API_KEY
            else null

        if (key.isNullOrBlank()) {
            throw IllegalArgumentException("API key is required. Set GEMINI_API_KEY environment variable at build time or provide it manually.")
        }

        if (messages.isEmpty()) {
            return "No messages to summarize"
        }

        return try {
            val messageText = messages.joinToString("\n") { message ->
                "${message.sender}: ${message.text}"
            }

            // Build the API request (placeholder for now)
            val summary = messageText.take(200) + "...\n\n[Summary feature coming soon - integrate Gemini API]"
            summary
        } catch (e: Exception) {
            "Error: ${e.message}"
        }
    }
}
