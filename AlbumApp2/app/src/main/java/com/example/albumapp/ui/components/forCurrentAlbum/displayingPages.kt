package com.example.albumapp.ui.components.forCurrentAlbum

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.example.albumapp.ui.screens.currentAlbum.ElementType
import com.example.albumapp.ui.screens.currentAlbum.PageElement
import com.example.albumapp.ui.screens.currentAlbum.stickersMap

@Composable
fun DisplayElement(
    elementName: String,
    context: Context = LocalContext.current,
    pageSize: IntSize,
    element: PageElement,
) {
    var position by remember {
        mutableStateOf(
            Offset(
                element.offsetX * pageSize.width, element.offsetY * pageSize.height
            )
        )
    } // для перемещения
    var rotation by remember { mutableFloatStateOf(element.rotation) } // для вращения
    var scale by remember {
        mutableFloatStateOf(
            element.scale * pageSize.width
        )
    } // для увеличения
    var elementSize by remember { mutableStateOf(IntSize.Zero) } // Размер элемента

    LaunchedEffect(pageSize, element.offsetX, element.offsetY) {
        position = Offset(element.offsetX * pageSize.width, element.offsetY * pageSize.height)
        rotation = element.rotation
        scale = element.scale * pageSize.width
    }

    Box(modifier = Modifier
        .offset {
            IntOffset(
                position.x.toInt(), position.y.toInt()
            )
        }
        .size((scale).dp)
    ) {
        when (element.type) {
            ElementType.STICKER -> {
                // Получаем идентификатор ресурса по имени файла (без расширения)
                val painter = rememberAsyncImagePainter(
                    model = ImageRequest.Builder(context).data(stickersMap[elementName])
                        .decoderFactory(SvgDecoder.Factory()).build(),
                )

                if (painter.state is AsyncImagePainter.State.Error) {
                    Log.e("tag", "Error loading SVG")
                }
                Image(painter = painter,
                    contentDescription = "element",
                    modifier = Modifier
                        .fillMaxSize()
                        .onSizeChanged { size ->
                            elementSize = size // Запоминаем фактический размер стикера
                        }
                        .graphicsLayer {
                            scaleX = 1f//scale/pageSize.width
                            scaleY = 1f//scale/pageSize.width
                            rotationZ = rotation
                        })
            }

            ElementType.IMAGE -> {
                val imageUri: Uri? =
                    if (element.resource.isNotEmpty()) Uri.parse(element.resource) else null
                val painter = rememberAsyncImagePainter(element.resource)
                if (imageUri != null) {
                    Image(
                        painter = painter,
                        contentDescription = "Image",
                        modifier = Modifier
                            .fillMaxSize()
                            .onSizeChanged { size ->
                                elementSize = size // Запоминаем фактический размер стикера
                            }
                            .graphicsLayer {
                                scaleX = 1f//scale/pageSize.width
                                scaleY = 1f//scale/pageSize.width
                                rotationZ = rotation
                            }
                    )
                }
            }

            ElementType.DEFAULT -> {}
            ElementType.TEXT_FIELD -> {}
        }
    }
}