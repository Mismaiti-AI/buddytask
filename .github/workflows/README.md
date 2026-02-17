# CI/CD Workflows

This directory contains GitHub Actions workflows for automated building, testing, and deployment of Kotlin Multiplatform projects.

## Workflows

### 🔄 CI/CD Pipeline (`ci-cd.yml`)
Main orchestrator workflow that:
- Auto-detects project type (Android, iOS, or Multiplatform)
- Reads configuration from `workflow-config.json`
- Triggers platform-specific builds
- Provides build summary

**Triggers:**
- Push to `main` or `release-*` branches
- Pull requests to `main`
- Manual workflow dispatch

### 🤖 Android Build (`build-android.yml`)
Builds Android application with:
- JDK 17 setup
- Gradle caching
- Unit tests (optional)
- Debug & Release APK generation
- Artifact upload
- Firebase App Distribution (optional)

**Outputs:**
- `app-debug.apk`
- `app-release.apk`
- Test results (if enabled)

### 🍎 iOS Build (`build-ios.yml`)
Builds iOS application with:
- Xcode 15.2
- CocoaPods dependency management
- Unit tests (optional)
- IPA generation
- Artifact upload
- Firebase App Distribution (optional)

**Outputs:**
- `app-ios-debug.ipa`
- Test results (if enabled)

**Note:** Requires macOS runner (higher cost)

### 📱 Multiplatform Build (`build-multiplatform.yml`)
Builds both Android and iOS in parallel using matrix strategy.

**Triggers:**
- Manual workflow dispatch only

## Configuration

### `workflow-config.json`
Configuration file that controls workflow behavior. This file is automatically updated by the Mismaiti GitHub App based on project requirements.

```json
{
  "project_type": "multiplatform",
  "platforms": {
    "android": { "enabled": true },
    "ios": { "enabled": true }
  },
  "testing": {
    "enable_unit_tests": true,
    "enable_e2e_tests": false
  },
  "firebase": {
    "enable_firebase_distribution": false,
    "firebase_app_id_android": "",
    "firebase_app_id_ios": ""
  }
}
```

## Required Secrets

For Firebase App Distribution (optional):

- `FIREBASE_TOKEN` - Firebase CLI token (generate with `firebase login:ci`)
- `FIREBASE_APP_ID` - Android Firebase App ID (from Firebase Console)
- `FIREBASE_APP_ID_IOS` - iOS Firebase App ID (from Firebase Console)

### How to Generate Firebase Token

```bash
# Install Firebase CLI
npm install -g firebase-tools

# Login and generate token
firebase login:ci
```

Copy the generated token and add it to repository secrets.

## Auto-Configuration

When using Mismaiti AI to generate code:

1. Project context is analyzed
2. `workflow-config.json` is automatically updated with:
   - Firebase settings (if enabled in deployment config)
   - Platform detection (Android/iOS/Both)
   - Testing preferences
3. Workflows automatically adapt to configuration
4. First build is triggered automatically

## Manual Configuration

To manually update workflow behavior:

1. Edit `.github/workflow-config.json`
2. Set `enable_firebase_distribution: true` to enable Firebase
3. Add Firebase secrets to repository settings
4. Push changes to trigger workflow

## Workflow Status

View workflow status:
- In GitHub Actions tab
- In Mismaiti AI chat interface (real-time updates)
- Via commit status checks

## Artifacts

Build artifacts are retained for 30 days and can be downloaded from:
- GitHub Actions run page
- Mismaiti AI interface (direct download links)

## Retry Logic

Workflows automatically retry on failure:
1. First failure: Immediate retry
2. Second failure: Retry after 2 minutes
3. Third failure: Retry after 5 minutes
4. After 3 attempts: Manual intervention required

## Troubleshooting

### Android Build Fails
- Check Gradle version compatibility
- Verify JDK 17 is used
- Check for missing dependencies

### iOS Build Fails
- Verify Xcode version (15.2 required)
- Check CocoaPods installation
- Verify code signing configuration

### Firebase Distribution Fails
- Verify `FIREBASE_TOKEN` secret is set
- Check `FIREBASE_APP_ID` is correct
- Ensure Firebase project has App Distribution enabled

## Support

For issues or questions:
- Check workflow run logs in GitHub Actions
- Contact Mismaiti AI support
- Review [GitHub Actions documentation](https://docs.github.com/en/actions)
