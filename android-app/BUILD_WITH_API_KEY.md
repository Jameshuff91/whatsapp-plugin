# Building with Injected Gemini API Key

This app now supports injecting your Gemini API key at build time, so you don't need to manually enter it in the app.

## Method 1: Build Command Line (Recommended)

### On macOS/Linux:
```bash
cd android-app
export GEMINI_API_KEY="your-api-key-here"
./gradlew installDebug
```

### On Windows (PowerShell):
```powershell
cd android-app
$env:GEMINI_API_KEY="your-api-key-here"
.\gradlew.bat installDebug
```

### On Windows (CMD):
```cmd
cd android-app
set GEMINI_API_KEY=your-api-key-here
gradlew.bat installDebug
```

## Method 2: Using Android Studio

1. Open **Preferences/Settings** → **Tools** → **Terminal**
2. Before running the app:
   ```bash
   export GEMINI_API_KEY="your-api-key-here"
   ```
3. Then click **Run**

## Method 3: In build.gradle.kts (Not Recommended for Security)

Only do this for development if you're not sharing the code:

```kotlin
defaultConfig {
    buildConfigField("String", "GEMINI_API_KEY", "\"sk-your-actual-key\"")
}
```

⚠️ **Never commit actual API keys to git!**

## Method 4: GitHub Actions (CI/CD)

If building via GitHub Actions:

```yaml
jobs:
  build:
    runs-on: ubuntu-latest
    env:
      GEMINI_API_KEY: ${{ secrets.GEMINI_API_KEY }}
    steps:
      - uses: actions/checkout@v3
      - name: Build APK
        run: |
          cd android-app
          ./gradlew assembleDebug
```

Then add your `GEMINI_API_KEY` as a GitHub secret.

## How It Works

1. **At build time**: The `build.gradle.kts` reads the `GEMINI_API_KEY` environment variable
2. **BuildConfig generation**: Gradle generates `BuildConfig.GEMINI_API_KEY` with your key
3. **Runtime**: App automatically uses `BuildConfig.GEMINI_API_KEY`
4. **No manual input needed**: The API key field in the app is hidden if a key was provided at build time

## Fallback Behavior

If you don't provide `GEMINI_API_KEY` at build time:
- The app shows a manual API key input field
- You can still paste your key in the app
- It gets saved to local encrypted storage

## Finding Your API Key

1. Go to [ai.google.dev](https://ai.google.dev)
2. Click "Get API key"
3. Create or select a project
4. Copy the API key
5. Use it in the build command

## Verify It Works

After building and installing, the app should:
1. ✓ NOT show the API key input field
2. ✓ Work immediately without manual key entry
3. ✓ Summarize messages when you tap the button

## Security Notes

### For Development:
- Using environment variables is safe
- The key is compiled into the APK (not ideal but acceptable for testing)

### For Production:
- Never hardcode API keys in APKs
- Use a backend server to handle API calls
- Implement proper authentication (OAuth, etc.)
- Rotate keys regularly
- Monitor API usage

## Example Complete Build Command

```bash
cd android-app && \
export GEMINI_API_KEY="AIzaSyDx...your-actual-key...xyz" && \
./gradlew clean installDebug
```

This:
1. Navigates to the android-app directory
2. Sets the API key environment variable
3. Cleans previous builds
4. Builds and installs on your device

