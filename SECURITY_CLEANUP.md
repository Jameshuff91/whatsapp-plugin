# Security Cleanup Instructions - URGENT

## Exposed API Key Issue
Your Gemini API key was exposed in git history (commit 0e1092557341cc97e53884eb8b0ae7ece2181ebf).

### Immediate Actions Completed:
✅ API key has been revoked (confirmed by user)
✅ Build directories cleaned
✅ .gitignore updated to prevent future exposures

### Required Actions:

## Option 1: Using BFG Repo-Cleaner (Recommended)

1. **Install BFG**:
   ```bash
   brew install bfg
   ```

2. **Clone a fresh copy of your repo**:
   ```bash
   cd ~/temp
   git clone --mirror https://github.com/YOUR_USERNAME/whatsapp-plugin.git
   ```

3. **Remove the exposed API key from history**:
   ```bash
   bfg --replace-text <(echo "AIzaSyA6weyZ6M5eCp88rb0UNSDqu4KlxbYaHOY==>REMOVED") whatsapp-plugin.git
   ```

4. **Clean up the repository**:
   ```bash
   cd whatsapp-plugin.git
   git reflog expire --expire=now --all && git gc --prune=now --aggressive
   ```

5. **Force push to remote**:
   ```bash
   git push --force
   ```

## Option 2: Create a New Repository (Nuclear Option)

If the above doesn't work or you prefer a clean slate:

1. **Create a new repository on GitHub**
2. **Copy your current code (without build files)**:
   ```bash
   rsync -av --exclude='android-app/app/build' --exclude='android-app/.gradle' . ../whatsapp-plugin-clean/
   ```
3. **Push to new repository**
4. **Archive or delete the old repository**

## Future Prevention Measures

### 1. Use Environment Variables for API Keys
Never hardcode API keys. Always use environment variables:

```bash
# When building Android app:
export GEMINI_API_KEY="your-new-key"
./gradlew assembleDebug
```

### 2. Use GitHub Secrets for CI/CD
If using GitHub Actions, store API keys in repository secrets.

### 3. Use `.env` Files Locally
Create a `.env.local` file (already in .gitignore) for local development:
```
GEMINI_API_KEY=your-new-key
```

### 4. Pre-commit Hooks
Consider using tools like:
- **git-secrets**: Prevents committing secrets
- **pre-commit**: Automated hooks for security checks

Install git-secrets:
```bash
brew install git-secrets
git secrets --install
git secrets --register-aws  # or custom patterns
```

### 5. Regular Security Audits
Use tools to scan for secrets:
- **TruffleHog**: `trufflehog git https://github.com/YOUR_REPO`
- **GitLeaks**: `gitleaks detect --source .`

## Important Notes

1. **The exposed key is already public** - Even after cleaning history, assume it's compromised
2. **Generate a new API key** and use proper secret management
3. **Monitor your Google Cloud console** for any unauthorized usage
4. **Consider enabling API key restrictions** in Google Cloud Console:
   - Restrict to specific IPs
   - Restrict to specific APIs
   - Set usage quotas

## Verification After Cleanup

After cleaning the repository, verify no secrets remain:

```bash
# Search for the old key
git log --all --full-history -p | grep "AIzaSyA6weyZ6M5eCp88rb0UNSDqu4KlxbYaHOY"

# Should return nothing
```

## Contact

If you need help with any of these steps, consider:
- GitHub Support for repository issues
- Google Cloud Support for API key concerns