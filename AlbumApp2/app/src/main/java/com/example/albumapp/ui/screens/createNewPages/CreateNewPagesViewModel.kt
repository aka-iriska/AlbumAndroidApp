package com.example.albumapp.ui.screens.createNewPages

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.IntSize
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.albumapp.data.AlbumsRepository
import com.example.albumapp.ui.screens.currentAlbum.CurrentAlbumUiState
import com.example.albumapp.ui.screens.currentAlbum.ElementType
import com.example.albumapp.ui.screens.currentAlbum.PageElement
import com.example.albumapp.ui.screens.currentAlbum.albumDetailedListToUiState
import com.example.albumapp.ui.screens.currentAlbum.toAlbumDetailedDbClass
import com.example.albumapp.utils.saveImagePathLocally
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import okhttp3.internal.toImmutableList

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
                    pagesUiState =
                        pagesUiState.copy(pageNumber = pagesUiState.pagesMap.keys.maxOrNull() ?: 0)
                    if (pagesUiState.pagesMap.isNotEmpty())
                        elementIdCounter += pagesUiState.pagesMap.values.flatten()
                            .maxOf { it.id }
                }
        }
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

    fun updatePagesToShow(pages: Int) {
        pagesUiState = pagesUiState.copy(pagesToShow = pages)
    }

    fun addNewPage() {
        pagesUiState = pagesUiState.copy(pageNumber = pagesUiState.pageNumber + 1)
    }

    fun updateCurrentPage(newCurrentPage: Int) {
        pagesUiState = pagesUiState.copy(currentPage = newCurrentPage)
    }

    fun updateUiState(pageNumber: Int, element: PageElement, elementId: Int = -1) {
        Log.d("before update", "${pagesUiState.pagesMap}")
        if (pageNumber != 0) {
            var newPagesMap = pagesUiState.pagesMap.toMutableMap()
            // Получаем список элементов для страницы или создаем новый пустой список, если его нет
            val newPageElementsList = newPagesMap[pageNumber]?.toMutableList() ?: mutableListOf()
            if (elementId == -1) {
                val uniqueElement = element.copy(id = elementIdCounter++)
                // Добавляем новый элемент на страницу
                newPageElementsList.add(uniqueElement)
            } else {
                newPageElementsList.forEachIndexed { index, pageElement ->
                    if (pageElement.id == elementId) {
                        newPageElementsList[index] = element
                        return@forEachIndexed
                    }
                }
            }
            // Обновляем карту с новым списком элементов для страницы
            newPagesMap[pageNumber] = newPageElementsList

            // Обновляем состояние pagesUiState
            pagesUiState = pagesUiState.copy(pagesMap = newPagesMap, changed = true)
        }
    }

    fun deleteElement(pageNumber: Int, elementId: Int) {
        // Получаем текущий список элементов на странице с номером pageNumber
        val currentPageElements = pagesUiState.pagesMap[pageNumber]?.toMutableList()

        // Если страница найдена и содержит элементы
        currentPageElements?.let {
            // Удаляем элемент с указанным elementId
            val updatedElements = it.filterNot { element -> element.id == elementId }

            // Обновляем карту страниц с измененным списком элементов
            pagesUiState = pagesUiState.copy(
                pagesMap = pagesUiState.pagesMap.toMutableMap().apply {
                    this[pageNumber] = updatedElements
                }
            )
        }
    }

    fun cancelDeleteElement(
        pageNumber: Int,
        elementId: Int,
        pageWidth: Int,
        pageHeight: Int,
        stickerSize: IntSize
    ) {
        val currentPageElements = pagesUiState.pagesMap[pageNumber]?.toMutableList()

        // Если страница найдена и содержит элементы
        currentPageElements?.forEachIndexed { index, pageElement ->
            if (pageElement.id == elementId) {
                val newPositionX = pageElement.offsetX.coerceAtLeast(0f)
                    .coerceAtMost((pageWidth - stickerSize.width) / pageWidth.toFloat())
                val newPositionY = pageElement.offsetY.coerceAtLeast(0f)
                    .coerceAtMost((pageHeight - stickerSize.height) / pageHeight.toFloat())
                currentPageElements[index] =
                    pageElement.copy(offsetY = newPositionY, offsetX = newPositionX)
                return@forEachIndexed
            }
        }
        currentPageElements?.let {
            val updatesPageElements: List<PageElement> = currentPageElements.toImmutableList()
            pagesUiState = pagesUiState.copy(
                pagesMap = pagesUiState.pagesMap.toMutableMap().apply {
                    this[pageNumber] = updatesPageElements
                })
        }

    }

    suspend fun savePagesForAlbum(context: Context) {
        pagesUiState.pagesMap.map { pageContent ->
            val pageNumber = pageContent.key
            pageContent.value.map { pageElement ->
                val albumDetailedDb = pagesUiState.toAlbumDetailedDbClass(pageNumber, pageElement)
                val insertedId = albumsRepository.insertAlbumDetails(albumDetailedDb)
                /**
                 * if we save image we need to reload real path
                 */
                if (pageElement.type == ElementType.IMAGE && pageElement.resource.startsWith("content://")) {
                    saveImagePathLocally(
                        pageElement.resource,
                        context,
                        insertedId.toInt(),
                        "image_element"
                    ).onSuccess { permanentUri ->
                        val updatedPageElement =
                            albumDetailedDb.copy(
                                id = insertedId.toInt(),
                                resource = permanentUri.toString()
                            )
                        /**
                         * Updating already saved album with actual link for image
                         */

                        albumsRepository.updateAlbumDetails(updatedPageElement)
                    }.onFailure { exception ->
                        // Обработка ошибки сохранения изображения
                        Log.e("Error", "Failed to save image: ${exception.message}")
                    }
                }
            }
        }

    }
}





