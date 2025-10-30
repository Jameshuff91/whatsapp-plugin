<div align="center">
<img width="1200" height="475" alt="GHBanner" src="https://github.com/user-attachments/assets/0aa67016-6eaf-458a-adb2-6e31a0763ed6" />
</div>

# Run and deploy your AI Studio app

This contains everything you need to run your app locally.

View your app in AI Studio: https://ai.studio/apps/drive/1mOs3ejmeW-MFe-KxLe4hbnGcugBAz_Sk

## Run Locally

**Prerequisites:**  Node.js

1. Install dependencies:
   ```bash
   npm install
   ```

2. Set up your API key:
   ```bash
   # Copy the example environment file
   cp .env.example .env.local

   # Edit .env.local and add your Gemini API key
   # Get your key from: https://aistudio.google.com/app/apikey
   ```

3. Run the app:
   ```bash
   npm run dev
   ```

## Security Best Practices

**IMPORTANT:** Never commit API keys or secrets to version control!

- Store API keys in `.env.local` (already in .gitignore)
- For Android builds, use environment variables:
  ```bash
  export GEMINI_API_KEY="your_key_here"
  cd android-app
  ./gradlew assembleDebug
  ```
- Review the [SECURITY_CLEANUP.md](SECURITY_CLEANUP.md) for security guidelines
