package com.sosauce.chocola.core.data.local

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "favorite_tracks")
data class FavoriteTrackEntity(@PrimaryKey val mediaId: Long, val dateAdded: Long)

@Entity(tableName = "favorite_albums")
data class FavoriteAlbumEntity(@PrimaryKey val mediaId: Long, val dateAdded: Long)

@Entity(tableName = "favorite_artists")
data class FavoriteArtistEntity(@PrimaryKey val mediaId: Long, val dateAdded: Long)

@Dao
interface FavoritesDao {
    @Query("SELECT mediaId FROM favorite_tracks")
    fun observeTrackIds(): Flow<List<Long>>
    @Query("SELECT mediaId FROM favorite_albums")
    fun observeAlbumIds(): Flow<List<Long>>
    @Query("SELECT mediaId FROM favorite_artists")
    fun observeArtistIds(): Flow<List<Long>>
    @Query("SELECT EXISTS(SELECT 1 FROM favorite_tracks WHERE mediaId = :id)")
    suspend fun isTrackFavorite(id: Long): Boolean
    @Query("SELECT EXISTS(SELECT 1 FROM favorite_albums WHERE mediaId = :id)")
    suspend fun isAlbumFavorite(id: Long): Boolean
    @Query("SELECT EXISTS(SELECT 1 FROM favorite_artists WHERE mediaId = :id)")
    suspend fun isArtistFavorite(id: Long): Boolean
    @Upsert
    suspend fun addTrack(item: FavoriteTrackEntity)
    @Upsert
    suspend fun addAlbum(item: FavoriteAlbumEntity)
    @Upsert
    suspend fun addArtist(item: FavoriteArtistEntity)
    @Query("DELETE FROM favorite_tracks WHERE mediaId = :id")
    suspend fun removeTrack(id: Long)
    @Query("DELETE FROM favorite_albums WHERE mediaId = :id")
    suspend fun removeAlbum(id: Long)
    @Query("DELETE FROM favorite_artists WHERE mediaId = :id")
    suspend fun removeArtist(id: Long)

}

@Database(
    entities = [FavoriteTrackEntity::class, FavoriteAlbumEntity::class, FavoriteArtistEntity::class],
    version = 1
)
abstract class FavoritesDatabase : RoomDatabase() {
    abstract val dao: FavoritesDao
}
