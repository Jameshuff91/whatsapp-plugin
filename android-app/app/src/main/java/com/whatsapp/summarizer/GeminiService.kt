package com.whatsapp.summarizer

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

private val Context.dataStore by preferencesDataStore(name = "api")

class GeminiService(private val context: Context) {
    private val apiKeyKey = stringPreferencesKey("gemini_api_key")
    private val client = OkHttpClient()

    companion object {
        private const val GEMINI_API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent"
        private const val TIMEOUT_MS = 30000L
    }

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

            // Call Gemini API
            callGeminiApi(messageText, key)
        } catch (e: Exception) {
            "Error: ${e.message}"
        }
    }

    private fun callGeminiApi(text: String, apiKey: String): String {
        val prompt = """
            Please summarize the following WhatsApp messages concisely in 2-3 sentences:

            $text

            Summary:
        """.trimIndent()

        // Build request body
        val requestBody = JsonObject().apply {
            add("contents", JsonArray().apply {
                add(JsonObject().apply {
                    add("parts", JsonArray().apply {
                        add(JsonObject().apply {
                            addProperty("text", prompt)
                        })
                    })
                })
            })
        }

        val request = Request.Builder()
            .url("$GEMINI_API_URL?key=$apiKey")
            .post(requestBody.toString().toRequestBody("application/json".toMediaType()))
            .build()

        return try {
            val response = client.newCall(request).execute()

            if (!response.isSuccessful) {
                return "Error: API returned ${response.code} - ${response.body?.string()}"
            }

            val responseBody = response.body?.string() ?: return "Error: Empty response"

            // Parse JSON response
            val json = com.google.gson.JsonParser.parseString(responseBody).asJsonObject

            // Extract the summary from the response
            val candidates = json.getAsJsonArray("candidates")
            if (candidates != null && candidates.size() > 0) {
                val content = candidates[0].asJsonObject.get("content")?.asJsonObject
                val parts = content?.get("parts")?.asJsonArray
                if (parts != null && parts.size() > 0) {
                    val summary = parts[0].asJsonObject.get("text")?.asString
                    return summary ?: "No summary generated"
                }
            }

            "Error: Could not extract summary from response"
        } catch (e: Exception) {
            "Error calling Gemini API: ${e.message}"
        }
    }
}
