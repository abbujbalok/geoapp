# geoapp

A simple Android "Hello World" app built with Kotlin and the Android Gradle Plugin.

## Building

The debug APK is built automatically on GitHub Actions (no local Android SDK needed).

- Push to `main` (or open a PR, or run the workflow manually from the Actions tab).
- The workflow downloads the debug APK as an artifact named **app-debug-apk**.

Workflow file: `.github/workflows/android.yml`
