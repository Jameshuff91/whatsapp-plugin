package com.whatsapp.summarizer

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WhatsAppNotificationListener : NotificationListenerService() {
    private lateinit var messageQueue: MessageQueue
    private val scope = CoroutineScope(Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        messageQueue = MessageQueue(this)
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)
        
        if (sbn == null) return
        
        // Only listen to WhatsApp notifications
        if (sbn.packageName != "com.whatsapp" && sbn.packageName != "com.whatsapp.w4b") {
            return
        }

        val notification = sbn.notification
        val extras = notification.extras

        // Extract message information
        val title = extras.getString("android.title") ?: return
        val text = extras.getString("android.text") ?: return
        val subText = extras.getString("android.subText") ?: ""

        // Parse the message
        // Format is typically: "Chat Name: Message" or just "Message"
        val (sender, message) = parseNotification(title, text, subText)

        if (message.isNotEmpty()) {
            scope.launch {
                messageQueue.addMessage(
                    Message(
                        sender = sender,
                        text = message,
                        chatName = title
                    )
                )
            }
        }
    }

    private fun parseNotification(title: String, text: String, subText: String): Pair<String, String> {
        // WhatsApp notifications include sender name in title or subtext
        // and the message content in text
        return Pair(title, text)
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        super.onNotificationRemoved(sbn)
    }
}
