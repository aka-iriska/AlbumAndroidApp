package com.example.albumapp.ui.screens.editPagesInAlbum

import SureChoice
import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.example.albumapp.PART_OF_PAGE_FOR_PADDING
import com.example.albumapp.R
import com.example.albumapp.data.AppViewModelProvider
import com.example.albumapp.ui.components.forCountPages.ShowActivePageByRadioButton
import com.example.albumapp.ui.components.forNewPage.DraggableElement
import com.example.albumapp.ui.components.forNewPage.SaveChangesModal
import com.example.albumapp.ui.navigation.AppTopBar
import com.example.albumapp.ui.navigation.NavigationDestination
import com.example.albumapp.ui.screens.currentAlbum.CurrentAlbumUiState
import com.example.albumapp.ui.screens.currentAlbum.ElementType
import com.example.albumapp.ui.screens.currentAlbum.PageElement
import com.example.albumapp.ui.screens.currentAlbum.stickersMap
import com.example.albumapp.utils.colorToHex
import kotlinx.coroutines.launch
import kotlin.math.min

object CreateNewPagesDestination : NavigationDestination {
    override val route = "create_new_pages_in_album"
    override val titleRes = R.string.create_new_pages_in_album
    const val ALBUM_ID_ARG = "itemId"
    val routeWithArgs = "$route/{$ALBUM_ID_ARG}"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateNewPages(
    modifier: Modifier = Modifier,
    navigateBack: (Int) -> Unit = {},
    albumViewModel: EditPagesViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val albumUiState = albumViewModel.pagesUiState
    val context = LocalContext.current

    val stickerNames = stickersMap.keys.toList()

    val openAlertDialog = remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    val focusManager = LocalFocusManager.current

    Scaffold(topBar = {
        AppTopBar(title = "New pages in album", navigateBack = {
            if (albumUiState.changed) {
                openAlertDialog.value = true
            } else {
                navigateBack(albumUiState.albumId)
            }
        })
    },
        modifier = Modifier
            .pointerInput(Unit) {
                // Обработчик нажатий на Box
                detectTapGestures(onTap = {
                    // Убираем фокус с текущего элемента
                    focusManager.clearFocus()
                })
            }
    ) { innerPadding ->

        BackHandler {
            if (albumUiState.changed) {
                openAlertDialog.value = !openAlertDialog.value
            } else {
                navigateBack(albumUiState.albumId)
            }
            focusManager.clearFocus()
        }
        when {
            openAlertDialog.value -> {
                SaveChangesModal(
                    saveChanges = {
                        coroutineScope.launch {
                            Log.d("orientation on click", albumUiState.pageOrientation.toString())
                            albumViewModel.savePagesForAlbum(context)
                            openAlertDialog.value = false
                            navigateBack(albumUiState.albumId)
                        }
                    },
                    onDismissRequest = { openAlertDialog.value = false },
                    onNavigateBack = {
                        openAlertDialog.value = false
                        navigateBack(albumUiState.albumId)
                    })
            }

        }

        CreateNewPagesBody(
            stickersList = stickerNames,
            context = context,
            focusManager = focusManager,
            modifier = modifier.padding(innerPadding),
            onUpdate = albumViewModel::updateUiState,
            albumUiState = albumUiState,
            onDeletePage = albumViewModel::deletePage,
            onDelete = albumViewModel::deleteElement,
            onCancelDelete = albumViewModel::cancelDeleteElement,
            addNewPage = albumViewModel::addNewPage,
            updateCurrentPage = albumViewModel::updateCurrentPage,
            updatePageOrientation = albumViewModel::updatePageOrientation
        )
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun CreateNewPagesBody(
    stickersList: List<String>,
    context: Context,
    focusManager: FocusManager,
    modifier: Modifier = Modifier,
    onUpdate: (Int, PageElement, Int) -> Unit,
    albumUiState: CurrentAlbumUiState,
    onDelete: (Int, Int) -> Unit,
    onDeletePage: (Int) -> Unit,
    onCancelDelete: (Int, Int, Int, Int, IntSize) -> Unit,
    addNewPage: () -> Unit,
    updateCurrentPage: (Int) -> Unit,
    updatePageOrientation: (Boolean, IntSize) -> Unit,

    ) {
    val addedElements = albumUiState.pagesMap
    var stickersPressed by remember { mutableStateOf(false) }
    // var settingsPressed by remember { mutableStateOf(false) }
    val pageNumber = albumUiState.pageNumber
    var pageSize by remember { mutableStateOf(IntSize.Zero) }

    val primaryColor = MaterialTheme.colorScheme.primary

    var showSaveChanges by remember { mutableStateOf(false) }

    /**
     * For Image Picker
     */

    val picker =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.PickVisualMedia()) { uri: Uri? ->
            if (uri != null) {
                onUpdate(
                    albumUiState.currentPage,
                    PageElement(
                        type = ElementType.IMAGE,
                        offsetY = 0f / pageSize.width,
                        offsetX = 0f / pageSize.height,
                        scale = 0.3f,// / min(pageSize.width, pageSize.height),
                        rotation = 0f,
                        resource = uri.toString()
                    ),
                    -1
                )
            } else {
                Log.e("PhotoPicker", "No media selected")
            }
        }

    if (showSaveChanges) {
        Dialog(onDismissRequest = { showSaveChanges = false }) {
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = MaterialTheme.shapes.medium,
            ) {
                Box(
                    modifier = Modifier.clip(MaterialTheme.shapes.medium)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(dimensionResource(id = R.dimen.padding_from_edge))
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.SpaceAround
                    ) {
                        SureChoice(
                            onYesClick = {
                                onDeletePage(albumUiState.currentPage)
                                showSaveChanges = false
                            },
                            onNoClick = { showSaveChanges = false },
                            text = "Are you sure to delete the whole page?"
                        )

                    }
                }
            }
        }
    }
    Column(modifier = modifier.fillMaxSize()) {

        /**
         * Icons for adding photos, stickers, textFields, newPages, changing orientation
         */

        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .background(MaterialTheme.colorScheme.surfaceContainerHigh),
                verticalAlignment = Alignment.CenterVertically
            ) {

                /**
                 * for stickers
                 */

                item {
                    IconToggleButton(checked = stickersPressed,
                        onCheckedChange = { stickersPressed = it }) {
                        if (stickersPressed) {
                            Icon(
                                painterResource(R.drawable.heart_minus),
                                contentDescription = "Show Stickers",
                                modifier = Modifier.stickerChoice()
                            )
                        } else {
                            Icon(
                                painterResource(R.drawable.heart_plus),
                                contentDescription = "Add Stickers",
                                modifier = Modifier.stickerChoice()
                            )
                        }
                    }
                }
                if (stickersPressed) {
                    items(stickersList) { stickerName ->
                        val stickerResId = stickersMap[stickerName]
                        SvgSticker(
                            stickerId = stickerResId!!,
                            context = context,
                            onClick = {
                                onUpdate(
                                    albumUiState.currentPage,
                                    PageElement(
                                        type = ElementType.STICKER,
                                        offsetY = 0f / pageSize.width,
                                        offsetX = 0f / pageSize.height,
                                        scale = 0.2f,// / min(pageSize.width, pageSize.height),
                                        rotation = 0f,
                                        resource = stickerName
                                    ),
                                    -1
                                )
                                if (pageNumber == 0) {
                                    Toast.makeText(
                                        context,
                                        "Add at least one page",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            },
                            modifier = Modifier.stickerChoice()
                        )
                    }
                }

                /**
                 * for images
                 */

                item {
                    IconButton(onClick = {
                        picker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    }) {
                        Icon(
                            painterResource(R.drawable.add_photo),
                            modifier = Modifier.stickerChoice(),
                            contentDescription = ""
                        )
                    }
                }

                /**
                 * for text fields
                 */

                item {
                    IconButton(
                        onClick = {
                            onUpdate(
                                albumUiState.currentPage,
                                PageElement(
                                    type = ElementType.TEXT_FIELD,
                                    offsetY = 0f / pageSize.width,
                                    offsetX = 0f / pageSize.height,
                                    scale = 0.2f,// / min(pageSize.width, pageSize.height),
                                    rotation = 0f,
                                    resource = "16f/${colorToHex(primaryColor)}/"
                                ),
                                -1
                            )
                        }) {
                        Icon(
                            painterResource(R.drawable.add_text_fields2),
                            modifier = Modifier.stickerChoice(),
                            contentDescription = ""
                        )
                    }
                }

                /**
                 * for adding new pages in Album
                 */

                item {
                    IconButton(onClick = addNewPage) {
                        Icon(
                            painterResource(R.drawable.new_page),
                            modifier = Modifier.stickerChoice(),
                            contentDescription = "Add a new page"
                        )
                    }
                }

                /**
                 * for deleting the whole page
                 */

                item {
                    IconButton(onClick = { showSaveChanges = true }) {
                        Icon(
                            painterResource(R.drawable.page_delete),
                            modifier = Modifier.stickerChoice(),
                            contentDescription = "Delete the whole page"
                        )
                    }
                }

                /**
                 * for settings: changing orientation of page, count of showed ones
                 */

                item {
                    IconButton(onClick = {
                        updatePageOrientation(albumUiState.pageOrientation, pageSize)
                    }) {
                        Icon(
                            painterResource(R.drawable.rotate_pages),
                            modifier = Modifier.stickerChoice(),
                            contentDescription = "Change pages rotation"
                        )
                    }
                }
            }
        }

        /**
         * part for viewing [CanvasBody]
         */

        if (pageNumber != 0) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                var isNearBottomEdge by remember { mutableStateOf(false) } // Для подсветки всей `Column`
                val paddingSizeForPage = (min(pageSize.height, pageSize.width) / PART_OF_PAGE_FOR_PADDING).dp
                val pagerState = rememberPagerState(pageCount = { pageNumber })

                /**
                 * для BottomSheet
                 */

                val sheetState = rememberModalBottomSheetState()
                var showBottomSheet by remember { mutableStateOf(false) }
                var selectedElementId by remember { mutableIntStateOf(0) }

                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) { pageIndex ->
                    LaunchedEffect(pagerState.settledPage) {
                        updateCurrentPage(pagerState.settledPage + 1)
                    }
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(),
                            contentAlignment = Alignment.Center // Центрирование содержимого внутри Box
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .then(
                                        if (albumUiState.pageOrientation) Modifier.aspectRatio(3f / 2f)
                                        else Modifier.aspectRatio(2f / 3f)
                                    )
                                    .padding(dimensionResource(id = R.dimen.padding_from_edge)) // padding до shadow
                                    .shadow(
                                        10.dp,
                                        shape = RoundedCornerShape(8.dp)
                                    ) // shadow с закруглением
                                    .clip(RoundedCornerShape(8.dp)) // Clip для правильной тени
                                    .background(MaterialTheme.colorScheme.surfaceContainerHighest),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                if (pagerState.settledPage == pageIndex && albumUiState.currentPage == pageIndex + 1) {

                                    val gradientBrush = Brush.verticalGradient(
                                        colors = listOf(Color.Transparent, Color.Transparent,MaterialTheme.colorScheme.error.copy(alpha = 0.3f))
                                    )

                                    updateCurrentPage(pagerState.settledPage + 1)

                                    CanvasBody(
                                        pageSize = pageSize,
                                        elements = addedElements.getOrDefault(
                                            albumUiState.currentPage,
                                            emptyList()
                                        ),
                                        onUpdate = onUpdate,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .then(
                                                if (isNearBottomEdge) Modifier.background(brush = gradientBrush)
                                                else Modifier.background(color = Color.Transparent) // Используем пустой Modifier, если подсветка не нужна
                                            )
                                            .padding(paddingSizeForPage)
                                            .onSizeChanged { newSize ->
                                                pageSize = newSize
                                            },
                                        comeToTheEdge = { isNearBottomEdge = it },
                                        onElementRemove = onDelete,
                                        onCancelDelete = onCancelDelete,
                                        currentPage = albumUiState.currentPage,
                                        focusManager = focusManager,
                                        sheetState = sheetState,
                                        onLongClick = { showBottom, settedElement ->
                                            showBottomSheet = showBottom
                                            selectedElementId = settedElement
                                        },
                                        showBottomSheet = showBottomSheet,
                                        selectedElementId = selectedElementId
                                    )
                                } else {
                                    Column(
                                        modifier = Modifier.fillMaxSize(),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        //CircularProgressIndicator()
                                    }
                                }
                            }
                        }
                    }
                }
                ShowActivePageByRadioButton(
                    pagerState,
                    Modifier
                        .align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CanvasBody(
    modifier: Modifier = Modifier,
    pageSize: IntSize,
    elements: List<PageElement> = emptyList(),
    onUpdate: (Int, PageElement, Int) -> Unit,
    onElementRemove: (Int, Int) -> Unit,
    comeToTheEdge: (Boolean) -> Unit,
    onCancelDelete: (Int, Int, Int, Int, IntSize) -> Unit,
    sheetState: SheetState,
    onLongClick: (Boolean, Int) -> Unit,
    showBottomSheet: Boolean,
    selectedElementId: Int,
    currentPage: Int,
    focusManager: FocusManager
) {
    val sortedElements = elements.sortedBy { it.zIndex }

    var elementToRemove by remember { mutableStateOf<Pair<Int, Int>?>(null) } // Текущий элемент для удаления

    var elementSize by remember { mutableStateOf(IntSize.Zero) }
    Box(
        modifier = modifier
    ) {
        sortedElements.forEach { element ->
            DraggableElement(
                pageNumber = currentPage,
                elementName = element.resource,
                context = LocalContext.current,
                focusManager = focusManager,
                pageSize = pageSize,
                element = element,
                onElementUpdate = onUpdate,
                onNear = { comeToTheEdge(it) },
                onDelete = {
                    elementToRemove = Pair(currentPage, element.id)
                    elementSize = it
                },
                sheetState = sheetState,
                onLongClick = onLongClick,
                showBottomSheet = showBottomSheet,
                selectedElementId = selectedElementId
            )
            // Слайдеры для изменения параметров
            /*SliderWithLabel("Scale", element.scale , 0.1f , 2f) {
                element.scale = it }*/


        }

    }

    elementToRemove?.let {
        Dialog(onDismissRequest = {
            comeToTheEdge(false)
            elementToRemove = null
        }) {
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = MaterialTheme.shapes.medium,
            ) {
                Box(
                    modifier = Modifier.clip(MaterialTheme.shapes.medium)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(dimensionResource(id = R.dimen.padding_from_edge))
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.SpaceAround
                    ) {
                        SureChoice(
                            onYesClick =
                            {
                                onElementRemove(
                                    elementToRemove!!.first,
                                    elementToRemove!!.second
                                )  // Удаление элемента
                                comeToTheEdge(false)
                                elementToRemove = null

                            },
                            onNoClick = {
                                if (elementSize.width <= pageSize.width && elementSize.height <= pageSize.height) {
                                    onCancelDelete(
                                        elementToRemove!!.first,
                                        elementToRemove!!.second,
                                        pageSize.width,
                                        pageSize.height,
                                        elementSize
                                    )
                                }
                                comeToTheEdge(false)
                                elementToRemove = null
                            },
                            text = "Do you want to delete element?",
                        )

                    }
                }
            }

        }
    }
}

@Composable
fun SvgSticker(
    stickerId: Int, onClick: () -> Unit, context: Context, modifier: Modifier = Modifier
) {
    // Получаем идентификатор ресурса по имени файла (без расширения)
    val painter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(context).data(stickerId)
            .decoderFactory(SvgDecoder.Factory())
            .build(),
    )

    if (painter.state is AsyncImagePainter.State.Error) {
        Log.e("tag", "Error loading SVG")
    }

    Image(
        painter = painter,
        contentDescription = "Sticker",
        modifier = modifier.clickable(onClick = onClick)
    )
}

fun Modifier.stickerChoice(): Modifier = this
    .size(50.dp) // Определите размеры для стикеров
    .padding(5.dp) // Добавьте отступы, если нужно
//
//@Preview(showBackground = true)
//@Composable
//fun CreateNewPageBodyPreiewEx() {
//    AlbumAppTheme {
//        Surface(modifier = Modifier.fillMaxSize()) {
//            CreateNewPagesBodyEx(LocalContext.current, listOf(R.raw.heart, R.raw.star))
//        }
//    }
//}
