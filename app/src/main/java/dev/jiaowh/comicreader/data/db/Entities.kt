package dev.jiaowh.comicreader.data.db

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import dev.jiaowh.comicreader.data.remote.Chapter

@Entity(tableName = "comics")
data class ComicEntity(
    /** Site path, e.g. "/1234-batman.html". */
    @PrimaryKey val url: String,
    val title: String,
    val coverUrl: String?,
    val publisher: String?,
    val status: String?,
    val newsId: Int,
    val addedAt: Long,
    val lastCheckedAt: Long = 0,
    /** Chapter the user is on (or last finished). Null = not started. */
    val lastReadChapterId: Int? = null,
    val lastReadPage: Int = 0,
    val lastReadPageCount: Int = 0,
    val lastChapterFinished: Boolean = false,
    val lastReadAt: Long = 0,
    /** Chapters discovered by update checks since the user last opened this comic. */
    val newChapterCount: Int = 0,
    /** The [SeriesEntity] this comic is grouped under, if any. */
    val seriesId: Long? = null,
)

/** A user-made group of comics, shown as a collapsible section in the library. */
@Entity(tableName = "series")
data class SeriesEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val collapsed: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
)

@Entity(
    tableName = "chapters",
    primaryKeys = ["comicUrl", "id"],
    indices = [Index("comicUrl")],
    foreignKeys = [
        ForeignKey(
            entity = ComicEntity::class,
            parentColumns = ["url"],
            childColumns = ["comicUrl"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
)
data class ChapterEntity(
    val comicUrl: String,
    val id: Int,
    val number: Float,
    val title: String,
    val date: String,
    /** Reading order, 0 = first chapter. */
    val sortIndex: Int,
)

fun ChapterEntity.toChapter() = Chapter(id, number, title, date)

fun List<Chapter>.toEntities(comicUrl: String) = mapIndexed { index, chapter ->
    ChapterEntity(comicUrl, chapter.id, chapter.number, chapter.title, chapter.date, index)
}

/**
 * One entry in a series' reading order: a BatCave comic plus an issue range within it.
 * Done-ness isn't stored; it's derived from the comic's reading progress.
 */
@Entity(
    tableName = "reading_steps",
    indices = [Index("seriesId")],
    foreignKeys = [
        ForeignKey(
            entity = SeriesEntity::class,
            parentColumns = ["id"],
            childColumns = ["seriesId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
)
data class ReadingStepEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val seriesId: Long,
    val position: Int,
    /** Heading this step is listed under, e.g. "The Dark Knights". */
    val section: String,
    /** Display name, e.g. "Dark Nights: Metal #3". */
    val label: String,
    /** Comic title to look for on BatCave, without the year. */
    val title: String,
    /** Publication year, used to tell volumes apart. */
    val year: Int?,
    /** Issue range within the comic; both null = the whole comic (one-shots). */
    val firstIssue: Float?,
    val lastIssue: Float?,
    /** Optional steps are listed but skipped by "next up". */
    val optional: Boolean,
    val note: String?,
    /** The linked BatCave comic, once found. */
    val comicUrl: String? = null,
    /** Automatic matching ran and found nothing. */
    val unresolved: Boolean = false,
)

/** A library row plus reading-state figures computed in SQL. */
data class LibraryItem(
    @Embedded val comic: ComicEntity,
    val totalChapters: Int,
    /** Chapters after the current one. */
    val unreadCount: Int,
    val lastReadChapterTitle: String?,
    /** First chapter after the current one, for "resume" when the current is finished. */
    val nextChapterId: Int?,
)
