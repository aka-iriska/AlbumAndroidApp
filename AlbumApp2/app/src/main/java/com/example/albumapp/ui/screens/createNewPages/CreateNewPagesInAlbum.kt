package com.example.albumapp.ui.screens.createNewPages

import android.content.Context
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.example.albumapp.R
import com.example.albumapp.data.AppViewModelProvider
import com.example.albumapp.ui.navigation.AppTopBar
import com.example.albumapp.ui.navigation.NavigationDestination
import com.example.albumapp.ui.screens.currentAlbum.CurrentAlbumViewModel
import com.example.albumapp.ui.theme.AlbumAppTheme
import kotlin.math.roundToInt

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
    Scaffold(
        topBar = {
            AppTopBar(
                title = "New pages in album",
                navigateBack = { navigateBack(albumUiState.value.albumDetails.albumId) }
            )
        }
    ) { innerpadding ->
        CreateNewPagesBody(
            stickers = stickersList, context = context, modifier = modifier.padding(innerpadding)
        )
    }
}

@Composable
fun CreateNewPagesBody(context: Context, stickers: List<Int>, modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier
            .fillMaxWidth().background(MaterialTheme.colorScheme.surfaceContainerLow)) {
            LazyRow(modifier = Modifier.fillMaxWidth()) {
                items(stickers) { stickerId ->
                    SvgSticker(stickerId, context)
                }
            }
        }
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(
                    dimensionResource(id = R.dimen.padding_from_edge)
                )
                .background(MaterialTheme.colorScheme.primary)
                .aspectRatio(2f/3f),
            verticalArrangement = Arrangement.Center
        ) {
//            Canvas(modifier = Modifier) {
//            }
        }

    }
}

@Composable
fun DraggableSticker(stickerId: Int, context: Context, pageSize: IntSize) {
    // Состояния для позиции стикера
    var stickerPosition by remember { mutableStateOf(Offset.Zero) }

    // Modifier для перетаскивания стикера
    val modifier = Modifier
        .offset { stickerPosition.toIntOffset() }
        .pointerInput(Unit) {
            detectDragGestures { change, dragAmount ->
                change.consume()

                // Обновляем позицию стикера
                val newPosition = stickerPosition + dragAmount

                // Ограничиваем перемещение внутри границ страницы
                stickerPosition = Offset(
                    x = newPosition.x.coerceIn(0f, pageSize.width.toFloat() - 100.dp.toPx()),
                    y = newPosition.y.coerceIn(0f, pageSize.height.toFloat() - 100.dp.toPx())
                )
            }
        }

    // Отображаем стикер
    SvgSticker(stickerId = stickerId, context = context)
}

@Composable
fun SvgSticker(stickerId: Int, context: Context) {
    // Получаем идентификатор ресурса по имени файла (без расширения)
    Image(
        painter = rememberAsyncImagePainter(
            model = ImageRequest.Builder(context).data(stickerId) // Передаем ID ресурса
                .decoderFactory(SvgDecoder.Factory()) // Используем декодер для SVG
                .build(),
        ),
        contentDescription = "Sticker",
        modifier = Modifier.size(100.dp) // Установите нужный размер
    )
}

// Вспомогательная функция для преобразования Offset в IntOffset
fun Offset.toIntOffset() = androidx.compose.ui.unit.IntOffset(x.roundToInt(), y.roundToInt())

@Preview(showBackground = true)
@Composable
fun CreateNewPagesPreview() {
    AlbumAppTheme {
        val stickersList = listOf<Int>(
            R.raw.heart, R.raw.star
        )
        Surface(modifier = Modifier.fillMaxSize()) {
            CreateNewPagesBody(context = LocalContext.current, stickers = stickersList, modifier = Modifier.padding(top = 10.dp))
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xff0000)
@Composable
fun SvgStickerPreview() {
    AlbumAppTheme(darkTheme = true) {
        SvgStickerDrawable(stickerId = R.drawable.instax_square)
    }
}

@Composable
fun SvgStickerDrawable(stickerId: Int) {
    Image(
        imageVector = ImageVector.vectorResource(id = stickerId),
        contentDescription = "Sticker",
        modifier = Modifier.size(100.dp) // Установите нужный размер
    )
}