package dev.jiaowh.comicreader.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** A comic as shown in search results. [url] is a site path, e.g. "/1234-batman.html". */
data class ComicSummary(
    val url: String,
    val title: String,
    val coverUrl: String?,
)

data class SearchPage(
    val comics: List<ComicSummary>,
    val hasNextPage: Boolean,
)

data class Chapter(
    val id: Int,
    val number: Float,
    val title: String,
    val date: String,
)

data class ComicDetails(
    val url: String,
    val title: String,
    val coverUrl: String?,
    val publisher: String?,
    val year: String?,
    val status: String?,
    val description: String?,
    /** The site's internal comic id, needed by the reader API. */
    val newsId: Int,
    /** Oldest first. */
    val chapters: List<Chapter>,
)

// --- JSON shapes used by the site ---

/** Embedded in comic pages as `window.__DATA__ = {...};` */
@Serializable
class ChapterListDto(
    @SerialName("news_id") val newsId: Int,
    val chapters: List<ChapterDto> = emptyList(),
)

@Serializable
class ChapterDto(
    val id: Int,
    @SerialName("posi") val number: Float = 0f,
    val title: String = "",
    val date: String = "",
)

@Serializable
class ChapterImagesRequest(
    @SerialName("news_id") val newsId: String,
    @SerialName("chapter_id") val chapterId: String,
)

@Serializable
class ChapterImagesResponse(
    val data: ChapterImagesDto,
)

@Serializable
class ChapterImagesDto(
    val images: List<String> = emptyList(),
)
