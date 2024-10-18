package com.example.albumapp.ui.screens.currentAlbum

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.albumapp.R
import com.example.albumapp.data.AlbumsRepository
import com.example.albumapp.ui.screens.createNewAlbum.Album
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import java.util.UUID

class CurrentAlbumViewModel(
    savedStateHandle: SavedStateHandle, private val albumsRepository: AlbumsRepository
) : ViewModel() {
    var pagesUiState by mutableStateOf(CurrentAlbumUiState())
        private set
    private val albumId: Int = checkNotNull(savedStateHandle[CurrentAlbumDestination.AlbumIdArg])

    init {
        viewModelScope.launch {
            albumsRepository
                .getAlbumDetailsStreamViaForeignKey(albumId)
                .filterNotNull()
                .collect { albumDetails ->
                    pagesUiState = albumDetailedListToUiState(albumDetails, isEditing = false)
                    pagesUiState =
                        pagesUiState.copy(pageNumber = pagesUiState.pagesMap.keys.maxOrNull() ?: 0)
                }

        }
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

    fun updateCurrentPage(newCurrentPage: Int) {
        pagesUiState = pagesUiState.copy(currentPage = newCurrentPage)
    }

    suspend fun findTitle(albumId: Int): String {
        return albumsRepository.getAlbumTitleForDetailed(albumId)
    }
}

@Entity(
    tableName = "albumDetailsTable", foreignKeys = [ForeignKey(
        entity = Album::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("albumId"),
        onDelete = ForeignKey.CASCADE
    )]
)
data class AlbumDetailed(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val albumId: Int,
    val type: String,
    val offsetX: Float,
    val offsetY: Float,
    val scale: Float,
    val rotation: Float,
    val resource: String, // путь для стикеров и фото, текст для текстовых полей
    val zIndex: Int,          // Z-индекс для управления наложением элементов
    val pageNumber: Int
)

data class CurrentAlbumUiState(
    val albumId: Int = 0,
    val currentPage: Int = 0,
    val pagesMap: Map<Int, List<PageElement>> = emptyMap(),
    val isEditing: Boolean = false,
    val changed: Boolean = false,
    val pageNumber: Int = 0,
    val pagesToShow: Int = 1,
)

fun CurrentAlbumUiState.toAlbumDetailedDbClass(
    pageNumber: Int,
    element: PageElement
): AlbumDetailed = AlbumDetailed(
    id = element.id,
    albumId = this.albumId,
    type = element.type.toString(),
    offsetX = element.offsetX,
    offsetY = element.offsetY,
    scale = element.scale,
    rotation = element.rotation,
    resource = element.resource,
    zIndex = element.zIndex,
    pageNumber = pageNumber
)

data class PageElement(
    val id: Int = UUID.randomUUID().hashCode(), // Генерация уникального ID
    val type: ElementType = ElementType.DEFAULT,
    val offsetX: Float = 0f,
    val offsetY: Float = 0f,
    val scale: Float = 1f,
    val rotation: Float = 0f,
    val resource: String = "",
    val zIndex: Int = 0,          // Z-индекс для управления наложением элементов
    val originalWidth: Float = 0f,
    val originalHeight: Float = 0f
)

enum class ElementType {
    STICKER, IMAGE, TEXT_FIELD, DEFAULT
}


fun albumDetailedListToUiState(
    data: List<AlbumDetailed>, isEditing: Boolean = false
): CurrentAlbumUiState {
    val pagesMap: Map<Int, List<PageElement>> =
        data.groupBy { it.pageNumber } // Группировка по номеру страницы
            .mapValues { entry -> // Преобразование групп в PageElement
                entry.value.map { albumDetailed ->
                    PageElement(
                        id = albumDetailed.id,
                        type = stringToElementType(albumDetailed.type),
                        offsetX = albumDetailed.offsetX,
                        offsetY = albumDetailed.offsetY,
                        scale = albumDetailed.scale,
                        rotation = albumDetailed.rotation,
                        resource = albumDetailed.resource,
                        zIndex = albumDetailed.zIndex
                    )
                }
            }
    // Создание CreateNewPagesUiState с pagesMap
    return CurrentAlbumUiState(
        albumId = data.firstOrNull()?.albumId
            ?: 0, // Предполагаем, что albumId одинаковый для всех элементов
        pagesMap = pagesMap,
        isEditing = isEditing
    )
}

fun stringToElementType(type: String): ElementType {
    return try {
        ElementType.valueOf(type.uppercase()) // Преобразуем строку в верхний регистр для сопоставления
    } catch (e: IllegalArgumentException) {
        // Обработка ошибки, если строка не является допустимым значением перечисления
        // Можно вернуть значение по умолчанию или выбросить собственное исключение
        ElementType.STICKER // или любое другое значение по умолчанию
    }
}

val stickersMap = mapOf(
    "heart" to R.raw.heart,
    "star" to R.raw.star,
    "scotch" to R.raw.scotch,
    "sea_plant" to R.raw.sea_plant,
    "instax_square" to R.raw.instax_square
)