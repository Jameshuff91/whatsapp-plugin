package com.whatsapp.summarizer

import android.accessibilityservice.AccessibilityService
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WhatsAppAccessibilityService : AccessibilityService() {
    private lateinit var messageQueue: MessageQueue
    private val scope = CoroutineScope(Dispatchers.IO)
    private val capturedMessages = mutableSetOf<String>()

    companion object {
        private const val TAG = "WhatsAppAccessibility"
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "WhatsAppAccessibilityService created")
        messageQueue = MessageQueue(this)
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) {
            Log.w(TAG, "Received null AccessibilityEvent")
            return
        }

        // Only process events from WhatsApp
        if (event.packageName != "com.whatsapp" && event.packageName != "com.whatsapp.w4b") {
            return
        }

        when (event.eventType) {
            AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED -> {
                Log.d(TAG, "Window content changed in WhatsApp")
                processWhatsAppMessages(event)
            }
            AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED -> {
                Log.d(TAG, "Text changed in WhatsApp")
                processWhatsAppMessages(event)
            }
        }
    }

    private fun processWhatsAppMessages(event: AccessibilityEvent) {
        val rootNode = rootInActiveWindow ?: return

        Log.d(TAG, "Processing accessibility tree for messages")
        extractMessages(rootNode)
    }

    private fun extractMessages(node: android.view.accessibility.AccessibilityNodeInfo?) {
        if (node == null) return

        // Look for message containers
        val className = node.className?.toString() ?: ""
        val text = node.text?.toString() ?: ""

        // WhatsApp message bubbles typically contain text
        if (text.isNotEmpty() && text.length < 500) { // Reasonable message length
            val uniqueKey = "${node.contentDescription ?: ""}_$text"

            if (!capturedMessages.contains(uniqueKey)) {
                capturedMessages.add(uniqueKey)

                Log.d(TAG, "Found potential message: $text")

                // Determine sender based on content description or node properties
                val sender = determineSender(node)

                scope.launch {
                    try {
                        messageQueue.addMessage(
                            Message(
                                sender = sender,
                                text = text,
                                chatName = "WhatsApp Chat"
                            )
                        )
                        Log.d(TAG, "Message added from accessibility service - Sender: $sender, Text: $text")
                    } catch (e: Exception) {
                        Log.e(TAG, "Error adding message to queue", e)
                    }
                }
            }
        }

        // Recursively process child nodes
        for (i in 0 until (node.childCount ?: 0)) {
            val child = node.getChild(i)
            extractMessages(child)
        }
    }

    private fun determineSender(node: android.view.accessibility.AccessibilityNodeInfo?): String {
        if (node == null) return "Unknown"

        // Try to find the sender information from the node hierarchy
        var parent = node.parent
        var depth = 0
        while (parent != null && depth < 5) {
            val contentDesc = parent.contentDescription?.toString() ?: ""
            if (contentDesc.contains("message", ignoreCase = true)) {
                // Parse sender from content description if available
                val lines = contentDesc.split(",")
                if (lines.isNotEmpty()) {
                    return lines[0].trim()
                }
            }
            parent = parent.parent
            depth++
        }

        return "You"
    }

    override fun onInterrupt() {
        Log.d(TAG, "Accessibility service interrupted")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "WhatsAppAccessibilityService destroyed")
    }
}
