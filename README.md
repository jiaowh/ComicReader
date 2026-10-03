# Comic Reader

A personal Android app for tracking the comics I'm reading on [BatCave](https://batcave.biz/) without juggling browser tabs.

<p>
  <img src="Screenshot_20261003_223952_Comic%20Reader.jpg" alt="Library: covers with unread badges and resume buttons" width="280">
  &nbsp;
  <img src="Screenshot_20261003_223957_Comic%20Reader.jpg" alt="Search: BatCave's latest releases" width="280">
</p>

- **Library**: covers of everything I'm following, where I left off (chapter and page), and a badge when new chapters are out. Long-press a comic to move it to a series or remove it.
- **Search**: search BatCave (or browse the latest releases) and add comics to the library.
- **Reader**: built-in page reader that saves progress on every page turn, with pinch/double-tap zoom and next-chapter navigation.
- **Series & reading orders**: group comics into collapsible series. A series can have a reading order that links issues across several comics (e.g. *Dark Nights: Metal* → its one-shots → *Death Metal*). The reader's last page continues to the next step in the order.
- **Update checks**: a background job checks the library every 6 hours and posts a notification for new chapters. Pull down on the library to check now.

## Built-in reading orders

The app ships with 50+ reading orders, from DC All In and the Marvel Ultimate line to Knightfall, Injustice and Elseworlds. Each order's books are matched to BatCave comics the first time it's opened; a book BatCave doesn't have is flagged and skipped by "Next up", and can be linked by hand. Orders sync with the app on launch, so edits and added sequels reach existing installs without losing progress.

The orders are defined in `data/ReadingOrderSeeds.kt`, `DcSeeds.kt`, `MarvelSeeds.kt` and `UltimateSeeds.kt`, each citing the reading guide it follows.

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
| Reading orders (status, next up, title matching, sync) | `data/ReadingOrder.kt` |
| New-chapter checks (WorkManager) | `work/UpdateWorker.kt` |
| Screens (Jetpack Compose) | `ui/` |

BatCave sits behind bot protection. The app shares one cookie store between OkHttp and Android's WebView and uses the WebView's user agent. When a request hits a challenge, a hidden WebView loads the page so the challenge can complete, and then the request is retried. If that fails, the app shows a **Verify** button that opens the site in a visible WebView so you can complete the check yourself.

The page selectors follow the site's DataLife Engine templates. If BatCave changes its layout, `BatcaveApi.kt` is the only file that should need updating. The [keiyoushi BatCave extension](https://github.com/keiyoushi/extensions-source/tree/main/src/en/batcave) is a good reference for current selectors.
