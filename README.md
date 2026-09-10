# Score Counter

Native Android score counter app in Kotlin + Jetpack Compose.

## Included

- Two-team, three-player, and four-player layouts
- Per-player score editing with undo/redo stacks
- Bottom sheets for mode selection, settings, and score picking
- DataStore-backed settings for theme, sound, animation, and language
- SoundPool integration with placeholder WAV assets
- RTL-friendly Kurdish/Arabic UI support

## Open In Android Studio

1. Open this folder in Android Studio Hedgehog or newer.
2. Sync the project with the included Gradle wrapper.
3. Make sure your Android SDK is installed and configured.
4. Run the `app` configuration on an emulator or device.

## Notes

- UI sounds now use assets from Kenney's `Interface Sounds` pack (CC0): https://kenney.nl/assets/interface-sounds
- Hilt was left out to keep the project lightweight; the app uses a simple `ViewModelProvider.Factory` instead.
- If the Android SDK is not configured on your machine, create `local.properties` with a valid `sdk.dir=...` path or set `ANDROID_HOME`.
- GitHub Actions CI is included in `.github/workflows/android.yml` so the project can build a debug APK in the cloud.

## Voice Announcements (new in 1.1)

- Adding or subtracting points is spoken out loud with the real team name, e.g. "15 for Nashat".
- Uses the Android TextToSpeech engine; toggle it in Settings ("Voice Announcements").
- Kurdish TTS voices are rare on Android devices; the app automatically falls back to English speech while keeping the actual team name.

## Dark Mode (fixed in 1.1)

- The whole UI (background, cards, sheets, dialogs, text fields, status bar icons) now follows the selected Light/Dark/System theme.

## Building On This Machine

- `local.properties` must point at the local Android SDK: `sdk.dir=C:\Users\Administrator\AppData\Local\Android\Sdk`
- Build with: `set JAVA_HOME=C:\Users\Administrator\.jdks\jdk-21.0.12.1+1` then `gradlew.bat assembleDebug`
