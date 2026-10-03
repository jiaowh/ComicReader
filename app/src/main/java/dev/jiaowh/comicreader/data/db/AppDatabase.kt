package dev.jiaowh.comicreader.data.db

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [ComicEntity::class, ChapterEntity::class, SeriesEntity::class],
    version = 2,
    autoMigrations = [
        // v2: series table + comics.seriesId
        AutoMigration(from = 1, to = 2),
    ],
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun comicDao(): ComicDao

    companion object {
        fun create(context: Context): AppDatabase =
            Room.databaseBuilder(context, AppDatabase::class.java, "comics.db").build()
    }
}
