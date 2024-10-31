package com.example.albumapp.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.albumapp.ui.screens.createNewAlbum.Album
import com.example.albumapp.ui.screens.currentAlbum.AlbumDetailed
import kotlinx.coroutines.flow.Flow

@Dao
interface AlbumsDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(album: Album): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlbumDetails(albumDetails: AlbumDetailed): Long

    @Update
    suspend fun update(album: Album)

    @Update
    suspend fun updateAlbumDetails(albumDetails: AlbumDetailed)

    @Delete
    suspend fun deleteAlbum(album: Album)

    @Delete
    suspend fun deleteAlbumDetails(albumDetails: AlbumDetailed)

    @Query("SELECT * from albumsTable WHERE id = :id")
    fun getAlbum(id: Int): Flow<Album>

    @Query("SELECT * from albumDetailsTable WHERE id = :albumId")
    fun getAlbumDetails(albumId: Int): Flow<AlbumDetailed?>

    @Query("SELECT * from albumsTable ORDER BY dateOfCreation ASC")
    fun getAllAlbums(): Flow<List<Album>>

    @Query("SELECT * from albumDetailsTable ORDER BY id ASC")
    fun getAllAlbumsDetailed(): Flow<List<AlbumDetailed>>

    @Query("DELETE FROM albumsTable")
    suspend fun deleteAllAlbums()

    @Query("DELETE FROM albumDetailsTable")
    suspend fun deleteAllAlbumDetails()

    @Query("SELECT a.title FROM albumsTable a INNER JOIN albumDetailsTable ad ON a.id = ad.albumId WHERE albumId = :albumId")
    fun getAlbumTitleForDetailed(albumId: Int): Flow<String>

    @Query("SELECT ad.id, ad.albumId, ad.pageNumber, ad.type,ad.offsetX,ad.offsetY,ad.scale,ad.rotation,ad.resource,ad.zIndex from albumDetailsTable ad INNER JOIN albumsTable a ON a.id = ad.albumId WHERE albumId = :albumId ")
    fun getAlbumDetailsStreamViaForeignKey(albumId: Int): Flow<List<AlbumDetailed>>

    @Query("SELECT a.pageOrientation from albumsTable a where a.id = :albumId")
    fun getPageOrientationForAlbum(albumId: Int): Flow<Boolean>
}