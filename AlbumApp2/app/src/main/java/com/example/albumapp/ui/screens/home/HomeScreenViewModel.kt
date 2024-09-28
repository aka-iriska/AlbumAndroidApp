package com.example.albumapp.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.albumapp.data.AlbumsRepository
import com.example.albumapp.ui.screens.createNewAlbum.Album
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

/**
 * ViewModel to retrieve all items in the Room database.
 */
class HomeViewModel(albumsRepository: AlbumsRepository) : ViewModel() {
    private val _sortOption = MutableStateFlow(SortOptions.CREATE)

    var homeUiState: StateFlow<HomeUiState> =
        combine(
            albumsRepository.getAllAlbumsStream(),
            _sortOption
        ) { albums, sortOption ->
            val sortedAlbums = when (sortOption) {
                SortOptions.CREATE -> albums.sortedBy { it.dateOfCreation }
                SortOptions.BEGIN_DATE -> albums.sortedBy { it.dateOfActivity }
                SortOptions.END_DATE -> albums.sortedBy { it.endDateOfActivity }
                SortOptions.TITLE -> albums.sortedBy { it.title }
            }
            HomeUiState(sortedAlbums)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = HomeUiState()
        )

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

    fun sortAlbumsListByChoice(sortOption: SortOptions) {
        _sortOption.value = sortOption
    }
}


/**
 * Ui State for HomeScreen
 * Use the [stateIn] operator to convert the Flow into a StateFlow.
 * The StateFlow is the observable API for UI state, which enables the UI to update itself.
 */
data class HomeUiState(
    val albumList: List<Album> = listOf()
)

