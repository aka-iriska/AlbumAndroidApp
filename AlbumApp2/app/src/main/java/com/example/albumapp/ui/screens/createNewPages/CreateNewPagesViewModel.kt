package com.example.albumapp.ui.screens.createNewPages

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.albumapp.data.AlbumsRepository
import com.example.albumapp.ui.screens.currentAlbum.AlbumUiState
import com.example.albumapp.ui.screens.currentAlbum.toAlbumUiState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class CreateNewPagesViewModel(
    savedStateHandle: SavedStateHandle,
    private val albumsRepository: AlbumsRepository
) : ViewModel() {


    private val albumId: Int = checkNotNull(savedStateHandle[CreateNewPagesDestination.AlbumIdArg])
    val uiState: StateFlow<CreateNewPagesUiState> =
        albumsRepository.getAlbumDetailsStreamViaForeignKey(albumId)
            .filterNotNull()
            .map {
                CreateNewPagesUiState(albumDetails = it.toAlbumUiState())
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = CreateNewPagesUiState()
            )

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}
data class CreateNewPagesUiState(
    //val outOfStock: Boolean = true,
    val albumDetails: AlbumUiState = AlbumUiState()
)