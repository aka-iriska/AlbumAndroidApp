package com.example.albumapp.ui.screens.currentAlbum

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.albumapp.data.AlbumsRepository
import com.example.albumapp.ui.screens.createNewAlbum.Album
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.util.UUID

class CurrentAlbumViewModel(
    savedStateHandle: SavedStateHandle, private val albumsRepository: AlbumsRepository
) : ViewModel() {


    private val albumId: Int = checkNotNull(savedStateHandle[CurrentAlbumDestination.AlbumIdArg])
    val uiState: StateFlow<CurrentAlbumUiState> =
        albumsRepository
            .getAlbumDetailsStreamViaForeignKey(albumId)
            .filterNotNull()
            .map { sth ->
                //Log.d("data", sth.toString())
                albumDetailedListToUiState(sth)
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
const val BASE_SIZE = 1024f

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
    val originalWidth: Float,
    val originalHeight: Float,
    val scale: Float,
    val rotation: Float,
    val resourceId: Int, // для стикеров и изображений
    val text: String, // для текстовых полей
    val zIndex: Int,          // Z-индекс для управления наложением элементов
    val pageNumber: Int
)

data class CurrentAlbumUiState(
    val albumId: Int = 0,
    val currentPage: Int = 1,
    val pagesMap: Map<Int, List<PageElement>> = emptyMap(),
    val isEditing: Boolean = false,
    val changed:Boolean = false

)
fun CurrentAlbumUiState.toAlbumDetailedDbClass(pageNumber: Int, element: PageElement): AlbumDetailed = AlbumDetailed(
    id = element.id,
    albumId = this.albumId,
    type = element.type.toString(),
    offsetX = element.offsetX,
    offsetY = element.offsetY,
    scale = element.scale,
    rotation = element.rotation,
    resourceId = element.resourceId, // для стикеров и изображений
    text = element.text, // для текстовых полей
    zIndex = element.zIndex,
    originalWidth = element.originalWidth,
    originalHeight = element.originalHeight,
    pageNumber = pageNumber
)

data class PageElement(
    val id: Int = UUID.randomUUID().hashCode(), // Генерация уникального ID
    val type: ElementType = ElementType.DEFAULT,
    val offsetX: Float = 0f,
    val offsetY: Float = 0f,
    val scale: Float = 1f,
    val rotation: Float = 0f,
    val resourceId: Int = 0, // для стикеров и изображений
    val text: String = "", // для текстовых полей
    val zIndex: Int = 0,          // Z-индекс для управления наложением элементов
    val originalWidth: Float = 0f,
    val originalHeight: Float= 0f
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
                        resourceId = albumDetailed.resourceId,
                        text = albumDetailed.text,
                        originalHeight = albumDetailed.originalHeight,
                        originalWidth = albumDetailed.originalHeight,
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