package com.example.albumapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.albumapp.ui.screens.createNewAlbum.Album
import com.example.albumapp.ui.screens.currentAlbum.AlbumDetailed

@Database(entities = [Album::class, AlbumDetailed::class], version = 7, exportSchema = false)
abstract class AlbumsDataBase : RoomDatabase() {
    abstract fun albumsDao(): AlbumsDAO

    // allows access to the methods to create or get the database and uses the class name as the qualifier.
    companion object {
        // helps maintain a single instance of the database opened at a given time
        @Volatile
        private var Instance: AlbumsDataBase? = null

        // with a Context parameter that the database builder needs.
        //
        fun getDatabase(context: Context): AlbumsDataBase {
            /**
             * [synchronized] only one thread of execution at a time can enter this block of code,
             * which makes sure the database only gets initialized once.
             */
            return Instance ?: synchronized(this) {
                Room
                    .databaseBuilder(context, AlbumsDataBase::class.java, "albums_database")
                    .fallbackToDestructiveMigration()
                    .build()
                    // keep a reference to the recently created db instance.
                    .also { Instance = it }
            }
        }
    }
}