package dev.jiaowh.comicreader.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ComicDao {

    @Query(
        """
        SELECT c.*,
            (SELECT COUNT(*) FROM chapters t WHERE t.comicUrl = c.url) AS totalChapters,
            (SELECT COUNT(*) FROM chapters u WHERE u.comicUrl = c.url AND u.sortIndex > IFNULL(
                (SELECT r.sortIndex FROM chapters r WHERE r.comicUrl = c.url AND r.id = c.lastReadChapterId), -1)
            ) AS unreadCount,
            (SELECT r.title FROM chapters r WHERE r.comicUrl = c.url AND r.id = c.lastReadChapterId) AS lastReadChapterTitle,
            (SELECT n.id FROM chapters n WHERE n.comicUrl = c.url AND n.sortIndex > IFNULL(
                (SELECT r.sortIndex FROM chapters r WHERE r.comicUrl = c.url AND r.id = c.lastReadChapterId), -1)
                ORDER BY n.sortIndex LIMIT 1
            ) AS nextChapterId
        FROM comics c
        ORDER BY (c.newChapterCount > 0) DESC, MAX(c.lastReadAt, c.addedAt) DESC
        """,
    )
    fun libraryItems(): Flow<List<LibraryItem>>

    @Query("SELECT url FROM comics")
    fun libraryUrls(): Flow<List<String>>

    @Query("SELECT * FROM comics WHERE url = :url")
    fun comicFlow(url: String): Flow<ComicEntity?>

    @Query("SELECT * FROM comics WHERE url = :url")
    suspend fun comic(url: String): ComicEntity?

    @Query("SELECT * FROM comics")
    suspend fun allComics(): List<ComicEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertComic(comic: ComicEntity)

    @Update
    suspend fun updateComic(comic: ComicEntity)

    @Query("DELETE FROM comics WHERE url = :url")
    suspend fun deleteComic(url: String)

    @Query("SELECT * FROM chapters WHERE comicUrl = :url ORDER BY sortIndex")
    fun chaptersFlow(url: String): Flow<List<ChapterEntity>>

    @Query("SELECT * FROM chapters WHERE comicUrl = :url ORDER BY sortIndex")
    suspend fun chapters(url: String): List<ChapterEntity>

    @Query("DELETE FROM chapters WHERE comicUrl = :url")
    suspend fun deleteChapters(url: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChapters(chapters: List<ChapterEntity>)

    @Transaction
    suspend fun replaceChapters(url: String, chapters: List<ChapterEntity>) {
        deleteChapters(url)
        insertChapters(chapters)
    }

    @Query(
        """
        UPDATE comics SET lastReadChapterId = :chapterId, lastReadPage = :page, lastReadPageCount = :pageCount,
            lastChapterFinished = :finished, lastReadAt = :at
        WHERE url = :url
        """,
    )
    suspend fun updateProgress(url: String, chapterId: Int?, page: Int, pageCount: Int, finished: Boolean, at: Long)

    @Query("UPDATE comics SET newChapterCount = 0 WHERE url = :url")
    suspend fun clearNewChapters(url: String)

    @Query("UPDATE comics SET seriesId = :seriesId WHERE url = :url")
    suspend fun setComicSeries(url: String, seriesId: Long?)

    // --- Series ---

    @Query("SELECT * FROM series ORDER BY createdAt")
    fun seriesFlow(): Flow<List<SeriesEntity>>

    @Query("SELECT * FROM series WHERE name = :name COLLATE NOCASE LIMIT 1")
    suspend fun seriesByName(name: String): SeriesEntity?

    @Insert
    suspend fun insertSeries(series: SeriesEntity): Long

    @Query("UPDATE series SET name = :name WHERE id = :id")
    suspend fun renameSeries(id: Long, name: String)

    @Query("UPDATE series SET collapsed = :collapsed WHERE id = :id")
    suspend fun setSeriesCollapsed(id: Long, collapsed: Boolean)

    @Query("DELETE FROM series WHERE id = :id")
    suspend fun deleteSeriesRow(id: Long)

    @Query("UPDATE comics SET seriesId = NULL WHERE seriesId = :id")
    suspend fun ungroupSeries(id: Long)

    @Query("DELETE FROM comics WHERE seriesId = :id")
    suspend fun deleteComicsInSeries(id: Long)

    // --- Reading orders ---

    @Query("SELECT * FROM reading_steps ORDER BY seriesId, position")
    fun allStepsFlow(): Flow<List<ReadingStepEntity>>

    @Query("SELECT * FROM reading_steps WHERE seriesId = :seriesId ORDER BY position")
    suspend fun steps(seriesId: Long): List<ReadingStepEntity>

    @Query("SELECT * FROM reading_steps WHERE id = :id")
    suspend fun step(id: Long): ReadingStepEntity?

    @Insert
    suspend fun insertSteps(steps: List<ReadingStepEntity>)

    @Update
    suspend fun updateStep(step: ReadingStepEntity)

    @Query("DELETE FROM reading_steps WHERE id IN (:ids)")
    suspend fun deleteSteps(ids: List<Long>)

    @Query("SELECT * FROM series WHERE id = :id")
    suspend fun seriesById(id: Long): SeriesEntity?

    @Query("UPDATE reading_steps SET comicUrl = :url, unresolved = :unresolved WHERE id = :id")
    suspend fun setStepComic(id: Long, url: String?, unresolved: Boolean)

    /** Lets matching run again: clears "not found" flags and links to comics no longer in the library. */
    @Query(
        """
        UPDATE reading_steps SET unresolved = 0,
            comicUrl = CASE WHEN comicUrl IN (SELECT url FROM comics) THEN comicUrl ELSE NULL END
        WHERE seriesId = :seriesId
        """,
    )
    suspend fun resetUnmatchedSteps(seriesId: Long)

    @Query("SELECT * FROM comics WHERE url IN (SELECT comicUrl FROM reading_steps WHERE comicUrl IS NOT NULL)")
    fun stepComicsFlow(): Flow<List<ComicEntity>>

    @Query("SELECT * FROM chapters WHERE comicUrl IN (SELECT comicUrl FROM reading_steps WHERE comicUrl IS NOT NULL)")
    fun stepChaptersFlow(): Flow<List<ChapterEntity>>

    /** Deletes a series; its comics either go with it or fall back to ungrouped. */
    @Transaction
    suspend fun deleteSeries(id: Long, deleteComics: Boolean) {
        if (deleteComics) deleteComicsInSeries(id) else ungroupSeries(id)
        deleteSeriesRow(id)
    }
}
