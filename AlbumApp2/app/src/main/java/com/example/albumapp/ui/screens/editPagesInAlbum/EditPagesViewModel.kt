package com.example.albumapp.ui.screens.editPagesInAlbum

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
import com.example.albumapp.ui.screens.currentAlbum.AlbumDetailed
import com.example.albumapp.ui.screens.currentAlbum.CurrentAlbumUiState
import com.example.albumapp.ui.screens.currentAlbum.ElementType
import com.example.albumapp.ui.screens.currentAlbum.PageElement
import com.example.albumapp.ui.screens.currentAlbum.albumDetailedListToUiState
import com.example.albumapp.ui.screens.currentAlbum.toAlbumDetailedDbClass
import com.example.albumapp.utils.saveImagePathLocally
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import okhttp3.internal.toImmutableList

class EditPagesViewModel(
    savedStateHandle: SavedStateHandle, private val albumsRepository: AlbumsRepository
) : ViewModel() {

    var pagesUiState by mutableStateOf(CurrentAlbumUiState())
        private set

    private val albumId: Int =
        checkNotNull(savedStateHandle[CreateNewPagesDestination.ALBUM_ID_ARG])

    private var elementIdCounter = -2

    private var deletedElements = mutableListOf<Int>()

    init {
        viewModelScope.launch {
            val flag = albumsRepository.getPageOrientationForAlbum(albumId)
            albumsRepository
                .getAlbumDetailsStreamViaForeignKey(albumId)
                .filterNotNull()
                .collect { albumDetails ->
                    pagesUiState = albumDetailedListToUiState(albumDetails, isEditing = true)
                    pagesUiState =
                        pagesUiState.copy(
                            pageNumber = pagesUiState.pagesMap.keys.maxOrNull() ?: 0,
                            pageOrientation = flag
                        )
                }
        }
    }

    /* companion object {
         private const val TIMEOUT_MILLIS = 5_000L
     }*/

    /*fun updatePagesToShow(pages: Int) {
        pagesUiState = pagesUiState.copy(pagesToShow = pages)
    }*/

    fun addNewPage() {
        //val newPagesNumber = pagesUiState.pageNumber + 1
        pagesUiState = pagesUiState.copy(pageNumber = pagesUiState.pageNumber + 1)
    }

    fun updatePageOrientation(orientation: Boolean = false, pageSize: IntSize) {

        val newPagesMap = pagesUiState.pagesMap.mapValues { (_, pageElements) ->
            pageElements.map { pageElement ->
                pageElement.copy(offsetX = pageElement.offsetY, offsetY = pageElement.offsetX)
            }
        }

        pagesUiState =
            pagesUiState.copy(
                pagesMap = newPagesMap,
                pageOrientation = !orientation,
                changed = true
            )
    }

    fun updateCurrentPage(newCurrentPage: Int) {
        pagesUiState = pagesUiState.copy(currentPage = newCurrentPage)
    }

    fun updateUiState(pageNumber: Int, element: PageElement, elementId: Int = -1) {
        Log.d("update", "$element")
        if (pageNumber != 0) {
            val newPagesMap = pagesUiState.pagesMap.toMutableMap()
            // Получаем список элементов для страницы или создаем новый пустой список, если его нет
            val newPageElementsList = newPagesMap[pageNumber]?.toMutableList() ?: mutableListOf()
            if (elementId == -1) {
                val uniqueElement = element.copy(id = elementIdCounter--)
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

    fun deletePage(pageNumber: Int) {
        val mutablePagesMap = emptyMap<Int, List<PageElement>>().toMutableMap()
        pagesUiState.pagesMap.filterNot { content ->
            content.key == pageNumber
        }.forEach { (key, value) ->
            if (key > pageNumber) {
                mutablePagesMap[key - 1] = value
            } else mutablePagesMap[key] = value
        }
        pagesUiState =
            pagesUiState.copy(
                pagesMap = mutablePagesMap, pageNumber = pagesUiState.pageNumber - 1
            )
    }

    fun deleteElement(pageNumber: Int, elementId: Int) {
        Log.d("delete", elementId.toString())
        // Получаем текущий список элементов на странице с номером pageNumber
        val currentPageElements = pagesUiState.pagesMap[pageNumber]?.toMutableList()

        // Если страница найдена и содержит элементы
        currentPageElements?.let {
            // Удаляем элемент с указанным elementId
            val updatedElements = it.filterNot { element -> element.id == elementId }
            Log.d("deleteElement", "$updatedElements")
            // Обновляем карту страниц с измененным списком элементов
            pagesUiState = pagesUiState.copy(
                pagesMap = pagesUiState.pagesMap.toMutableMap().apply {
                    this[pageNumber] = updatedElements
                }
            )
            deletedElements.add(elementId)
        }
    }

    fun cancelDeleteElement(
        pageNumber: Int,
        elementId: Int,
        pageWidth: Int,
        pageHeight: Int,
        elementSize: IntSize
    ) {
        val currentPageElements = pagesUiState.pagesMap[pageNumber]?.toMutableList()

        // Если страница найдена и содержит элементы
        currentPageElements?.forEachIndexed { index, pageElement ->
            if (pageElement.id == elementId) {
                val newPositionX = pageElement.offsetX.coerceAtLeast(0f)
                    .coerceAtMost((pageWidth - elementSize.width) / pageWidth.toFloat())
                val newPositionY = pageElement.offsetY.coerceAtLeast(0f)
                    .coerceAtMost((pageHeight - elementSize.height) / pageHeight.toFloat())
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
        val pageOrientation = pagesUiState.pageOrientation

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
                        Log.d("orientation1", pagesUiState.pageOrientation.toString())
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

        albumsRepository.updatePageOrientation(
            albumId = albumId,
            newPageOrientation = pageOrientation
        )

        deletedElements.forEach { elementId ->
            val selectedElement: AlbumDetailed? =
                albumsRepository.getAlbumDetailsStream(elementId).first()

            if (selectedElement != null) {
                albumsRepository.deleteAlbumDetails(selectedElement)
            }
        }
    }
}





