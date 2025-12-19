# TETRIXA - Google Play Store Deployment Instructions

## ✅ Build Complete

Your release bundle (AAB) has been successfully built!

**Location**: `app/build/outputs/bundle/release/app-release.aab`

---

## 📋 Pre-Deployment Checklist

Before uploading to Google Play Store, ensure you have:

- [x] ✅ Release bundle (AAB) built successfully
- [x] ✅ App signed with release keystore
- [ ] ⚠️ Google Play Developer Account ($25 one-time fee)
- [ ] ⚠️ Privacy Policy URL (required even if no data collection)
- [ ] ⚠️ App screenshots (minimum 2, recommended 4-8)
- [ ] ⚠️ Feature graphic (1024 × 500 pixels)
- [ ] ⚠️ App icon (512 × 512 pixels)

---

## 🚀 Step-by-Step Deployment Guide

### Step 1: Access Google Play Console

1. Go to [Google Play Console](https://play.google.com/console)
2. Sign in with your Google account
3. Pay the $25 one-time registration fee (if not already done)
4. Accept the Developer Distribution Agreement

### Step 2: Create New App

1. Click **"Create app"** button
2. Fill in the app details:
   - **App name**: `TETRIXA`
   - **Default language**: English (United States)
   - **App or game**: Game
   - **Free or paid**: Free
   - Click **"Create app"**

### Step 3: Complete Store Listing

Navigate to **Store presence** → **Main store listing**

#### Required Information:

**App Name**: `TETRIXA`
- Maximum 50 characters

**Short description** (80 characters max):
```
Classic Tetris with modern design and smooth drag controls
```

**Full description** (4000 characters max):
```
TETRIXA is a modern take on the classic Tetris puzzle game, featuring:

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

HOW TO PLAY:
• Drag left/right to move pieces
• Tap to rotate
• Drag down for soft drop
• Clear lines to score points

Enjoy the classic Tetris experience with a modern twist!
```

**App icon**: Upload 512 × 512 pixels PNG
- Location: `app/src/main/res/mipmap-*/ic_launcher.png`
- Or create a new one with TETRIXA branding

**Feature graphic**: 1024 × 500 pixels
- Create a banner showcasing the game
- Include game screenshots or promotional graphics

**Screenshots** (Minimum 2, recommended 4-8):
- Phone screenshots: 16:9 or 9:16 aspect ratio
- Show gameplay, menus, settings, different game states
- Take screenshots from your device or emulator

**Category**: 
- Primary: Games → Puzzle
- Secondary: Games → Arcade (optional)

### Step 4: Set Up App Content

Navigate to **Policy** → **App content**

#### Content Rating:
1. Click **"Start questionnaire"**
2. Answer questions:
   - Violence: None
   - Sexual content: None
   - Profanity: None
   - Gambling: None
   - Drugs/Alcohol: None
   - User-generated content: None
   - Location sharing: None
3. Expected rating: **Everyone** (PEGI 3, ESRB Everyone)

#### Privacy Policy:
1. Go to **Privacy policy**
2. Enter your privacy policy URL
   - If you don't have one, create a simple page on GitHub Pages or your website
   - See sample privacy policy below

**Sample Privacy Policy** (host on your website/GitHub Pages):
```
Privacy Policy for TETRIXA

Last updated: [Current Date]

TETRIXA ("we", "our", or "us") respects your privacy.

DATA COLLECTION
TETRIXA does not collect, store, or transmit any personal information.

PERMISSIONS
- Vibration: Used solely for haptic feedback during gameplay to enhance user experience. No data is collected or transmitted.

LOCAL STORAGE
Game settings (grid size, game speed) are stored locally on your device using Android's SharedPreferences. This data never leaves your device.

THIRD-PARTY SERVICES
TETRIXA does not use any third-party analytics, advertising, or tracking services.

CONTACT
For questions about this privacy policy, contact: [your-email@example.com]

CHANGES
We may update this privacy policy. Changes will be posted on this page.
```

#### Data Safety:
1. Go to **Data safety**
2. Answer questions:
   - Does your app collect or share user data? **No**
   - Does your app use encryption? **No** (not required for this app)
   - Complete the form indicating no data collection

### Step 5: Upload Release Bundle

1. Navigate to **Production** (or **Internal testing** for initial testing)
2. Click **"Create new release"**
3. Upload your AAB file:
   - **File location**: `app/build/outputs/bundle/release/app-release.aab`
   - Drag and drop or click to upload
4. Fill in **Release notes**:
   ```
   Initial release of TETRIXA!
   
   Features:
   - Classic Tetris gameplay
   - Smooth drag-based controls
   - Configurable grid size and game speed
   - Modern Candy Crush-style UI
   - Score-based level progression
   ```
5. Click **"Save"**

### Step 6: Review and Submit

1. Review all information:
   - Store listing complete
   - Content rating complete
   - Privacy policy added
   - Data safety form complete
   - Release uploaded
2. Click **"Review release"**
3. If everything looks good, click **"Start rollout to Production"**
4. Your app will be submitted for review
   - Review typically takes 1-7 days
   - You'll receive email notifications about the status

---

## 🔐 Important Security Notes

### Keystore Information

**⚠️ CRITICAL**: Keep your keystore file safe!

- **Keystore file**: `tetrixa-release-key.jks` (in project root)
- **Keystore password**: `tetrixa2024`
- **Key alias**: `tetrixa-key`
- **Key password**: `tetrixa2024`

**IMPORTANT**:
- ⚠️ **Back up the keystore file** in multiple secure locations
- ⚠️ **Never commit** the keystore to version control (already in `.gitignore`)
- ⚠️ **If you lose the keystore**, you cannot update your app on Play Store
- ⚠️ **Consider changing passwords** before production release

### Recommended Actions:

1. **Backup keystore**:
   ```bash
   # Copy to secure location (USB drive, cloud storage, etc.)
   cp tetrixa-release-key.jks /path/to/secure/backup/
   cp keystore.properties /path/to/secure/backup/
   ```

2. **Change passwords** (optional but recommended):
   - Generate new keystore with stronger passwords
   - Update `keystore.properties` with new passwords
   - Rebuild release bundle

---

## 📱 Testing Before Release

### Internal Testing Track (Recommended First Step):

1. Upload AAB to **Internal testing** track first
2. Add testers via email
3. Test on real devices
4. Fix any issues found
5. Then promote to Production

### Testing Checklist:

- [ ] Test on multiple device sizes (phone, tablet)
- [ ] Test on different Android versions
- [ ] Verify all game features work
- [ ] Test drag controls
- [ ] Test rotation
- [ ] Test settings persistence
- [ ] Test speed settings
- [ ] Test grid size changes
- [ ] Test pause/resume
- [ ] Test game over detection
- [ ] Verify no crashes or errors

---

## 📊 Post-Deployment

### Monitor Your App:

1. **Play Console Dashboard**:
   - Monitor installs, ratings, reviews
   - Check crash reports
   - Review user feedback

2. **Set Up Alerts**:
   - Enable email notifications for reviews
   - Monitor crash reports
   - Track performance metrics

3. **Respond to Reviews**:
   - Engage with users
   - Address issues promptly
   - Thank positive reviewers

---

## 🔄 Future Updates

When releasing updates:

1. **Increment Version**:
   - Update `versionCode` in `app/build.gradle.kts` (must increase)
   - Update `versionName` (e.g., "1.0.1", "1.1.0")

2. **Build New Release**:
   ```bash
   ./gradlew clean bundleRelease
   ```

3. **Upload to Play Console**:
   - Create new release
   - Upload new AAB
   - Add release notes
   - Submit for review

---

## 📞 Support Resources

- [Google Play Console Help](https://support.google.com/googleplay/android-developer)
- [Android Developer Documentation](https://developer.android.com)
- [Play Console](https://play.google.com/console)

---

## ✅ Quick Reference

**AAB Location**: `app/build/outputs/bundle/release/app-release.aab`

**Application ID**: `com.tetrixa`

**Version**: 
- Version Code: `1`
- Version Name: `1.0.0`

**Min SDK**: 24 (Android 7.0)
**Target SDK**: 35 (Android 15)

**Keystore**: `tetrixa-release-key.jks` (in project root)

---

**Good luck with your deployment! 🚀**

