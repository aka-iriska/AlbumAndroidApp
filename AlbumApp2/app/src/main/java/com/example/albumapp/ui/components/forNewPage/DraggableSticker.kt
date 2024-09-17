package com.example.albumapp.ui.components.forNewPage

import android.content.Context
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.example.albumapp.ui.screens.currentAlbum.PageElement


@Composable
fun DraggableSticker(
    stickerId: Int,
    context: Context = LocalContext.current,
    pageSize:IntSize,
    sticker: PageElement,
    onStickerUpdate: (PageElement)-> Unit = {}
){
    var position by remember { mutableStateOf(Offset(sticker.offsetX, sticker.offsetY)) }

    Box(
        modifier = Modifier
            .offset { IntOffset(position.x.toInt(), position.y.toInt()) }
            .draggable(
                state = rememberDraggableState { delta ->
                    position = Offset(position.x + delta, position.y)
                    onStickerUpdate(sticker.copy(offsetX = position.x, offsetY = position.y))
                },
                orientation = Orientation.Horizontal
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
            modifier = Modifier.size(100.dp)
        )
    }
}