package com.example.albumapp.data

import android.app.Application
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.albumapp.AppApplication
import com.example.albumapp.ui.screens.editAlbumInGallery.EditAlbumInGalleryViewModel
import com.example.albumapp.ui.screens.createNewAlbum.AlbumsViewModel
import com.example.albumapp.ui.screens.createNewPages.CreateNewPagesViewModel
import com.example.albumapp.ui.screens.currentAlbum.CurrentAlbumViewModel
import com.example.albumapp.ui.screens.home.HomeViewModel

object AppViewModelProvider {
    val Factory = viewModelFactory {
        // Other Initializers
        // Initializer for create new album in gallery
        initializer {
            AlbumsViewModel(inventoryApplication().container.albumsRepository)
        }
        // Initializer for home screen
        initializer {
            HomeViewModel(inventoryApplication().container.albumsRepository)
        }
        initializer {
            EditAlbumInGalleryViewModel(this.createSavedStateHandle(),inventoryApplication().container.albumsRepository)
        }
        initializer {
            CurrentAlbumViewModel(this.createSavedStateHandle(),inventoryApplication().container.albumsRepository)
        }
        initializer {
            CreateNewPagesViewModel(this.createSavedStateHandle(), inventoryApplication().container.albumsRepository)
        }
    }
}
/**
 * Extension function to queries for [Application] object and returns an instance of
 * [InventoryApplication].
 */
fun CreationExtras.inventoryApplication(): AppApplication =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as AppApplication)