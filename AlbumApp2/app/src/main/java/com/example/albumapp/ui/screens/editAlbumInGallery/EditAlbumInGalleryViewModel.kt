package com.example.albumapp.ui.screens.editAlbumInGallery

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.albumapp.data.AlbumsRepository
import com.example.albumapp.ui.screens.createNewAlbum.AlbumsUiState
import com.example.albumapp.ui.screens.createNewAlbum.toAlbumDbClass
import com.example.albumapp.ui.screens.createNewAlbum.toAlbumsUiState
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class EditAlbumInGalleryViewModel(
    savedStateHandle: SavedStateHandle,
    private val albumsRepository: AlbumsRepository
) : ViewModel() {
    var albumsUiState by mutableStateOf(AlbumsUiState())
        private set
    private val albumId: Int =
        checkNotNull(savedStateHandle[EditAlbumInGalleryDestination.ALBUM_ID_ARG])

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
            title.isNotBlank()
        }
    }
    /*todo что с фото*/
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