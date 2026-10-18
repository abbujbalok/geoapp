# geoapp

An offline map of Bangladesh for Android, built with Kotlin and
[osmdroid](https://github.com/osmdroid/osmdroid) (OpenStreetMap). No API key,
no fees.

## How it works

- On first launch, tap **Download offline map**. The app downloads OpenStreetMap
  tiles for the whole Bangladesh bounding box at zoom levels **5-12** into the
  device's app storage (~100 MB, keep internet on).
- Wait for the download to finish, then tap **Go offline**. From then on the
  map works with **no internet at all**.
- Tap **Go online** to switch back to live tiles.

## Building

The debug APK is built automatically on GitHub Actions (no local Android SDK
needed).

- Push to `main` (or run the workflow manually from the Actions tab).
- Download the APK from the run's artifact named **app-debug-apk**.

Workflow file: `.github/workflows/android.yml`

## Notes

- Tiles come from the public OpenStreetMap tile servers, which are meant for
  light use. If the download stops early with errors, retry later, or lower
  `maxZoom` in `MainActivity.kt` to 10-11 to download fewer tiles.
- Data (c) OpenStreetMap contributors, available under the Open Database License.
