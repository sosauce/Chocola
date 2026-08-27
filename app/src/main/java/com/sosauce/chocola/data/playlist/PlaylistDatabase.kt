package com.sosauce.chocola.data.playlist

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.sosauce.chocola.data.MediaItemConverter
import com.sosauce.chocola.data.models.Playlist

@Database(
    entities = [Playlist::class],
    version = 2
)
@TypeConverters(MediaItemConverter::class)
abstract class PlaylistDatabase : RoomDatabase() {
    abstract val dao: PlaylistDao

}

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE Playlist ADD COLUMN color INTEGER NOT NULL DEFAULT -1")
        db.execSQL("ALTER TABLE Playlist ADD COLUMN tags TEXT NOT NULL DEFAULT '[]'")
    }
}
