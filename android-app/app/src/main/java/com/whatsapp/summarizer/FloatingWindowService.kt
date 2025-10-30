package com.whatsapp.summarizer

import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class FloatingWindowService : Service() {
    private var floatingView: View? = null
    private var windowManager: WindowManager? = null
    private lateinit var messageQueue: MessageQueue
    private lateinit var geminiService: GeminiService
    private val scope = CoroutineScope(Dispatchers.Main)
    private lateinit var params: WindowManager.LayoutParams
    private var summarizeJob: kotlinx.coroutines.Job? = null

    override fun onCreate() {
        super.onCreate()
        messageQueue = MessageQueue(this)
        geminiService = GeminiService(this)
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (floatingView == null) {
            // Create the floating window - let the system handle permission errors
            createFloatingWindow()
        }
        return START_STICKY
    }

    private fun createFloatingWindow() {
        try {
            val inflater = LayoutInflater.from(this)
            floatingView = inflater.inflate(R.layout.floating_window, null)

            params = WindowManager.LayoutParams().apply {
                type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                } else {
                    @Suppress("DEPRECATION")
                    WindowManager.LayoutParams.TYPE_PHONE
                }
                format = PixelFormat.TRANSLUCENT
                flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS

                // Get display metrics for proper sizing
                val displayMetrics = resources.displayMetrics
                width = displayMetrics.widthPixels  // Full screen width
                height = 150  // Approximate height for header (will wrap to content)

                gravity = Gravity.TOP
                x = 0
                y = 0
            }

            windowManager?.addView(floatingView, params)
            setupFloatingViewListeners()
            Log.i("FloatingWindow", "Floating window created successfully at position (0,0) with size ${params.width}x${params.height}")
        } catch (e: Exception) {
            Log.e("FloatingWindow", "Failed to create floating window: ${e.message}", e)
            // Window couldn't be added - likely permission not granted
        }
    }

    private fun setupFloatingViewListeners() {
        val floatingButton = floatingView?.findViewById<Button>(R.id.floatingButton) ?: return
        val expandedView = floatingView?.findViewById<View>(R.id.expandedView) ?: return
        val summaryText = floatingView?.findViewById<TextView>(R.id.summaryText) ?: return
        val summaryDetailText = floatingView?.findViewById<TextView>(R.id.summaryDetailText)
        val messagesText = floatingView?.findViewById<TextView>(R.id.messagesText) ?: return
        val summarizeBtn = floatingView?.findViewById<Button>(R.id.summarizeBtn) ?: return
        val clearBtn = floatingView?.findViewById<Button>(R.id.clearBtn) ?: return
        val closeBtn = floatingView?.findViewById<Button>(R.id.closeBtn) ?: return
        val loadingProgress = floatingView?.findViewById<ProgressBar>(R.id.loadingProgress) ?: return

        var isExpanded = false

        // Auto-summarize messages with debouncing
        scope.launch {
            messageQueue.getMessagesFlow().collect { messages ->
                if (messages.isEmpty()) {
                    summaryText.text = "Waiting for messages..."
                    return@collect
                }

                Log.d("FloatingWindow", "Messages arrived, triggering debounced summarization. Count: ${messages.size}")

                // Cancel previous summarization job
                summarizeJob?.cancel()

                // Launch new debounced summarization job
                summarizeJob = scope.launch {
                    try {
                        // Wait 2 seconds to allow more messages to arrive (debounce)
                        delay(2000)

                        Log.d("FloatingWindow", "Starting auto-summarization of ${messages.size} messages")
                        summaryText.text = "Generating summary..."
                        loadingProgress.visibility = View.VISIBLE

                        val apiKey = geminiService.getApiKey()
                        if (apiKey.isEmpty()) {
                            Log.w("FloatingWindow", "API key not available for auto-summarization")
                            summaryText.text = "API key not configured"
                            loadingProgress.visibility = View.GONE
                            return@launch
                        }

                        // Call Gemini API to summarize
                        val summary = geminiService.summarizeMessages(messages, apiKey)
                        Log.d("FloatingWindow", "Auto-summarization complete: $summary")

                        summaryText.text = summary
                        summaryDetailText?.text = summary
                    } catch (e: Exception) {
                        Log.e("FloatingWindow", "Error in auto-summarization", e)
                        summaryText.text = "Error: ${e.message}"
                        summaryDetailText?.text = "Error: ${e.message}"
                    } finally {
                        loadingProgress.visibility = View.GONE
                    }
                }
            }
        }

        // Toggle expand/collapse for detailed view
        floatingButton.setOnClickListener {
            isExpanded = !isExpanded
            expandedView.visibility = if (isExpanded) View.VISIBLE else View.GONE
            floatingButton.text = if (isExpanded) "×" else "−"

            if (isExpanded) {
                updateMessages(messagesText)
            }
        }

        // Summarize button
        summarizeBtn.setOnClickListener {
            scope.launch {
                val messages = messageQueue.getMessages()
                if (messages.isEmpty()) {
                    summaryDetailText?.text = "No messages to summarize"
                    return@launch
                }

                val apiKey = geminiService.getApiKey()
                if (apiKey.isEmpty()) {
                    summaryDetailText?.text = "API key not configured. Set GEMINI_API_KEY at build time."
                    return@launch
                }

                loadingProgress.visibility = View.VISIBLE
                summaryDetailText?.text = "Generating summary..."

                try {
                    val summary = geminiService.summarizeMessages(messages, apiKey)
                    summaryDetailText?.text = summary
                    // Also update header with summary
                    summaryText.text = summary.take(100) + if (summary.length > 100) "..." else ""
                } catch (e: Exception) {
                    summaryDetailText?.text = "Error: ${e.message}"
                } finally {
                    loadingProgress.visibility = View.GONE
                }
            }
        }

        // Clear button
        clearBtn.setOnClickListener {
            scope.launch {
                messageQueue.clearMessages()
                summaryDetailText?.text = ""
                summaryText.text = "Waiting for messages..."
                messagesText.text = "No messages"
            }
        }

        // Close button
        closeBtn.setOnClickListener {
            isExpanded = false
            expandedView.visibility = View.GONE
            floatingButton.text = "−"
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

    private fun hasWindowPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.SYSTEM_ALERT_WINDOW
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED
        } else {
            true // Permission not needed on older Android versions
        }
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
