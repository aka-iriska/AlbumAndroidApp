package com.example.albumapp.ui.screens.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.albumapp.ui.screens.createNewAlbum.Album
import com.example.albumapp.data.AlbumsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/**
 * ViewModel to retrieve all items in the Room database.
 */
class HomeViewModel(albumsRepository: AlbumsRepository) : ViewModel() {
    val homeUiState: StateFlow<HomeUiState> =
        albumsRepository
            .getAllAlbumsStream()
            .map{ sth ->
                Log.d("mapFlow", sth.toString())
                HomeUiState(sth)
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = HomeUiState()
            )
    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}

/**
 * Ui State for HomeScreen
 * Use the [stateIn] operator to convert the Flow into a StateFlow.
 * The StateFlow is the observable API for UI state, which enables the UI to update itself.
 */
data class HomeUiState(val albumList: List<Album> = listOf())