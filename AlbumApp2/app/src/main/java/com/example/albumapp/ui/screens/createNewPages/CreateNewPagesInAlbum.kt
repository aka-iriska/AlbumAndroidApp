package com.example.albumapp.ui.screens.createNewPages

import android.content.Context
import android.graphics.pdf.PdfDocument.Page
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.example.albumapp.R
import com.example.albumapp.data.AppViewModelProvider
import com.example.albumapp.ui.components.forNewPage.DraggableSticker
import com.example.albumapp.ui.navigation.AppTopBar
import com.example.albumapp.ui.navigation.NavigationDestination
import com.example.albumapp.ui.screens.currentAlbum.CurrentAlbumViewModel
import com.example.albumapp.ui.screens.currentAlbum.ElementType
import com.example.albumapp.ui.screens.currentAlbum.PageElement

object CreateNewPagesDestination : NavigationDestination {
    override val route = "create_new_pages_in_album"
    override val titleRes = R.string.create_new_pages_in_album
    const val AlbumIdArg = "itemId"
    val routeWithArgs = "${CreateNewPagesDestination.route}/{$AlbumIdArg}"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateNewPages(
    navigateBack: (Int) -> Unit = {},
    albumViewModel: CurrentAlbumViewModel = viewModel(factory = AppViewModelProvider.Factory),
    modifier: Modifier = Modifier
) {
    val albumUiState = albumViewModel.uiState.collectAsState()
    val context = LocalContext.current
    val stickersList =
        listOf<Int>(R.raw.heart, R.raw.star, R.raw.scotch, R.raw.sea_plant, R.raw.instax_square)
    Scaffold(topBar = {
        AppTopBar(title = "New pages in album",
            navigateBack = { navigateBack(albumUiState.value.albumDetails.albumId) })
    }) { innerpadding ->
        CreateNewPagesBody(
            stickersList = stickersList,
            context = context,
            modifier = modifier.padding(innerpadding)
        )
    }
}

@Composable
fun CreateNewPagesBody(stickersList: List<Int>, context: Context, modifier: Modifier = Modifier) {
    var stickersPressed by remember { mutableStateOf(false) }
    var settingsPressed by remember { mutableStateOf(false) }
    var addImagePressed by remember { mutableStateOf(false) }
    val addedElements = remember { mutableStateListOf<PageElement>() }
    Column(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
            //.background(MaterialTheme.colorScheme.surfaceContainerLow)
        ) {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .background(MaterialTheme.colorScheme.surfaceContainerHigh),
                verticalAlignment = Alignment.CenterVertically
            ) {
                item {
                    IconToggleButton(
                        checked = stickersPressed,
                        onCheckedChange = { stickersPressed = it }) {
                        if (stickersPressed) {
                            Icon(
                                Icons.Filled.Favorite,
                                contentDescription = "Show Stickers",
                                modifier = Modifier.stickerChoice()
                            )
                        } else {
                            Icon(
                                Icons.Outlined.FavoriteBorder,
                                contentDescription = "Add Stickers",
                                modifier = Modifier.stickerChoice()
                            )
                        }
                    }
                }
                if (stickersPressed) {
                    items(stickersList) { stickerResId ->
                        SvgSticker(
                            stickerId = stickerResId,
                            context = context,
                            onClick = {
                                addedElements.add(
                                    PageElement(
                                        resourceId = stickerResId, // Используем идентификатор ресурса стикера
                                        offsetX = 100f,
                                        offsetY = 100f,
                                        scale = 1f,
                                        rotation = 0f
                                    )
                                )
                            },
                            modifier = Modifier.stickerChoice()
                        )
                    }
                }
                item {
                    IconToggleButton(
                        checked = addImagePressed,
                        onCheckedChange = { addImagePressed = it }) {
                        if (addImagePressed) {
                            Icon(
                                Icons.Filled.AddCircle,
                                contentDescription = "Choose Image",
                                modifier = Modifier.stickerChoice()
                            )
                        } else {
                            Icon(
                                Icons.Outlined.Add,
                                contentDescription = "Add image",
                                modifier = Modifier.stickerChoice()
                            )
                        }
                    }
                }
                item {
                    IconToggleButton(
                        checked = settingsPressed,
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

            }
        }
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center
        ) {
            Column(
                modifier = Modifier
                    .aspectRatio(2f / 3f)
                    .padding(dimensionResource(id = R.dimen.padding_from_edge)) // padding до shadow
                    .shadow(10.dp, shape = RoundedCornerShape(8.dp)) // shadow с закруглением
                    .clip(RoundedCornerShape(8.dp)) // Clip для правильной тени
                    .background(MaterialTheme.colorScheme.surfaceContainerHighest)

            ) {
                CanvasBody(addedElements)
            }
        }
    }
}
@Composable
fun CanvasBody(elements: List<PageElement> = emptyList(), onUpdate: (PageElement) -> Unit = {}){
    Box(modifier = Modifier.fillMaxSize()) {
        elements.forEach { element ->
            when (element.type) {
                ElementType.STICKER -> DraggableSticker(
                    stickerId = element.resourceId!!,
                    context = LocalContext.current,
                    pageSize = IntSize(300, 500),
                    sticker = element,
                    onStickerUpdate = onUpdate
                )
                ElementType.IMAGE -> {}
                ElementType.TEXT_FIELD -> {}
            }
        }
        }
}

//@Composable
//fun EditablePage(elements: List<PageElement> = emptyList(), onUpdate: (PageElement) -> Unit = {}) {
//    Box(modifier = Modifier.fillMaxSize()) {
//        elements.forEach { element ->
//            when (element.type) {
//                ElementType.STICKER -> DraggableSticker(
//                    stickerId = element.resourceId!!,
//                    context = LocalContext.current,
//                    pageSize = IntSize(300, 500),
//                    sticker = element,
//                    onStickerUpdate = onUpdate
//                )
//
//                ElementType.IMAGE -> DraggableImage(
//                    imageId = element.resourceId!!,
//                    context = LocalContext.current,
//                    element = element,
//                    onElementUpdate = onUpdate
//                )
//
//                ElementType.TEXT_FIELD -> DraggableTextField(
//                    text = element.text.orEmpty(),
//                    element = element,
//                    onTextUpdate = onUpdate
//                )
//            }
//        }
//    }
//}

@Composable
fun SvgSticker(stickerId: Int, onClick:()->Unit, context: Context, modifier: Modifier = Modifier) {
    // Получаем идентификатор ресурса по имени файла (без расширения)
    val painter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(context)
            .data(stickerId)
            .decoderFactory(SvgDecoder.Factory())
            .build(),
    )

    if (painter.state is AsyncImagePainter.State.Error) {
        Log.e("tag", "Error loading SVG")
    }

    Image(
        painter = painter,
        contentDescription = "Sticker",
        modifier = modifier.clickable ( onClick = onClick )
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
