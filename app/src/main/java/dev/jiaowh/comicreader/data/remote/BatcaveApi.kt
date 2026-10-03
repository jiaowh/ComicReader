package dev.jiaowh.comicreader.data.remote

import dev.jiaowh.comicreader.data.net.Site
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import java.io.IOException

class SiteFormatException(message: String) : IOException(message)

/**
 * Scrapes batcave.biz. Selectors follow the site's DataLife Engine templates
 * (cross-checked against the keiyoushi BatCave extension).
 */
class BatcaveApi(private val client: OkHttpClient) {

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        coerceInputValues = true
    }

    /** The home page feed of recently updated comics. */
    suspend fun latest(page: Int): SearchPage = withContext(Dispatchers.IO) {
        val url = Site.BASE_URL.toHttpUrl().newBuilder()
            .apply { if (page > 1) addPathSegments("page/$page/") }
            .build()
        val doc = getDocument(url.toString())
        val comics = doc.select("#content-load > .latest.grid-item").mapNotNull { element ->
            val link = element.selectFirst(".latest__title > a") ?: return@mapNotNull null
            val title = link.ownText().trim().ifEmpty { return@mapNotNull null }
            ComicSummary(
                url = Site.toPath(link.absUrl("href")),
                title = title,
                coverUrl = element.selectFirst(".latest__img img")?.imageUrl(),
            )
        }.distinctBy { it.url }
        SearchPage(comics, hasNextPage = doc.selectFirst("li.pagination a[href]") != null)
    }

    suspend fun search(query: String, page: Int): SearchPage = withContext(Dispatchers.IO) {
        val url = Site.BASE_URL.toHttpUrl().newBuilder()
            .addPathSegment("search")
            .addPathSegment(query.trim())
            .apply {
                if (page > 1) {
                    addPathSegment("page")
                    addPathSegment(page.toString())
                }
            }
            .addPathSegment("")
            .build()
        val doc = getDocument(url.toString())
        val comics = doc.select("#dle-content > .readed").mapNotNull { element ->
            val link = element.selectFirst(".readed__title > a") ?: return@mapNotNull null
            ComicSummary(
                url = Site.toPath(link.absUrl("href")),
                title = link.ownText().trim(),
                coverUrl = element.selectFirst(".readed__img img")?.imageUrl(),
            )
        }
        val hasNextPage = doc.selectFirst("div.pagination__pages")?.children()?.lastOrNull()?.tagName() == "a"
        SearchPage(comics, hasNextPage)
    }

    suspend fun comicDetails(path: String): ComicDetails = withContext(Dispatchers.IO) {
        val doc = getDocument(Site.absolute(path))
        val title = doc.selectFirst("header.page__header h1")?.text()
            ?: throw SiteFormatException("Couldn't read the comic page. The site layout may have changed.")
        val chapterData = parseChapterData(doc)

        ComicDetails(
            url = path,
            title = title,
            coverUrl = doc.selectFirst("div.page__poster img")?.imageUrl(),
            publisher = doc.pageListLink("Publisher"),
            year = doc.pageListLink("Year"),
            status = doc.selectFirst(".page__list > li:has(> div:contains(Release type))")
                ?.ownText()?.trim()?.ifEmpty { null },
            description = doc.selectFirst("div.page__text")?.text()?.ifBlank { null },
            newsId = chapterData.newsId,
            chapters = chapterData.chapters
                .map { Chapter(it.id, it.number, it.title.ifBlank { "Chapter ${it.number.format()}" }, it.date) }
                .sortedWith(compareBy({ it.number }, { it.id })),
        )
    }

    suspend fun chapterImages(newsId: Int, chapterId: Int): List<String> = withContext(Dispatchers.IO) {
        val body = json.encodeToString(ChapterImagesRequest.serializer(), ChapterImagesRequest(newsId.toString(), chapterId.toString()))
            .toRequestBody("application/json".toMediaType())
        val request = Request.Builder()
            .url("${Site.BASE_URL}/engine/ajax/controller.php?mod=api&action=reader/getChapterData")
            .post(body)
            .header("Accept", "application/json, text/plain, */*")
            .build()
        val text = client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Couldn't load chapter (HTTP ${response.code})")
            response.body?.string().orEmpty()
        }
        val images = json.decodeFromString(ChapterImagesResponse.serializer(), text).data.images
        images.map { it.trim() }.filter { it.isNotEmpty() }.map(Site::absolute)
    }

    private fun getDocument(url: String): Document {
        val request = Request.Builder()
            .url(url)
            .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
            .header("Sec-Fetch-Dest", "document")
            .header("Sec-Fetch-Mode", "navigate")
            .header("Sec-Fetch-Site", "same-origin")
            .build()
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("BatCave returned HTTP ${response.code}")
            return Jsoup.parse(response.body?.string().orEmpty(), response.request.url.toString())
        }
    }

    private fun parseChapterData(doc: Document): ChapterListDto {
        val script = doc.select("script").map { it.data() }.firstOrNull { "window.__DATA__" in it }
            ?: throw SiteFormatException("Chapter list not found on the comic page.")
        val raw = script
            .substringAfter("window.__DATA__")
            .substringAfter("=")
            .substringBeforeLast(";")
            .trim()
        return json.decodeFromString(ChapterListDto.serializer(), raw)
    }

    /** Value of a `.page__list` row such as "Publisher: <a>DC</a>". */
    private fun Document.pageListLink(label: String): String? =
        selectFirst(".page__list > li:has(> div:contains($label))")
            ?.selectFirst("> a")
            ?.text()
            ?.takeIf { it.isNotBlank() }

    /** Covers are lazy-loaded, so prefer data-src. */
    private fun Element.imageUrl(): String? =
        absUrl("data-src").ifEmpty { absUrl("src") }.ifEmpty { null }
}

fun Float.format(): String = if (this % 1f == 0f) toInt().toString() else toString()
