# WhatsApp Message Summarizer - Android App

An Android app that listens to WhatsApp notifications and uses Google Gemini AI to summarize unread messages.

## Features

- **Notification Listener**: Captures WhatsApp unread message notifications
- **Message Queue**: Stores captured messages for batch summarization
- **Gemini Integration**: Uses Google's Generative AI to create summaries
- **Simple UI**: Easy-to-use interface with API key management

## Prerequisites

- Android Studio 2023.1 or later
- Android SDK 24+ (Android 7.0 Nougat minimum)
- Google Gemini API key (get one at [ai.google.dev](https://ai.google.dev))
- Physical Android device or emulator for testing

## Setup & Build

### 1. Clone and Navigate
```bash
cd android-app
```

### 2. Build the Project
```bash
./gradlew build
```

### 3. Install on Device
```bash
./gradlew installDebug
```

Or use Android Studio:
- Open the project in Android Studio
- Connect your Android device
- Click "Run" or press Shift+F10

## Configuration

### 1. Enable Notification Listener

After installing:
1. Open the app
2. Click "Enable Listener" button
3. Go to Settings > Notifications > Notification Access
4. Find "WhatsApp Message Summarizer" and enable it
5. Return to the app

### 2. Add Gemini API Key

1. Get your API key from [ai.google.dev](https://ai.google.dev)
2. In the app, paste your API key in the input field
3. Click "Save API Key"

## How It Works

1. **WhatsApp sends a notification** when you receive a message
2. **The app's notification listener** captures the notification text
3. **Messages are stored** in the app's queue
4. **Click "Summarize Unread Messages"** to generate a summary
5. **Gemini AI** creates a concise summary of all unread messages

## Architecture

```
app/src/main/java/com/whatsapp/summarizer/
├── MainActivity.kt                 # Main UI activity
├── WhatsAppNotificationListener.kt # Notification listener service
├── MessageQueue.kt                 # Message storage & persistence
├── GeminiService.kt                # Gemini API integration
└── Message.kt                      # Data model
```

## Limitations

- **Visible notifications only**: The app only captures notifications that generate notifications
- **Text preview**: Limited to notification preview text length
- **API costs**: Using Gemini API will incur costs based on usage

## Security Notes

- Your Gemini API key is stored locally in encrypted SharedPreferences
- Messages are stored only on your device
- The notification listener service requires explicit permission

## Troubleshooting

### Messages not being captured?
1. Check that notification listener is enabled in Settings
2. Verify the app has notification access permission
3. Make sure WhatsApp notifications are enabled

### Summarization not working?
1. Verify your Gemini API key is valid
2. Check internet connection
3. Ensure you have API quota remaining

### App crashes?
- Check Android Studio Logcat for error messages
- Clear app data: Settings > Apps > WhatsApp Summarizer > Clear Data
- Reinstall the app

## Building for Release

```bash
./gradlew bundleRelease
```

This creates an App Bundle that can be uploaded to Google Play Store.

## Contributing

Feel free to submit issues and enhancement requests!

## License

MIT License - see LICENSE file for details
