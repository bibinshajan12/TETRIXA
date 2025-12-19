# Google Play Store Deployment Guide

This document outlines all the steps and requirements needed to deploy the Tetrixa Tetris game to the Google Play Store.

## Table of Contents
1. [Prerequisites](#prerequisites)
2. [App Signing Configuration](#app-signing-configuration)
3. [Build Configuration](#build-configuration)
4. [Version Management](#version-management)
5. [Play Store Listing Requirements](#play-store-listing-requirements)
6. [Graphics and Assets](#graphics-and-assets)
7. [Privacy Policy and Permissions](#privacy-policy-and-permissions)
8. [Content Rating](#content-rating)
9. [Testing Requirements](#testing-requirements)
10. [Deployment Steps](#deployment-steps)

---

## Prerequisites

- [ ] Google Play Developer Account ($25 one-time fee)
- [ ] Android Studio installed and configured
- [ ] Java JDK 8 or higher
- [ ] Android SDK with API level 24+ (Android 7.0+)
- [ ] Keystore file for app signing (see App Signing section)

---

## App Signing Configuration

### 1. Generate Release Keystore

Create a keystore file for signing your release APK/AAB:

```bash
keytool -genkey -v -keystore tetrixa-release-key.jks -keyalg RSA -keysize 2048 -validity 10000 -alias tetrixa-key
```

**Important**: 
- Store the keystore file securely (backup in multiple locations)
- Remember the keystore password and key alias password
- Never commit the keystore to version control
- Add `*.jks` to `.gitignore`

### 2. Create `keystore.properties` File

Create a file at the project root (not in version control):

```properties
storePassword=your_keystore_password
keyPassword=your_key_password
keyAlias=tetrixa-key
storeFile=path/to/tetrixa-release-key.jks
```

### 3. Update `app/build.gradle.kts`

Add signing configuration:

```kotlin
android {
    ...
    
    signingConfigs {
        create("release") {
            val keystorePropertiesFile = rootProject.file("keystore.properties")
            val keystoreProperties = java.util.Properties()
            if (keystorePropertiesFile.exists()) {
                keystoreProperties.load(java.io.FileInputStream(keystorePropertiesFile))
                storeFile = file(keystoreProperties["storeFile"] as String)
                storePassword = keystoreProperties["storePassword"] as String
                keyAlias = keystoreProperties["keyAlias"] as String
                keyPassword = keystoreProperties["keyPassword"] as String
            }
        }
    }
    
    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
    }
}
```

---

## Build Configuration

### 1. Update `app/build.gradle.kts`

**Current State**: Release build has `isMinifyEnabled = false`, no signing config

**Required Changes**:

1. Add signing configuration section (before `buildTypes`):
```kotlin
signingConfigs {
    create("release") {
        val keystorePropertiesFile = rootProject.file("keystore.properties")
        val keystoreProperties = java.util.Properties()
        if (keystorePropertiesFile.exists()) {
            keystoreProperties.load(java.io.FileInputStream(keystorePropertiesFile))
            storeFile = file(keystoreProperties["storeFile"] as String)
            storePassword = keystoreProperties["storePassword"] as String
            keyAlias = keystoreProperties["keyAlias"] as String
            keyPassword = keystoreProperties["keyPassword"] as String
        }
    }
}
```

2. Update release build type:
```kotlin
buildTypes {
    release {
        isMinifyEnabled = true  // Change from false to true
        isShrinkResources = true  // Add this line
        proguardFiles(
            getDefaultProguardFile("proguard-android-optimize.txt"),
            "proguard-rules.pro"
        )
        signingConfig = signingConfigs.getByName("release")  // Add this line
    }
}
```

3. Update application ID (change from `com.tetrixa`):
```kotlin
defaultConfig {
    applicationId = "com.yourcompany.tetrixa"  // Change this
    minSdk = 24
    targetSdk = 35
    versionCode = 1
    versionName = "1.0.0"  // Update format
    ...
}
```

### 2. Update ProGuard Rules

Ensure `app/proguard-rules.pro` includes necessary keep rules:

```proguard
# Keep game classes
-keep class com.tetrixa.** { *; }

# Keep data classes
-keepclassmembers class * {
    @kotlinx.serialization.SerialName <fields>;
}

# Keep Parcelable implementations
-keepclassmembers class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator CREATOR;
}
```

### 3. Build Release Bundle (AAB)

Google Play requires Android App Bundle (AAB) format:

```bash
./gradlew bundleRelease
```

Output: `app/build/outputs/bundle/release/app-release.aab`

---

## Version Management

### 1. Update Version in `app/build.gradle.kts`

```kotlin
android {
    defaultConfig {
        applicationId "com.tetrixa"  // Change to your package name
        minSdk = 24
        targetSdk = 35
        versionCode = 1  // Increment for each release
        versionName = "1.0.0"  // User-visible version
    }
}
```

**Version Code Rules**:
- Must be an integer
- Must increase with each release
- Cannot be decreased
- Example: 1, 2, 3, ... or 100, 101, 102, ...

**Version Name Rules**:
- User-visible version string
- Format: `MAJOR.MINOR.PATCH` (e.g., "1.0.0", "1.1.0", "2.0.0")

### 2. Update Application ID

Change from `com.tetrixa` to your unique package name:
- Format: `com.yourcompany.tetrixa` or `com.yourname.tetrixa`
- Must be unique across Google Play Store
- Cannot be changed after first release

---

## Play Store Listing Requirements

### 1. App Title
- **Maximum**: 50 characters
- **Current**: "TETRIXA" (or "TETRIXA - Neon Tetris")
- **Action Required**: Update `app/src/main/res/values/strings.xml`:
```xml
<string name="app_name">Tetrixa</string>
```

### 2. Short Description
- **Maximum**: 80 characters
- **Example**: "Classic Tetris with modern neon design and smooth drag controls"

### 3. Full Description
- **Maximum**: 4000 characters
- **Should include**:
  - Game features
  - Controls explanation
  - Settings options
  - Developer information

**Example**:
```
Tetrixa is a modern take on the classic Tetris puzzle game, featuring:

🎮 GAMEPLAY FEATURES
• Smooth drag-based controls for intuitive piece movement
• Configurable grid sizes (8-12 columns, 16-24 rows)
• Adjustable game speed (5 difficulty levels)
• Score-based level progression
• Ghost piece preview for better placement

🎨 VISUAL DESIGN
• Candy Crush-style vibrant UI with soft gradients
• Dark gameplay theme for visual comfort
• Glossy, crystal-like block rendering
• Smooth animations and transitions
• RGB neon button accents

⚙️ CUSTOMIZATION
• Adjustable game speed (Very Slow to Very Fast)
• Configurable grid dimensions
• Settings persist between sessions

Developed by Bibin Shajan
```

### 4. App Category
- **Primary**: Games → Puzzle
- **Secondary**: Games → Arcade (optional)

### 5. Content Rating
- Complete Google Play's content rating questionnaire
- Expected rating: **Everyone** (PEGI 3, ESRB Everyone)

### 6. Privacy Policy
- **Required**: Yes (if app collects any data)
- **Current permissions**: VIBRATE (for haptic feedback)
- **Privacy Policy URL**: Required even if no data collection
- Create a simple privacy policy stating:
  - App does not collect personal data
  - Vibration permission is for haptic feedback only
  - No third-party analytics or tracking

---

## Graphics and Assets

### 1. App Icon
- **Required sizes**:
  - 512 × 512 pixels (Play Store icon)
  - 1024 × 1024 pixels (High-res icon)
- **Format**: PNG (24-bit, no transparency)
- **Design**: Should represent the game clearly
- **Current**: Update app icon in `app/src/main/res/mipmap-*/`

### 2. Feature Graphic
- **Size**: 1024 × 500 pixels
- **Format**: PNG or JPG
- **Purpose**: Banner shown at top of Play Store listing
- **Content**: Game screenshot or promotional graphic

### 3. Screenshots
- **Minimum**: 2 screenshots
- **Recommended**: 4-8 screenshots
- **Sizes**:
  - Phone: 16:9 or 9:16 aspect ratio
  - Tablet: 16:9 or 9:16 aspect ratio
- **Content**: Show gameplay, menus, settings, different game states

### 4. Promotional Graphics (Optional)
- **Promo graphic**: 180 × 120 pixels
- **TV banner**: 1280 × 720 pixels
- **Wear icon**: 512 × 512 pixels

### 5. Current Asset Locations
- Icons: `app/src/main/res/mipmap-*/`
- Update these with high-quality, Play Store-ready graphics

---

## Privacy Policy and Permissions

### 1. Current Permissions

**AndroidManifest.xml**:
```xml
<uses-permission android:name="android.permission.VIBRATE" />
```

**Privacy Policy Requirements**:
- Explain why vibration permission is needed (haptic feedback)
- State that no personal data is collected
- State that no data is shared with third parties
- Include contact information

**Sample Privacy Policy** (host on your website or GitHub Pages):
```
Privacy Policy for Tetrixa

Last updated: [Date]

Tetrixa ("we", "our", or "us") respects your privacy.

DATA COLLECTION
Tetrixa does not collect, store, or transmit any personal information.

PERMISSIONS
- Vibration: Used solely for haptic feedback during gameplay to enhance user experience. No data is collected or transmitted.

LOCAL STORAGE
Game settings (grid size, game speed) are stored locally on your device using Android's SharedPreferences. This data never leaves your device.

THIRD-PARTY SERVICES
Tetrixa does not use any third-party analytics, advertising, or tracking services.

CONTACT
For questions about this privacy policy, contact: [your-email@example.com]

CHANGES
We may update this privacy policy. Changes will be posted on this page.
```

### 2. Update AndroidManifest.xml

**Current State**: VIBRATE permission is used in code but not declared in manifest

**Required Changes**:

Add VIBRATE permission before `<application>` tag:

```xml
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools">

    <!-- Add this permission for haptic feedback -->
    <uses-permission android:name="android.permission.VIBRATE" />
    
    <application
        android:allowBackup="true"
        android:dataExtractionRules="@xml/data_extraction_rules"
        android:fullBackupContent="@xml/backup_rules"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/Theme.TETRIXA"
        tools:targetApi="31">
        ...
    </application>
</manifest>
```

**Note**: The app currently uses vibration for haptic feedback, so this permission is required.

---

## Content Rating

### 1. Complete Content Rating Questionnaire

Google Play requires content rating. Expected questions:

- **Violence**: None
- **Sexual Content**: None
- **Profanity**: None
- **Gambling**: None
- **Drugs/Alcohol**: None
- **User-Generated Content**: None
- **Location Sharing**: None

**Expected Rating**: **Everyone** (PEGI 3, ESRB Everyone)

### 2. Target Audience
- **Age**: All ages
- **Content**: Puzzle game, no objectionable content

---

## Testing Requirements

### 1. Pre-Release Testing Checklist

- [ ] Test on multiple device sizes (phone, tablet)
- [ ] Test on different Android versions (API 24-34)
- [ ] Test all game features:
  - [ ] Drag controls work correctly
  - [ ] Rotation works on tap
  - [ ] Settings persist correctly
  - [ ] Speed settings affect gameplay
  - [ ] Grid size changes work
  - [ ] Pause/Resume works
  - [ ] Game over detection works
  - [ ] Score calculation is correct
- [ ] Test edge cases:
  - [ ] Very fast speed setting
  - [ ] Very slow speed setting
  - [ ] Small grid size
  - [ ] Large grid size
  - [ ] Rapid drag movements
  - [ ] Multi-touch (should be ignored)
- [ ] Test system UI:
  - [ ] Status bar doesn't overlap content
  - [ ] Navigation bar doesn't overlap content
  - [ ] Screen rotation (if supported)
- [ ] Performance testing:
  - [ ] No lag during gameplay
  - [ ] Smooth animations (60fps)
  - [ ] No memory leaks
  - [ ] Battery usage is reasonable

### 2. Internal Testing

Use Google Play Console's Internal Testing track:
1. Upload AAB to Internal Testing
2. Add testers via email
3. Test on real devices
4. Fix any issues found

### 3. Closed Beta Testing (Optional)

Use Closed Beta Testing track:
1. Upload AAB to Closed Beta
2. Invite beta testers
3. Collect feedback
4. Make improvements

---

## Deployment Steps

### Step 1: Prepare Release Build

1. Update version code and version name in `app/build.gradle.kts`
2. Update application ID (if changing from `com.tetrixa`)
3. Ensure signing configuration is set up
4. Build release bundle:

```bash
./gradlew clean
./gradlew bundleRelease
```

5. Verify AAB file: `app/build/outputs/bundle/release/app-release.aab`

### Step 2: Create Play Store Listing

1. Go to [Google Play Console](https://play.google.com/console)
2. Create new app
3. Fill in store listing:
   - App name
   - Short description
   - Full description
   - App icon
   - Feature graphic
   - Screenshots
   - Category

### Step 3: Complete Store Listing

1. **App Access**: Set to "All users" (or restricted if needed)
2. **Ads**: Declare if app contains ads (Tetrixa: No)
3. **Content Rating**: Complete questionnaire
4. **Privacy Policy**: Add URL
5. **Target Audience**: Set age range
6. **Data Safety**: Complete data safety form
   - Declare no data collection
   - Explain vibration permission

### Step 4: Upload Release

1. Go to **Production** (or **Internal Testing** first)
2. Create new release
3. Upload AAB file: `app-release.aab`
4. Add release notes (what's new in this version)
5. Review and roll out

### Step 5: Review and Publish

1. Review all information
2. Submit for review
3. Google typically reviews within 1-7 days
4. Once approved, app goes live

---

## Post-Deployment Checklist

- [ ] App is live on Play Store
- [ ] Test download and installation
- [ ] Monitor crash reports in Play Console
- [ ] Monitor user reviews
- [ ] Set up Google Play Console alerts
- [ ] Plan for future updates

---

## Future Updates

When releasing updates:

1. **Increment Version Code**: Always increase `versionCode`
2. **Update Version Name**: Update `versionName` (e.g., "1.0.1", "1.1.0")
3. **Update Release Notes**: Describe what's new
4. **Test Thoroughly**: Ensure no regressions
5. **Build New AAB**: `./gradlew bundleRelease`
6. **Upload to Play Console**: Create new release

---

## Action Items Checklist

Before deploying, complete these tasks:

### Immediate Actions Required

- [ ] **Change Application ID**: Update from `com.tetrixa` to unique package name (if needed)
- [ ] **Create Keystore**: Generate release keystore file
- [ ] **Add Signing Config**: Configure signing in `build.gradle.kts`
- [ ] **Enable Minification**: Set `isMinifyEnabled = true` for release
- [ ] **Add VIBRATE Permission**: Declare in `AndroidManifest.xml`
- [ ] **Update App Name**: Already set to "TETRIXA" in `strings.xml`
- [ ] **Update Version Name**: Change to "1.0.0" format
- [ ] **Create Privacy Policy**: Host on website/GitHub Pages
- [ ] **Prepare Graphics**: Create Play Store icons, screenshots, feature graphic
- [ ] **Test Release Build**: Build and test AAB before uploading

### Before First Release

- [ ] Complete Google Play Console account setup
- [ ] Fill out all store listing information
- [ ] Complete content rating questionnaire
- [ ] Upload all required graphics
- [ ] Test on multiple devices
- [ ] Review all permissions and data safety

---

## Important Notes

### ⚠️ Critical Warnings

1. **Keystore Security**: 
   - Never lose your keystore file
   - Never share keystore passwords
   - Back up keystore in secure location
   - If lost, you cannot update the app

2. **Application ID**:
   - Cannot be changed after first release
   - Choose carefully: `com.yourcompany.tetrixa`

3. **Version Code**:
   - Must always increase
   - Cannot be decreased
   - Plan version codes (e.g., 1, 2, 3, ... or 100, 101, 102, ...)

4. **Testing**:
   - Always test release builds before uploading
   - Use Internal Testing track first
   - Test on multiple devices

5. **Privacy Policy**:
   - Required even if no data collection
   - Must be accessible via URL
   - Must be kept up to date

---

## Resources

- [Google Play Console](https://play.google.com/console)
- [Android App Bundle Guide](https://developer.android.com/guide/app-bundle)
- [Play Store Listing Best Practices](https://support.google.com/googleplay/android-developer/answer/9859153)
- [Content Rating Guide](https://support.google.com/googleplay/android-developer/answer/9888170)
- [Data Safety Section](https://support.google.com/googleplay/android-developer/answer/10787469)

---

## Quick Reference

### Build Commands

```bash
# Clean build
./gradlew clean

# Build release bundle (AAB) - REQUIRED for Play Store
./gradlew bundleRelease

# Build release APK (for testing only, not for Play Store)
./gradlew assembleRelease

# Check build
./gradlew tasks
```

### Current Project Status

**Application ID**: `com.tetrixa` ✅ **READY** (valid for Play Store)
- Change to: `com.yourcompany.tetrixa` or similar unique package name

**Version**: 
- Version Code: `1` ✅
- Version Name: `"1.0"` (update to `"1.0.0"` for consistency)

**App Name**: `"TETRIXA"` ✅ **UPDATED**

**Minify**: `false` ⚠️ **SHOULD ENABLE** for release (`isMinifyEnabled = true`)

**Signing**: Not configured ⚠️ **MUST ADD** before release

**VIBRATE Permission**: Not in manifest ⚠️ **MUST ADD** (used in code)

### File Locations

- **Release AAB**: `app/build/outputs/bundle/release/app-release.aab`
- **Release APK**: `app/build/outputs/apk/release/app-release.apk`
- **Keystore**: Store securely outside project (not in version control)
- **ProGuard Rules**: `app/proguard-rules.pro`

### Version Management

- **Version Code**: Integer, must increase (1, 2, 3, ...)
- **Version Name**: String, user-visible ("1.0.0", "1.0.1", ...)
- **Location**: `app/build.gradle.kts` → `defaultConfig`

---

## Support

For issues or questions:
- Check [Google Play Console Help](https://support.google.com/googleplay/android-developer)
- Review [Android Developer Documentation](https://developer.android.com)
- Contact: [Your Contact Information]

---

**Last Updated**: [Current Date]
**App Version**: 1.0.0
**Target SDK**: 35
**Min SDK**: 24

