package com.whatsapp.summarizer

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "messages")

class MessageQueue(private val context: Context) {
    private val gson = Gson()
    private val messagesKey = stringPreferencesKey("unread_messages")

    // In-memory queue
    private val messages = mutableListOf<Message>()

    suspend fun addMessage(message: Message) {
        messages.add(message)
        saveMessages()
    }

    suspend fun getMessages(): List<Message> = messages.toList()

    suspend fun clearMessages() {
        messages.clear()
        context.dataStore.edit { preferences ->
            preferences.remove(messagesKey)
        }
    }

    fun getMessagesFlow(): Flow<List<Message>> {
        return context.dataStore.data.map { preferences ->
            val json = preferences[messagesKey] ?: return@map messages
            try {
                val array = gson.fromJson(json, Array<Message>::class.java)
                array.toList()
            } catch (e: Exception) {
                messages
            }
        }
    }

    private suspend fun saveMessages() {
        context.dataStore.edit { preferences ->
            preferences[messagesKey] = gson.toJson(messages)
        }
    }
}
