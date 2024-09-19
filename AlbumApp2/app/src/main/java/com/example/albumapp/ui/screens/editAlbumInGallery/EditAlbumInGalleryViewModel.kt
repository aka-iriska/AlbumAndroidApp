package com.example.albumapp.ui.screens.editAlbumInGallery

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.albumapp.data.AlbumsRepository
import com.example.albumapp.ui.screens.createNewAlbum.AlbumsUiState
import com.example.albumapp.ui.screens.createNewAlbum.ImageSavingException
import com.example.albumapp.ui.screens.createNewAlbum.toAlbumDbClass
import com.example.albumapp.ui.screens.createNewAlbum.toAlbumsUiState
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException

class EditAlbumInGalleryViewModel(
    savedStateHandle: SavedStateHandle,
    private val albumsRepository: AlbumsRepository
) : ViewModel() {
    var albumsUiState by mutableStateOf(AlbumsUiState())
        private set
    private val albumId: Int =
        checkNotNull(savedStateHandle[EditAlbumInGalleryDestination.AlbumIdArg])
    init {
        viewModelScope.launch {
            albumsUiState = albumsRepository.getAlbumStream(albumId)
                .filterNotNull()
                .first()
                .toAlbumsUiState()
            updateUiState(albumsUiState.copy(isEntryValid = true))
            //Log.d("Tag", "${albumsRepository.getAlbumStream(albumId) .filterNotNull().first().toAlbumsUiState()}")
        }
    }




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

    fun saveAlbumCoverLocally(context: Context, albumId: Int): Result<Uri> {

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

    suspend fun updateAlbum(context: Context) {
        if (validateInput()) {
            /*if (albumsUiState.imageCover.isNotEmpty()) {
                /**
                 * Making the link for image with the real id of album
                 */
                saveAlbumCoverLocally(context, albumsUiState.id).onSuccess { permanentUri ->
                    albumsRepository.updateAlbum(albumsUiState.toAlbumDbClass())
                }.onFailure { exception ->
                    // Обработка ошибки сохранения изображения
                    Log.e("Error", "Failed to save image: ${exception.message}")
                }
            }
            else*/ albumsRepository.updateAlbum(albumsUiState.toAlbumDbClass())
        }
    }
}