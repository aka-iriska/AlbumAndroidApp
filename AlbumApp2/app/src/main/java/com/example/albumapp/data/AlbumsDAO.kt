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
    suspend fun insert(album: Album):Long
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

    @Query("SELECT * from albumsTable ORDER BY title ASC")
    fun getAllAlbums(): Flow<List<Album>>
    @Query("SELECT * from albumDetailsTable ORDER BY id ASC")
    fun getAllAlbumsDetailed(): Flow<List<AlbumDetailed>>

    @Query("DELETE FROM albumsTable")
    suspend fun deleteAllAlbums()
    @Query("DELETE FROM albumDetailsTable")
    suspend fun deleteAllAlbumDetails()

    @Query("SELECT a.title FROM albumsTable a INNER JOIN albumDetailsTable ad ON a.id = ad.albumId WHERE albumId = :albumId")
    fun getAlbumTitleForDetailed(albumId:Int):Flow<String>

    @Query("SELECT ad.id, ad.albumId, ad.countPages from albumDetailsTable ad INNER JOIN albumsTable a ON a.id = ad.albumId WHERE albumId = :albumId ")
    fun getAlbumDetailsStreamViaForeignKey(albumId: Int): Flow<AlbumDetailed?>
}
/**
 *todo
 * [ksp]
 * D:/apps/AlbumApp2/app/src/main/java/com/example/albumapp/data/AlbumsDAO.kt:49:
 * The query returns some columns [title, artist, description, imageCover, dateOfCreation, dateOfActivity, endDateOfActivity]
 * which are not used by com.example.albumapp.ui.screens.currentAlbum.AlbumDetailed.
 * You can use @ColumnInfo annotation on the fields to specify the mapping.
 * You can annotate the method with @RewriteQueriesToDropUnusedColumns to direct Room to rewrite your query to avoid fetching unused columns.
 * You can suppress this warning by annotating the method with @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH).
 * Columns returned by the query: id, albumId, countPages, id, title, artist, description, imageCover, dateOfCreation, dateOfActivity, endDateOfActivity.
 * TODO
 * [ksp]
 * D:/apps/AlbumApp2/app/src/main/java/com/example/albumapp/ui/screens/currentAlbum/CurrentAlbumViewModel.kt:59
 * : albumId column references a foreign key but it is not part of an index.
 * This may trigger full table scans whenever parent table is modified so you are highly advised to create an index that covers this column.
 * */