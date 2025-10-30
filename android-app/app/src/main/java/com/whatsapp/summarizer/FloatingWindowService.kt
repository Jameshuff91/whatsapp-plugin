package com.whatsapp.summarizer

import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FloatingWindowService : Service() {
    private var floatingView: View? = null
    private var windowManager: WindowManager? = null
    private lateinit var messageQueue: MessageQueue
    private lateinit var geminiService: GeminiService
    private val scope = CoroutineScope(Dispatchers.Main)

    override fun onCreate() {
        super.onCreate()
        messageQueue = MessageQueue(this)
        geminiService = GeminiService(this)
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (floatingView == null) {
            createFloatingWindow()
        }
        return START_STICKY
    }

    private fun createFloatingWindow() {
        val inflater = LayoutInflater.from(this)
        floatingView = inflater.inflate(R.layout.floating_window, null)

        val params = WindowManager.LayoutParams().apply {
            type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                @Suppress("DEPRECATION")
                WindowManager.LayoutParams.TYPE_PHONE
            }
            format = PixelFormat.TRANSLUCENT
            flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
            width = 100
            height = 100
            gravity = Gravity.TOP or Gravity.END
            x = 0
            y = 100
        }

        windowManager?.addView(floatingView, params)
        setupFloatingViewListeners()
    }

    private fun setupFloatingViewListeners() {
        val floatingButton = floatingView?.findViewById<Button>(R.id.floatingButton) ?: return
        val expandedView = floatingView?.findViewById<View>(R.id.expandedView) ?: return
        val summaryText = floatingView?.findViewById<TextView>(R.id.summaryText) ?: return
        val messagesText = floatingView?.findViewById<TextView>(R.id.messagesText) ?: return
        val summarizeBtn = floatingView?.findViewById<Button>(R.id.summarizeBtn) ?: return
        val clearBtn = floatingView?.findViewById<Button>(R.id.clearBtn) ?: return
        val closeBtn = floatingView?.findViewById<Button>(R.id.closeBtn) ?: return
        val loadingProgress = floatingView?.findViewById<ProgressBar>(R.id.loadingProgress) ?: return

        var isExpanded = false

        // Toggle expand/collapse
        floatingButton.setOnClickListener {
            isExpanded = !isExpanded
            expandedView.visibility = if (isExpanded) View.VISIBLE else View.GONE
            floatingButton.text = if (isExpanded) "−" else "+"

            if (isExpanded) {
                updateMessages(messagesText)
            }
        }

        // Summarize button
        summarizeBtn.setOnClickListener {
            scope.launch {
                val messages = messageQueue.getMessages()
                if (messages.isEmpty()) {
                    summaryText.text = "No messages to summarize"
                    return@launch
                }

                val apiKey = getApiKey()
                if (apiKey.isEmpty()) {
                    summaryText.text = "Please set API key in main app"
                    return@launch
                }

                loadingProgress.visibility = View.VISIBLE
                summaryText.text = "Generating summary..."

                try {
                    val summary = geminiService.summarizeMessages(messages, apiKey)
                    summaryText.text = summary
                } catch (e: Exception) {
                    summaryText.text = "Error: ${e.message}"
                } finally {
                    loadingProgress.visibility = View.GONE
                }
            }
        }

        // Clear button
        clearBtn.setOnClickListener {
            scope.launch {
                messageQueue.clearMessages()
                summaryText.text = ""
                messagesText.text = "No messages"
            }
        }

        // Close button
        closeBtn.setOnClickListener {
            isExpanded = false
            expandedView.visibility = View.GONE
            floatingButton.text = "+"
        }

        // Make floating button draggable
        var lastAction = 0
        var initialX = 0
        var initialY = 0
        var initialTouchX = 0f
        var initialTouchY = 0f

        floatingButton.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    lastAction = 0
                    initialX = params.x
                    initialY = params.y
                    initialTouchX = event.rawX
                    initialTouchY = event.rawY
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    val deltaX = (event.rawX - initialTouchX).toInt()
                    val deltaY = (event.rawY - initialTouchY).toInt()

                    params.x = initialX + deltaX
                    params.y = initialY + deltaY

                    windowManager?.updateViewLayout(floatingView, params)
                    lastAction = 1
                    true
                }
                MotionEvent.ACTION_UP -> {
                    if (lastAction == 0) {
                        // It was a click
                        floatingButton.performClick()
                    }
                    true
                }
                else -> false
            }
        }
    }

    private fun updateMessages(messagesText: TextView) {
        scope.launch {
            val messages = messageQueue.getMessages()
            val text = if (messages.isEmpty()) {
                "No unread messages"
            } else {
                messages.take(5).joinToString("\n\n") {
                    "${it.sender}:\n${it.text.take(50)}..."
                }
            }
            messagesText.text = text
        }
    }

    private fun getApiKey(): String {
        val prefs = getSharedPreferences("api", Context.MODE_PRIVATE)
        return prefs.getString("gemini_api_key", "") ?: ""
    }

    override fun onDestroy() {
        super.onDestroy()
        if (floatingView != null) {
            windowManager?.removeView(floatingView)
            floatingView = null
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
