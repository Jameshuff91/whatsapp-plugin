package com.whatsapp.summarizer

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WhatsAppNotificationListener : NotificationListenerService() {
    private lateinit var messageQueue: MessageQueue
    private val scope = CoroutineScope(Dispatchers.IO)

    companion object {
        private const val TAG = "WhatsAppListener"
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "WhatsAppNotificationListener created")
        messageQueue = MessageQueue(this)
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)

        Log.d(TAG, "onNotificationPosted called: sbn=$sbn")

        if (sbn == null) {
            Log.w(TAG, "Received null StatusBarNotification")
            return
        }

        Log.d(TAG, "Notification from package: ${sbn.packageName}")

        // Only listen to WhatsApp notifications
        if (sbn.packageName != "com.whatsapp" && sbn.packageName != "com.whatsapp.w4b") {
            Log.d(TAG, "Ignoring notification from non-WhatsApp app: ${sbn.packageName}")
            return
        }

        Log.d(TAG, "Processing WhatsApp notification from ${sbn.packageName}")

        val notification = sbn.notification
        val extras = notification.extras

        // Extract message information
        val title = extras.getString("android.title")
        val text = extras.getString("android.text")
        val subText = extras.getString("android.subText") ?: ""

        Log.d(TAG, "Notification extras - Title: $title, Text: $text, SubText: $subText")

        if (title == null || text == null) {
            Log.w(TAG, "Missing title or text in notification")
            return
        }

        // Parse the message
        val (sender, message) = parseNotification(title, text, subText)

        if (message.isNotEmpty()) {
            Log.d(TAG, "Adding message to queue - Sender: $sender, Message: $message")
            scope.launch {
                try {
                    messageQueue.addMessage(
                        Message(
                            sender = sender,
                            text = message,
                            chatName = title
                        )
                    )
                    Log.d(TAG, "Message successfully added to queue")
                } catch (e: Exception) {
                    Log.e(TAG, "Error adding message to queue", e)
                }
            }
        } else {
            Log.w(TAG, "Empty message after parsing")
        }
    }

    private fun parseNotification(title: String, text: String, subText: String): Pair<String, String> {
        // WhatsApp notifications include sender name in title or subtext
        // and the message content in text
        Log.d(TAG, "Parsing notification - Title: $title, Text: $text")
        return Pair(title, text)
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        super.onNotificationRemoved(sbn)
        Log.d(TAG, "onNotificationRemoved called for ${sbn?.packageName}")
    }
}
