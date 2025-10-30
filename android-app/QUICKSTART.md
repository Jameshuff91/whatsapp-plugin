# WhatsApp Message Summarizer - Quick Start Guide

## What's Been Built

I've created a **complete Android app** that:
- Listens to WhatsApp notifications
- Captures unread messages as they arrive
- Stores them in an in-app queue
- Summarizes them using Google Gemini AI
- Displays results in a **floating overlay button** on top of any app

All 344 lines of Kotlin code across 6 core services:
1. **MainActivity.kt** - Main UI and controls
2. **FloatingWindowService.kt** - Floating overlay button
3. **WhatsAppNotificationListener.kt** - Captures notifications
4. **MessageQueue.kt** - Stores messages locally
5. **GeminiService.kt** - Handles Gemini API
6. **Message.kt** - Data model

## Project Structure
```
android-app/
├── build.gradle.kts          # Root gradle config
├── settings.gradle.kts       # Project setup
├── gradle.properties         # Gradle settings
├── BUILD_WITH_API_KEY.md     # API key injection guide
├── README.md                 # Full documentation
└── app/
    ├── build.gradle.kts      # App dependencies (Gemini, Coroutines, etc)
    └── src/main/
        ├── AndroidManifest.xml          # Permissions & components
        ├── java/com/whatsapp/summarizer/
        │   ├── MainActivity.kt
        │   ├── FloatingWindowService.kt
        │   ├── WhatsAppNotificationListener.kt
        │   ├── MessageQueue.kt
        │   ├── GeminiService.kt
        │   └── Message.kt
        └── res/
            ├── layout/
            │   ├── activity_main.xml
            │   └── floating_window.xml
            └── values/
                ├── strings.xml
                ├── colors.xml
                └── themes.xml
```

## How to Build & Run

### Prerequisites
- Android Studio (2023.1+) or just gradle
- Android SDK 24+ (Android 7.0)
- Physical Android device or emulator
- Gemini API key from ai.google.dev

### Option A: Build with API Key Injected (Recommended)

This is the easiest - your API key is automatically compiled in, no manual entry needed.

```bash
cd android-app
export GEMINI_API_KEY="your-api-key-here"
./gradlew installDebug
```

The API key input field will be hidden since it's already set.

### Option B: Build without API Key (Manual Entry)

```bash
cd android-app
./gradlew installDebug
```

Then manually paste your API key in the app after installing.

### Using Android Studio

1. Open Android Studio
2. Set environment variable in terminal:
   ```bash
   export GEMINI_API_KEY="your-api-key-here"
   ```
3. File → Open → Select `android-app` folder
4. Wait for Gradle sync
5. Connect your Android device
6. Click Run (or Shift+F10)

## Setup After Installing

### Step 1: Enable Notification Listener (Always Required)
1. Open the app
2. Click "Enable Listener" button
3. Go to: **Settings → Apps → Notifications → Notification Access**
4. Find "WhatsApp Message Summarizer" and toggle ON
5. Return to the app

### Step 2: Enable Floating Overlay
1. Toggle **"Floating Overlay: ON"** in the app
2. Android will ask for "Display over other apps" permission
3. Grant the permission
4. Look for a green **"+"** button on your screen

### Step 3: (Optional) Add API Key Manually
- Only needed if you didn't set `GEMINI_API_KEY` at build time
- Paste your API key in the input field
- Click "Save"

### Step 4: Start Capturing Messages
1. Open WhatsApp
2. Receive messages from any chat
3. The green button captures messages automatically

### Step 5: Summarize
1. Tap the green **"+"** button
2. It expands to show recent messages
3. Click **"Summarize"**
4. Wait for Gemini to generate summary

---

## Key Features

✅ **Floating Overlay Button** - Green "+" button on top of any app  
✅ **Draggable UI** - Move button anywhere on screen  
✅ **Auto-capture** - Messages recorded as notifications arrive  
✅ **AI Summaries** - Gemini generates smart summaries  
✅ **Persistent** - Messages stored locally  
✅ **Easy Toggle** - Turn overlay on/off in settings  
✅ **Works Over Any App** - Use WhatsApp, Messages, etc.  

---

## Floating Button Controls

```
Green "+" Button:
├── Tap to expand/collapse the panel
├── Drag to move around screen
└── Shows recent messages & summary

Expanded Panel:
├── Recent Messages (last 5)
├── [Summarize] - Generate AI summary
├── [Clear] - Reset message queue
├── Summary display
└── [×] - Close panel
```

---

## Limitations to Know

1. **Visible Notifications Only** - Only captures messages that generate notifications
   - Messages from muted chats won't be captured
   - Some messages might be missed if notification preview is disabled

2. **Notification Text Preview** - Limited to the text shown in notifications
   - Media files (images, videos) won't be included
   - Very long messages might be truncated

3. **API Costs** - Using Gemini API will incur charges
   - Check pricing at ai.google.dev
   - Typical cost: ~$0.075 per 1M input tokens

---

## Troubleshooting

### Green button not appearing?
- Verify "Floating Overlay: ON" is enabled in app
- Grant "Display over other apps" permission
- Check notification listener is enabled

### Messages not appearing?
- Verify notification listener is enabled in Settings
- Check WhatsApp notification settings are ON
- Try sending a test message from a chat

### Summarization fails?
- Verify API key is set (via env var or manual entry)
- Check internet connection
- Look at logcat for error messages

### App crashes?
- Clear app data: Settings → Apps → WhatsApp Summarizer → Clear Data
- Check Android Studio Logcat: **View → Tool Windows → Logcat**
- Reinstall the app

---

## Getting Your API Key

1. Visit [ai.google.dev](https://ai.google.dev)
2. Click "Get API key"
3. Create or select a project
4. Copy the API key
5. Use in build command: `export GEMINI_API_KEY="..."`

---

## File Locations Reference

| Component | File |
|-----------|------|
| Main UI | `app/src/main/java/.../MainActivity.kt` |
| Floating Button | `app/src/main/java/.../FloatingWindowService.kt` |
| Notifications | `app/src/main/java/.../WhatsAppNotificationListener.kt` |
| Message Storage | `app/src/main/java/.../MessageQueue.kt` |
| Gemini API | `app/src/main/java/.../GeminiService.kt` |
| Floating UI | `app/src/main/res/layout/floating_window.xml` |
| Main UI | `app/src/main/res/layout/activity_main.xml` |
| Manifest | `app/src/main/AndroidManifest.xml` |

---

## Next Steps

1. **Get your Gemini API key**: https://ai.google.dev
2. **Build with API key injected**:
   ```bash
   cd android-app
   export GEMINI_API_KEY="your-key"
   ./gradlew installDebug
   ```
3. **Enable permissions** in the app and Settings
4. **Use the floating button** to summarize messages

---

## Documentation

- **README.md** - Full documentation with troubleshooting
- **BUILD_WITH_API_KEY.md** - Detailed API key injection guide
- **ANDROID_APP_INFO.md** - Architecture and technical details

All inside the `android-app/` directory.

---

## Need Help?

1. Check the documentation files above
2. Review Android Studio Logcat for error messages
3. Verify all permissions are enabled
4. Try a clean build: `./gradlew clean installDebug`

Enjoy your WhatsApp summarizer! 🚀
