# ScreenLock App

Welcome to **ScreenLock**, the ultimate smart lock application crafted specifically for Android. Built with a stunning modern UI, ScreenLock gives you complete control over how and when your device locks.

Proudly developed by **lazyar Tech Company**.

## Features

- **Instant Lock**: Lock your device instantly using a sleek and easily accessible Home screen button.
- **Floating Widget**: Enable the floating lock widget to always have a quick-lock button hovering above other apps.
- **Smart Modes**: 
  - **Voice Lock**: Simply speak to lock your device.
  - **Scheduler**: Set specific times when your device should automatically lock, seamlessly integrated with your daily routine.
- **Beautiful Design**: A premium, cyber-inspired aesthetic utilizing Jetpack Compose for smooth animations, glassmorphic panels, and glowing neon accents.
- **Settings & Preferences**: Fully customizable settings for haptics, sounds, and floating button visibility.

## Technical Details

- **Platform**: Android
- **UI Framework**: Jetpack Compose (Material 3)
- **Architecture**: MVVM with distinct separation between presentation and background services.
- **Services**: Utilizes `AccessibilityService` for secure device locking, and Foreground Services for persistent features like the floating widget and voice listener.

## Installation

1. Clone this repository.
2. Open the project in Android Studio.
3. Build and run the `app` module on a device running Android 8.0 (API 26) or higher.

## Privacy & Permissions

This app requires the following permissions to function correctly:
- **Accessibility Service**: Required to perform the system-level action of locking the screen.
- **Display Over Other Apps (Overlay)**: Required to show the floating lock button.
- **Microphone**: Required for the voice lock feature.
- **Notifications**: Required to keep background services persistent.

All processing is handled completely locally on the device to ensure your privacy. We do not transmit or store your personal data.

## Support
For assistance, check out the Help & FAQ section in the app settings, or reach out to lazyar Tech Company support.
