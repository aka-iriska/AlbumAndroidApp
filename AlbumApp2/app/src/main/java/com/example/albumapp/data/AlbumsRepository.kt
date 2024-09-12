package com.example.albumapp.data

import com.example.albumapp.ui.screens.createNewAlbum.Album
import com.example.albumapp.ui.screens.currentAlbum.AlbumDetailed
import kotlinx.coroutines.flow.Flow

data class InsertResult(val id: Int)
/**
 * Repository that provides insert, update, delete, and retrieve of [Item] from a given data source.
 */
interface AlbumsRepository {
    /**
     * Retrieve all the albums from the the given data source.
     */
    fun getAllAlbumsStream(): Flow<List<Album>>
    /**
     * Retrieve all the albums details from the the given data source.
     */
    fun getAllDetailedAlbumsStream(): Flow<List<AlbumDetailed>>
    /**
     * Retrieve an item from the given data source that matches with the [id].
     */
    fun getAlbumStream(id: Int): Flow<Album?>

    /**
     * Retrieve album details for a given album ID.
     */
    fun getAlbumDetailsStream(albumId: Int): Flow<AlbumDetailed?>
    /**
     * Get the title of album for currentAlbumScreen via foreignkey albumId in albumDetailed and id in albumsTable
     */
    suspend fun getAlbumTitleForDetailed(albumId:Int): String

    fun getAlbumDetailsStreamViaForeignKey(albumId: Int): Flow<AlbumDetailed?>

    /**
     * Insert album in the data source
     */
    suspend fun insertAlbum(album: Album): Long

    /**
     * Insert album details in the data source
     */
    suspend fun insertAlbumDetails(albumDetails: AlbumDetailed): Long

    /**
     * Delete item from the data source
     */
    suspend fun deleteAlbum(album: Album)

    /**
     * Delete album details from the data source
     */
    suspend fun deleteAlbumDetails(albumDetails: AlbumDetailed)

    /**
     * Update item in the data source
     */
    suspend fun updateAlbum(album: Album)

    /**
     * Update album details in the data source
     */
    suspend fun updateAlbumDetails(albumDetails: AlbumDetailed)

    /**
     * Delete all albums
     */
    suspend fun deleteAllAlbums()
    /**
     * Delete all albums details.
     */
    suspend fun deleteAllAlbumsDetailed()
}