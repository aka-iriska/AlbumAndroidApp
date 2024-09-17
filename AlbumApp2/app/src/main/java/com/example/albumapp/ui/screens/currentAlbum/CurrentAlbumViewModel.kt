package com.example.albumapp.ui.screens.currentAlbum

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.albumapp.data.AlbumsRepository
import com.example.albumapp.ui.screens.createNewAlbum.Album
import com.example.albumapp.ui.screens.createNewAlbum.AlbumsUiState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class CurrentAlbumViewModel(
    savedStateHandle: SavedStateHandle, private val albumsRepository: AlbumsRepository
) : ViewModel() {


    private val albumId: Int = checkNotNull(savedStateHandle[CurrentAlbumDestination.AlbumIdArg])
    val uiState: StateFlow<CurrentAlbumUiState> =
        albumsRepository.getAlbumDetailsStreamViaForeignKey(albumId).filterNotNull().map {
            CurrentAlbumUiState(albumDetails = it.toAlbumUiState())
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = CurrentAlbumUiState()
        )

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

    suspend fun findTitle(albumId: Int): String {
        return albumsRepository.getAlbumTitleForDetailed(albumId)
    }
}

data class CurrentAlbumUiState(
    //val outOfStock: Boolean = true,
    val albumDetails: AlbumUiState = AlbumUiState()
)

@Entity(
    tableName = "albumDetailsTable", foreignKeys = arrayOf(
        ForeignKey(
            entity = Album::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("albumId"),
            onDelete = ForeignKey.CASCADE
        )
    )
)
data class AlbumDetailed(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val albumId: Int,
    val elements: List<PageElement>,
    val pageNumber: Int
)

data class PageElement(
    val id: Int = (System.currentTimeMillis() % Int.MAX_VALUE).toInt(), // Генерация уникального ID
    val type: ElementType = ElementType.STICKER,
    val offsetX: Float = 0f,
    val offsetY: Float = 0f,
    val scale: Float = 1f,
    val rotation: Float = 0f,
    val resourceId: Int? = null, // для стикеров и изображений
    val text: String? = null // для текстовых полей
)

enum class ElementType {
    STICKER, IMAGE, TEXT_FIELD
}


data class AlbumUiState(
    val id: Int = 0,
    val albumId: Int = 0,
    val elements: List<PageElement> = emptyList(),
    val pageNumber: Int = 0,
    //val selectedElement: PageElement? = null,
    val isEditing: Boolean = false
)

fun AlbumUiState.toAlbumDetailed(): AlbumDetailed = AlbumDetailed(
    id = id, albumId = albumId, pageNumber = pageNumber, elements = elements
)

fun AlbumDetailed.toAlbumUiState(): AlbumUiState = AlbumUiState(
    id = id, albumId = albumId, pageNumber = pageNumber
)
