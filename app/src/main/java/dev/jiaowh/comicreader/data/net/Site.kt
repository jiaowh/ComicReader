package dev.jiaowh.comicreader.data.net

import okhttp3.HttpUrl.Companion.toHttpUrlOrNull

object Site {
    const val BASE_URL = "https://batcave.biz"
    const val HOST = "batcave.biz"

    /** Turns a site path (as stored in the database) into a full URL. */
    fun absolute(pathOrUrl: String): String = when {
        pathOrUrl.startsWith("http") -> pathOrUrl
        pathOrUrl.startsWith("//") -> "https:$pathOrUrl"
        pathOrUrl.startsWith("/") -> BASE_URL + pathOrUrl
        else -> "$BASE_URL/$pathOrUrl"
    }

    /** Strips the domain so stored keys survive a domain change. */
    fun toPath(url: String): String {
        val parsed = url.toHttpUrlOrNull() ?: return url
        val query = parsed.encodedQuery?.let { "?$it" }.orEmpty()
        return parsed.encodedPath + query
    }
}
