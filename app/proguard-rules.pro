# jsoup optionally references re2j, which we don't ship.
-dontwarn com.google.re2j.**

# Keep serializable DTOs parsed from the site's JSON.
-keep @kotlinx.serialization.Serializable class dev.jiaowh.comicreader.** { *; }
