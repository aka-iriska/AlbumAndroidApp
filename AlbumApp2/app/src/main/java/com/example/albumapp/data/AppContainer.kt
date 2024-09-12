package com.example.albumapp.data

import android.content.Context

/**
 * App container for Dependency injection.
 */
interface AppContainer {
    val albumsRepository: AlbumsRepository
}

/**
 * [AppContainer] implementation that provides instance of [OfflineItemsRepository]
 */
class AppDataContainer(private val context: Context) : AppContainer {
    /**
     * Implementation for [ItemsRepository]
     */
    override val albumsRepository: AlbumsRepository by lazy {
        OfflineAlbumsRepository(AlbumsDataBase.getDatabase(context).albumsDao())
    }
}