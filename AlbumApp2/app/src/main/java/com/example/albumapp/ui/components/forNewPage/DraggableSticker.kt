package com.example.albumapp.ui.components.forNewPage

import android.content.Context
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.example.albumapp.ui.screens.currentAlbum.PageElement
import kotlin.math.min


@Composable
fun DraggableSticker(
    stickerId: Int,
    pageNumber: Int = 0,
    context: Context = LocalContext.current,
    pageSize: IntSize,
    sticker: PageElement,
    onStickerUpdate: (Int, PageElement, Int) -> Unit,
    //onUpdateCheck:(Int, PageElement)->Unit
) {
    var position by remember {
        mutableStateOf(
            Offset(
                sticker.offsetX,
                sticker.offsetY
            )
        )
    } // для перемещения
    var rotation by remember { mutableFloatStateOf(sticker.rotation) } // для вращения
    var scale by remember { mutableFloatStateOf(sticker.scale) } // для увеличения

    /*todo backstack to cancel changes*/

    Box(
        modifier = Modifier
            .offset {
                IntOffset(
                    position.x.toInt(),
                    position.y.toInt()
                )
            } // используем Offset для позиционирования
            .size((100.dp * scale)) // Явно задаем размер с учетом масштаба
            .transformable(
                state = rememberTransformableState { zoomChange, offsetChange, rotationChange ->
                    scale *= zoomChange
                    rotation += rotationChange
                    position += offsetChange
                    //onUpdateCheck()
                    // Обновляем стикер при изменении его параметров
                    onStickerUpdate(
                        pageNumber,
                        sticker.copy(
                            offsetX = position.x / pageSize.width,
                            offsetY = position.y / pageSize.height,
                            scale = scale / min(pageSize.width, pageSize.height),
                            rotation = rotation
                        ),
                        sticker.id
                    )
                }
            )
    ) {
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
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                    rotationZ = rotation
                }
        )

    }
}
