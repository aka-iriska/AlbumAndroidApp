package com.example.albumapp.ui.screens.createNewAlbum

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.albumapp.data.AlbumsRepository
import com.example.albumapp.ui.screens.currentAlbum.AlbumDetailed
import kotlinx.coroutines.flow.first
import java.io.File
import java.io.IOException
import java.sql.Timestamp

class AlbumsViewModel(private val albumsRepository: AlbumsRepository) : ViewModel() {
    /**
     * Holds current item ui state
     */
    var albumsUiState by mutableStateOf(AlbumsUiState())
        private set

    /**
     * Updates the [itemUiState] with the value provided in the argument. This method also triggers
     * a validation for input values.
     */
    fun updateUiState(albumDetails: AlbumsUiState) {
        albumsUiState = AlbumsUiState(
            id = albumDetails.id,
            title = albumDetails.title,
            artist = albumDetails.artist,
            description = albumDetails.description,
            imageCover = albumDetails.imageCover,
            dateOfCreation = albumDetails.dateOfCreation,
            dateOfActivity = albumDetails.dateOfActivity,
            endDateOfActivity = albumDetails.endDateOfActivity,
            isEntryValid = validateInput(albumDetails)
        )
    }

    private fun validateInput(uiState: AlbumsUiState = albumsUiState): Boolean {
        return with(uiState) {
            title.isNotBlank() //&& price.isNotBlank() && quantity.isNotBlank()
        }
    }

    private fun saveAlbumCoverLocally(context: Context, albumId: Int): Result<Uri> {

        val uri: Uri
        return try {
            uri = if (albumsUiState.imageCover.isNotEmpty()) {
                Uri.parse(albumsUiState.imageCover)
            } else {
                throw IllegalArgumentException("Image cover URI is empty") // Throw exception for empty URI
            }
            val contentResolver = context.contentResolver
            val inputStream = contentResolver.openInputStream(uri)
            val file = File(context.filesDir, "album_cover_$albumId.jpg")

            inputStream?.use { input ->
                file.outputStream().use { output ->
                    input.copyTo(output)
                }
            }

            Result.success(Uri.fromFile(file))
        } catch (e: IOException) {
            Result.failure(ImageSavingException("Failed to save image: ${e.message}"))
        }
    }

    private fun saveTimeOfSaving(uiState: AlbumsUiState = albumsUiState): Boolean {
        updateUiState(uiState.copy(dateOfCreation = Timestamp(System.currentTimeMillis()).toString()))
        return true
    }

    suspend fun saveItem(context: Context) {
        if (validateInput() && saveTimeOfSaving()) {
            //albumsRepository.deleteAllAlbums()
            /**
             * Saving the album with some id in table albumsTable
             */
            val newAlbum = albumsUiState.toAlbumDbClass()

            /**
             * Getting the updated and real id of current album following the db
             */
            val insertedId = albumsRepository.insertAlbum(newAlbum)
            if (albumsUiState.imageCover.isNotEmpty()) {
                /**
                 * Making the link for image with the real id of album
                 */
                saveAlbumCoverLocally(context, insertedId.toInt()).onSuccess { permanentUri ->
                    val updatedAlbum =
                        newAlbum.copy(id = insertedId.toInt(), imageCover = permanentUri.toString())

                    /**
                     * Updating already saved album with actual link for image
                     */

                    albumsRepository.updateAlbum(updatedAlbum)

                    /**
                     *  Saving the default value to the another table for future adding details
                     */

                    albumsRepository.insertAlbumDetails(
                        AlbumDetailed(
                            id = 0, albumId = insertedId.toInt(),
                            type = "DEFAULT",
                            offsetX = 0f,
                            offsetY = 0f,
                            scale = 0f,
                            rotation = 0f,
                            resourceId = 0,
                            text = "",
                            zIndex = 0,
                            pageNumber = 0
                        )
                    )
                }.onFailure { exception ->
                    // Обработка ошибки сохранения изображения
                    Log.e("Error", "Failed to save image: ${exception.message}")
                }
            }


        }


    }

    suspend fun deleteAlbum(id: Int) {
        var selectedAlbum: Album? = albumsRepository.getAlbumStream(id).first()
        if (selectedAlbum != null) {
            albumsRepository.deleteAlbum(selectedAlbum)
        }
    }
}

@Entity(tableName = "albumsTable")
data class Album(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    var artist: String,
    var description: String,
    var imageCover: String = "",
    //TEXT as ISO8601 strings ("YYYY-MM-DD HH:MM:SS.SSS")
    var dateOfCreation: String,
    var dateOfActivity: String,
    var endDateOfActivity: String
    //val pages: List<Page> = emptyList()
)

data class AlbumsUiState(
    val id: Int = 0,
    val title: String = "",
    val description: String = "",
    val artist: String = "",
    val imageCover: String = "",
    val dateOfActivity: String = "",
    var endDateOfActivity: String = "",
    val dateOfCreation: String = "1000-10-10 10:10:10.000",
    val isEntryValid: Boolean = false
)

fun AlbumsUiState.toAlbumDbClass(): Album = Album(
    id = id,
    title = title,
    artist = artist,
    description = description,
    imageCover = imageCover,
    dateOfCreation = dateOfCreation,
    dateOfActivity = dateOfActivity,
    endDateOfActivity = endDateOfActivity
)

fun Album.toAlbumsUiState(): AlbumsUiState = AlbumsUiState(
    id = id,
    title = title,
    artist = artist,
    description = description,
    imageCover = imageCover,
    dateOfCreation = dateOfCreation,
    dateOfActivity = dateOfActivity,
    endDateOfActivity = endDateOfActivity
)

// Custom exception class for image saving errors
class ImageSavingException(message: String) : Exception(message)