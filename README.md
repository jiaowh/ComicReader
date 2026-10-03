# Comic Reader

A personal Android app for tracking the comics I'm reading on [BatCave](https://batcave.biz/) without juggling browser tabs.

- **Library**: covers of everything I'm following, where I left off (chapter and page), and a badge when new chapters are out.
- **Search**: search BatCave (or browse the latest releases) and add comics to the library.
- **Reader**: built-in page reader that saves progress on every page turn, with pinch/double-tap zoom and next-chapter navigation.
- **Update checks**: a background job checks the library every 6 hours and posts a notification for new chapters. Pull down on the library to check now.

## Building

Open the project in Android Studio, or from the command line:

```sh
./gradlew assembleDebug        # app/build/outputs/apk/debug/app-debug.apk
./gradlew assembleRelease      # minified, signed with the debug key for sideloading
```

Requires JDK 17 and the Android SDK (platform 35). Install on a connected phone with `./gradlew installDebug`.

## How it works

| Piece | Where |
| --- | --- |
| Scraping (search, comic page, chapter images) | `data/remote/BatcaveApi.kt` |
| Site protection (Cloudflare + DLE Guard) | `data/net/SiteProtection.kt` |
| Library and progress storage (Room) | `data/db/`, `data/ComicRepository.kt` |
| New-chapter checks (WorkManager) | `work/UpdateWorker.kt` |
| Screens (Jetpack Compose) | `ui/` |

BatCave sits behind bot protection. The app shares one cookie store between OkHttp and Android's WebView and uses the WebView's user agent. When a request hits a challenge, a hidden WebView loads the page so the challenge can complete, and then the request is retried. If that fails, the app shows a **Verify** button that opens the site in a visible WebView so you can complete the check yourself.

The page selectors follow the site's DataLife Engine templates. If BatCave changes its layout, `BatcaveApi.kt` is the only file that should need updating. The [keiyoushi BatCave extension](https://github.com/keiyoushi/extensions-source/tree/main/src/en/batcave) is a good reference for current selectors.
