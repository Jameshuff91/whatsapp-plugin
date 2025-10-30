package com.whatsapp.summarizer

import android.app.NotificationManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var messageQueue: MessageQueue
    private lateinit var geminiService: GeminiService
    private lateinit var apiKeyInput: EditText
    private lateinit var messagesView: TextView
    private lateinit var summaryView: TextView
    private lateinit var loadingProgress: ProgressBar
    private lateinit var listenerStatus: TextView
    private lateinit var overlayToggle: Switch

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        messageQueue = MessageQueue(this)
        geminiService = GeminiService(this)

        initializeViews()
        setupListeners()
        checkNotificationListenerPermission()
    }

    private fun initializeViews() {
        apiKeyInput = findViewById(R.id.apiKeyInput)
        messagesView = findViewById(R.id.messagesView)
        summaryView = findViewById(R.id.summaryView)
        loadingProgress = findViewById(R.id.loadingProgress)
        listenerStatus = findViewById(R.id.listenerStatus)
        overlayToggle = findViewById(R.id.overlayToggle)

        // Load saved API key
        lifecycleScope.launch {
            geminiService.getApiKeyFlow().collect { apiKey ->
                apiKeyInput.setText(apiKey ?: "")
            }
        }

        // Load and display messages
        lifecycleScope.launch {
            messageQueue.getMessagesFlow().collect { messages ->
                updateMessagesView(messages)
            }
        }

        // Check if overlay service is running
        updateOverlayToggle()
    }

    private fun setupListeners() {
        findViewById<Button>(R.id.saveApiKeyButton).setOnClickListener {
            val apiKey = apiKeyInput.text.toString()
            if (apiKey.isNotEmpty()) {
                lifecycleScope.launch {
                    geminiService.saveApiKey(apiKey)
                    showMessage("API key saved")
                }
            }
        }

        findViewById<Button>(R.id.summarizeButton).setOnClickListener {
            summarizeMessages()
        }

        findViewById<Button>(R.id.clearButton).setOnClickListener {
            clearMessages()
        }

        findViewById<Button>(R.id.enableListenerButton).setOnClickListener {
            openNotificationListenerSettings()
        }

        overlayToggle.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                startFloatingWindow()
            } else {
                stopFloatingWindow()
            }
        }
    }

    private fun startFloatingWindow() {
        val intent = Intent(this, FloatingWindowService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }
    }

    private fun stopFloatingWindow() {
        val intent = Intent(this, FloatingWindowService::class.java)
        stopService(intent)
    }

    private fun updateOverlayToggle() {
        val isRunning = isServiceRunning(FloatingWindowService::class.java)
        overlayToggle.isChecked = isRunning
    }

    private fun isServiceRunning(serviceClass: Class<*>): Boolean {
        val manager = getSystemService(Context.ACTIVITY_SERVICE) as android.app.ActivityManager
        for (service in manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }

    private fun updateMessagesView(messages: List<Message>) {
        val text = if (messages.isEmpty()) {
            "No unread messages captured yet."
        } else {
            messages.joinToString("\n") { "${it.sender}: ${it.text}" }
        }
        messagesView.text = text
    }

    private fun summarizeMessages() {
        lifecycleScope.launch {
            val messages = messageQueue.getMessages()
            if (messages.isEmpty()) {
                summaryView.text = "No messages to summarize"
                return@launch
            }

            val apiKey = apiKeyInput.text.toString()
            if (apiKey.isEmpty()) {
                summaryView.text = getString(R.string.error_no_key)
                return@launch
            }

            loadingProgress.visibility = View.VISIBLE
            summaryView.text = ""

            try {
                val summary = geminiService.summarizeMessages(messages, apiKey)
                summaryView.text = summary
            } catch (e: Exception) {
                summaryView.text = "Error: ${e.message}"
            } finally {
                loadingProgress.visibility = View.GONE
            }
        }
    }

    private fun clearMessages() {
        lifecycleScope.launch {
            messageQueue.clearMessages()
            summaryView.text = ""
            showMessage("Messages cleared")
        }
    }

    private fun checkNotificationListenerPermission() {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val componentName = ComponentName(this, WhatsAppNotificationListener::class.java)
        val isEnabled = notificationManager.isNotificationListenerAccessGranted(componentName)
        
        updateListenerStatus(isEnabled)
    }

    private fun updateListenerStatus(enabled: Boolean) {
        listenerStatus.text = if (enabled) {
            "Listener: Enabled ✓"
        } else {
            "Listener: Not Enabled"
        }
    }

    private fun openNotificationListenerSettings() {
        val intent = Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS")
        startActivity(intent)
    }

    private fun showMessage(message: String) {
        println(message)
    }

    override fun onResume() {
        super.onResume()
        updateOverlayToggle()
    }
}
