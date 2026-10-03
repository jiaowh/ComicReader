package dev.jiaowh.comicreader.ui

import kotlinx.serialization.Serializable

@Serializable
object LibraryRoute

@Serializable
object SearchRoute

@Serializable
data class ComicRoute(val url: String)

@Serializable
data class ReaderRoute(val url: String, val chapterId: Int, val page: Int = 0)

@Serializable
object VerifyRoute
