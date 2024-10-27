package com.example.albumapp.data

import com.example.albumapp.ui.screens.createNewAlbum.Album
import com.example.albumapp.ui.screens.currentAlbum.AlbumDetailed
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class OfflineAlbumsRepository(private val albumDao: AlbumsDAO) : AlbumsRepository {
    override fun getAllAlbumsStream(): Flow<List<Album>> = albumDao.getAllAlbums()
    override fun getAllDetailedAlbumsStream(): Flow<List<AlbumDetailed>> =
        albumDao.getAllAlbumsDetailed()

    override fun getAlbumStream(id: Int): Flow<Album?> = albumDao.getAlbum(id)
    override fun getAlbumDetailsStream(albumId: Int): Flow<AlbumDetailed?> =
        albumDao.getAlbumDetails(albumId)

    override suspend fun insertAlbum(album: Album): Long = albumDao.insert(album)
    override suspend fun insertAlbumDetails(albumDetails: AlbumDetailed): Long =
        albumDao.insertAlbumDetails(albumDetails)

    override suspend fun deleteAlbum(album: Album) = albumDao.deleteAlbum(album)
    override suspend fun deleteAlbumDetails(albumDetailsId: Int) =
        albumDao.deleteAlbumDetails(albumDetailsId)

    override suspend fun updateAlbum(album: Album) = albumDao.update(album)
    override suspend fun updateAlbumDetails(albumDetails: AlbumDetailed) =
        albumDao.updateAlbumDetails(albumDetails)

    override suspend fun deleteAllAlbums() = albumDao.deleteAllAlbums()
    override suspend fun deleteAllAlbumsDetailed() = albumDao.deleteAllAlbumDetails()

    override suspend fun getAlbumTitleForDetailed(albumId: Int): String {
        return albumDao.getAlbumTitleForDetailed(albumId).firstOrNull() ?: ""
    }

    override fun getAlbumDetailsStreamViaForeignKey(albumId: Int): Flow<List<AlbumDetailed>> =
        albumDao.getAlbumDetailsStreamViaForeignKey(albumId)
}