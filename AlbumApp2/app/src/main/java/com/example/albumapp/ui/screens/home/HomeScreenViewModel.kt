package com.example.albumapp.ui.screens.home

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.toUpperCase
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.albumapp.data.AlbumsRepository
import com.example.albumapp.ui.screens.createNewAlbum.Album
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel to retrieve all items in the Room database.
 */
class HomeViewModel(albumsRepository: AlbumsRepository) : ViewModel() {
    var homeUiState by mutableStateOf(HomeUiState())
        private set

    init {
        viewModelScope.launch {
            homeUiState = albumsRepository.getAllAlbumsStream()
                .map { sth ->
                    HomeUiState(sth)
                }
                .filterNotNull()
                .first()

        }
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

    fun sortAlbumsListByChoice(sortOption: SortOptions) {
        var mutableAlbumList = homeUiState.albumList.toMutableList()
        homeUiState = when (sortOption){
            SortOptions.CREATE -> HomeUiState(albumList = homeUiState.albumList.sortedBy { it.dateOfCreation})
            SortOptions.BEGIN_DATE -> HomeUiState(albumList = homeUiState.albumList.sortedBy { it.dateOfActivity})
            SortOptions.END_DATE -> HomeUiState(albumList = homeUiState.albumList.sortedBy { it.endDateOfActivity})
            SortOptions.TITLE -> HomeUiState(albumList = homeUiState.albumList.sortedBy { it.title})
        }

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

