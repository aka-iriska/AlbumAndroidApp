package com.example.albumapp.ui.screens.createNewPages

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.albumapp.data.AlbumsRepository
import com.example.albumapp.ui.screens.currentAlbum.CurrentAlbumUiState
import com.example.albumapp.ui.screens.currentAlbum.PageElement
import com.example.albumapp.ui.screens.currentAlbum.albumDetailedListToUiState
import com.example.albumapp.ui.screens.currentAlbum.toAlbumDetailedDbClass
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch

class CreateNewPagesViewModel(
    savedStateHandle: SavedStateHandle, private val albumsRepository: AlbumsRepository
) : ViewModel() {
    var pagesUiState by mutableStateOf(CurrentAlbumUiState())
        private set
    private val albumId: Int = checkNotNull(savedStateHandle[CreateNewPagesDestination.AlbumIdArg])
    private var elementIdCounter = 1

    init {
        viewModelScope.launch {
            albumsRepository
                .getAlbumDetailsStreamViaForeignKey(albumId)
                .filterNotNull()
                .collect { albumDetails ->
                    pagesUiState = albumDetailedListToUiState(albumDetails, isEditing = true)
                    elementIdCounter += pagesUiState.pagesMap.values.flatten().maxOfOrNull { it.id }!!
                }
        }
    }


    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

    fun updateUiState(pageNumber: Int, element: PageElement, elementId: Int = -1) {
        /*todo разобраться с pageNumber */
        var newPagesMap = pagesUiState.pagesMap.toMutableMap()
        // Получаем список элементов для страницы или создаем новый пустой список, если его нет
        val newPageElementsList = newPagesMap[pageNumber]?.toMutableList() ?: mutableListOf()
        if (elementId == -1) {
            val uniqueElement = element.copy(id = elementIdCounter++)
            // Добавляем новый элемент на страницу
            newPageElementsList.add(uniqueElement)
            Log.d("new sticker", "add $uniqueElement \n $elementIdCounter")
        } else {
            newPageElementsList.forEachIndexed { index, pageElement ->
                if (pageElement.id == elementId) {
                    newPageElementsList[index] = element
                    Log.d("change sticker", "ch $element\n id to change: $elementId")
                    return@forEachIndexed
                }
            }
        }
        // Обновляем карту с новым списком элементов для страницы
        newPagesMap[pageNumber] = newPageElementsList

        // Обновляем состояние pagesUiState
        pagesUiState = pagesUiState.copy(pagesMap = newPagesMap, changed = true)
        /////////// для логов
        pagesUiState.pagesMap.mapValues { content ->
            content.value.forEach { sticker ->
                Log.d(
                    "every sticker",
                    "sticker id: ${sticker.id}\n resource: ${sticker.resourceId}\n elementId: ${elementId}"
                )
            }
        }

    }

    suspend fun savePagesForAlbum() {
        /*CurrentAlbumUiState(
        albumId=1,
        currentPage=1,
        pagesMap={
        0=[PageElement(id=1, type=STICKER, offsetX=0.0, offsetY=0.0, scale=0.0, rotation=0.0, resourceId=0, text=, zIndex=0)]
        },
        isEditing=true)
        */
        pagesUiState.pagesMap.map { pageContent ->
            val pageNumber = pageContent.key
            pageContent.value.map { pageElement ->
                val albumDetailedDb = pagesUiState.toAlbumDetailedDbClass(pageNumber, pageElement)
                albumsRepository.insertAlbumDetails(albumDetailedDb)
            }
        }
    }

}





