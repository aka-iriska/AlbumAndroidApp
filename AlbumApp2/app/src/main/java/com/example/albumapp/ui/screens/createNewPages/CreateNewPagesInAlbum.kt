package com.example.albumapp.ui.screens.createNewPages

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
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
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
import com.example.albumapp.R
import com.example.albumapp.data.AppViewModelProvider
import com.example.albumapp.ui.components.forCountPages.ShowActivePageByRadioButton
import com.example.albumapp.ui.components.forHome.SureChoice
import com.example.albumapp.ui.components.forNewPage.DraggableSticker
import com.example.albumapp.ui.components.forNewPage.SaveChangesModal
import com.example.albumapp.ui.navigation.AppTopBar
import com.example.albumapp.ui.navigation.NavigationDestination
import com.example.albumapp.ui.screens.currentAlbum.CurrentAlbumUiState
import com.example.albumapp.ui.screens.currentAlbum.ElementType
import com.example.albumapp.ui.screens.currentAlbum.PageElement
import com.example.albumapp.ui.screens.currentAlbum.stickersMap
import kotlinx.coroutines.launch
import kotlin.math.min

object CreateNewPagesDestination : NavigationDestination {
    override val route = "create_new_pages_in_album"
    override val titleRes = R.string.create_new_pages_in_album
    const val AlbumIdArg = "itemId"
    val routeWithArgs = "$route/{$AlbumIdArg}"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateNewPages(
    modifier: Modifier = Modifier,
    navigateBack: (Int) -> Unit = {},
    albumViewModel: CreateNewPagesViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val albumUiState = albumViewModel.pagesUiState
    val context = LocalContext.current

    val stickerNames = stickersMap.keys.toList()

    val openAlertDialog = remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()


    Scaffold(topBar = {
        AppTopBar(title = "New pages in album", navigateBack = {
            if (albumUiState.changed) {
                openAlertDialog.value = true
            } else {
                navigateBack(albumUiState.albumId)
            }
        })
    }) { innerPadding ->

        BackHandler {
            if (albumUiState.changed) {
                openAlertDialog.value = !openAlertDialog.value
            } else {
                navigateBack(albumUiState.albumId)
            }
        }
        when {
            openAlertDialog.value -> {
                SaveChangesModal(
                    saveChanges = {
                        coroutineScope.launch {
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
            modifier = modifier.padding(innerPadding),
            onUpdate = albumViewModel::updateUiState,
            albumUiState = albumUiState,
            onDelete = albumViewModel::deleteElement,
            onCancelDelete = albumViewModel::cancelDeleteElement,
            addNewPage = albumViewModel::addNewPage,
            updateCurrentPage = albumViewModel::updateCurrentPage
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CreateNewPagesBody(
    stickersList: List<String>,
    context: Context,
    modifier: Modifier = Modifier,
    onUpdate: (Int, PageElement, Int) -> Unit,
    albumUiState: CurrentAlbumUiState,
    onDelete: (Int, Int) -> Unit,
    onCancelDelete: (Int, Int, Int, Int, IntSize) -> Unit,
    addNewPage: () -> Unit,
    updateCurrentPage: (Int) -> Unit

) {
    var addedElements = albumUiState.pagesMap
    var stickersPressed by remember { mutableStateOf(false) }
    var settingsPressed by remember { mutableStateOf(false) }
    var pageNumber = albumUiState.pageNumber
    var pageSize by remember { mutableStateOf(IntSize.Zero) }

    /**
     * For Image Picker
     */
//    var imageUri by rememberSaveable { mutableStateOf<Uri?>(null) }
    val picker =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.PickVisualMedia()) { uri: Uri? ->
            if (uri != null) {
                /*    imageUri = uri*/
                onUpdate(
                    albumUiState.currentPage,
                    PageElement(
                        type = ElementType.IMAGE,
                        offsetY = 0f / pageSize.width,
                        offsetX = 0f / pageSize.height,
                        scale = 0.1f,// / min(pageSize.width, pageSize.height),
                        rotation = 0f,
                        resource = uri.toString()
                    ),
                    -1
                ) // Обновляем ImageUri
            } else {
                Log.e("PhotoPicker", "No media selected")
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
                                        scale = 0.1f,// / min(pageSize.width, pageSize.height),
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

                /*todo add adding images*/
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
                    IconButton(onClick = {}) {
                        Icon(
                            painterResource(R.drawable.add_text_fields2),
                            modifier = Modifier.stickerChoice(),
                            contentDescription = ""
                        )
                    }
                }
                /**
                 * for settings: changing orientation of page, count of showed ones
                 */

                item {
                    IconToggleButton(checked = settingsPressed,
                        onCheckedChange = { settingsPressed = it }) {
                        if (settingsPressed) {
                            Icon(
                                Icons.Filled.Settings,
                                contentDescription = "Choose Image",
                                modifier = Modifier.stickerChoice()
                            )
                        } else {
                            Icon(
                                Icons.Outlined.Settings,
                                contentDescription = "Add image",
                                modifier = Modifier.stickerChoice()
                            )
                        }
                    }
                }

                /**
                 * for adding new pages in Album
                 */

                /*todo add adding new pages*/
                item {
                    IconButton(onClick = addNewPage) {
                        Icon(
                            painterResource(R.drawable.new_page),
                            modifier = Modifier.stickerChoice(),
                            contentDescription = ""
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
                modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center
            ) {
                var isNearEdge by remember { mutableStateOf(false) } // Для подсветки всей `Column`
                val paddingSizeForPage = (min(pageSize.height, pageSize.width) / 22).dp
                val pagerState = rememberPagerState(pageCount = { pageNumber })
                HorizontalPager(state = pagerState) { pageIndex ->
                    LaunchedEffect(pagerState.settledPage) {
                        updateCurrentPage(pagerState.settledPage + 1)
                    }
                    Column(
                        modifier = Modifier
                            .aspectRatio(2f / 3f)
                            .padding(dimensionResource(id = R.dimen.padding_from_edge)) // padding до shadow
                            .shadow(
                                10.dp,
                                shape = RoundedCornerShape(8.dp)
                            ) // shadow с закруглением
                            .clip(RoundedCornerShape(8.dp)) // Clip для правильной тени
                            .background(MaterialTheme.colorScheme.surfaceContainerHighest)
                    ) {
                        if (pagerState.settledPage == pageIndex && albumUiState.currentPage == pageIndex + 1) {
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
                                    .border(
                                        width = paddingSizeForPage,
                                        color = if (isNearEdge) Color.Red.copy(alpha = 0.3f) else Color.Unspecified,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .padding(paddingSizeForPage)
                                    .onSizeChanged { newSize ->
                                        pageSize = newSize
                                    },
                                comeToTheEdge = { isNearEdge = it },
                                onElementRemove = onDelete,
                                onCancelDelete = onCancelDelete,
                                currentPage = albumUiState.currentPage
                            )
                        } /*else {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) { CircularProgressIndicator() }

                        }*/
                    }
                }
                ShowActivePageByRadioButton(
                    pagerState,
                    Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}

@Composable
fun CanvasBody(
    modifier: Modifier = Modifier,
    pageSize: IntSize,
    elements: List<PageElement> = emptyList(),
    onUpdate: (Int, PageElement, Int) -> Unit,
    onElementRemove: (Int, Int) -> Unit,
    comeToTheEdge: (Boolean) -> Unit,
    onCancelDelete: (Int, Int, Int, Int, IntSize) -> Unit,
    currentPage: Int
) {
    var elementToRemove by remember { mutableStateOf<Pair<Int, Int>?>(null) } // Текущий элемент для удаления
    val additionalColor: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
    var elementSize by remember { mutableStateOf(IntSize.Zero) }
    Box(
        modifier = modifier
    ) {
        elements.forEach { element ->
            DraggableSticker(
                pageNumber = currentPage,
                elementName = element.resource,
                context = LocalContext.current,
                pageSize = pageSize,
                element = element,
                onElementUpdate = onUpdate,
                onNear = { comeToTheEdge(it) },
                onDelete = {
                    elementToRemove = Pair(currentPage, element.id)
                    elementSize = it
                }
            )
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
                            color = additionalColor,
                            onYesClick =
                            {
                                onElementRemove(
                                    elementToRemove!!.first,
                                    elementToRemove!!.second
                                )  // Удаление элемента
                                comeToTheEdge(false)
                                elementToRemove = null

                            },
                            /*todo исправить cancel, чтобы не показывался*/
                            onCancelClick = {},
                            onNoClick = {
                                onCancelDelete(
                                    elementToRemove!!.first,
                                    elementToRemove!!.second,
                                    pageSize.width,
                                    pageSize.height,
                                    elementSize
                                )
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
