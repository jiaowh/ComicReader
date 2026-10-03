package dev.jiaowh.comicreader.ui

import kotlinx.serialization.Serializable

@Serializable
object LibraryRoute

/** With [linkStepId] set, picking a result links it to that reading-order step instead of opening it. */
@Serializable
data class SearchRoute(
    val query: String = "",
    val linkStepId: Long? = null,
    val linkLabel: String? = null,
)

@Serializable
data class ComicRoute(val url: String)

@Serializable
data class ReaderRoute(val url: String, val chapterId: Int, val page: Int = 0)

@Serializable
data class ReadingOrderRoute(val seriesId: Long)

@Serializable
object VerifyRoute
