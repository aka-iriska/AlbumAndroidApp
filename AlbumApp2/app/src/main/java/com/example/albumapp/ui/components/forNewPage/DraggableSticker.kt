package com.example.albumapp.ui.components.forNewPage

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.example.albumapp.ui.screens.currentAlbum.ElementType
import com.example.albumapp.ui.screens.currentAlbum.PageElement
import com.example.albumapp.ui.screens.currentAlbum.stickersMap


@Composable
fun DraggableSticker(
    elementName: String,
    pageNumber: Int = 0,
    context: Context = LocalContext.current,
    focusManager: FocusManager,
    pageSize: IntSize,
    element: PageElement,
    onElementUpdate: (Int, PageElement, Int) -> Unit,
    onDelete: (IntSize) -> Unit,
    onNear: (Boolean) -> Unit
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
    val elementHeight = (scale).dp
    var elementSize by remember { mutableStateOf(IntSize.Zero) } // Размер элемента

    /**
     * Для TextField
     * */

    var textForTextField by remember { mutableStateOf(element.resource) }
    val focusRequester = remember { FocusRequester() }

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
        .zIndex(element.zIndex.toFloat())
        .wrapContentSize(unbounded = true)
        .transformable(state = rememberTransformableState { zoomChange, offsetChange, rotationChange ->
            scale *= zoomChange
            rotation += rotationChange
            position += offsetChange
            focusManager.clearFocus()
            // Обновляем стикер при изменении его параметров
            Log.d("id", element.id.toString())
            onElementUpdate(
                pageNumber, element.copy(
                    offsetX = position.x / pageSize.width,
                    offsetY = position.y / pageSize.height,
                    scale = scale / pageSize.width,
                    rotation = rotation,
                    zIndex = element.zIndex
                ), element.id
            )
            val centerX = position.x + elementSize.width / 2
            val centerY = position.y + elementSize.height / 2
            // Проверяем, находится ли стикер за пределами padding
            if (centerX - elementSize.width / 4 < 0 || centerY - elementSize.height / 4 < 0 || centerX + elementSize.width / 4 >= pageSize.width || centerY + elementSize.height / 4 >= pageSize.height) {
                onNear(true)
            } else onNear(false)
            // Вычисляем центр элемента
            if (centerX - elementSize.width / 5 < 0 || centerY - elementSize.height / 5 < 0 || centerX + elementSize.width / 5 >= pageSize.width || centerY + elementSize.height / 5 >= pageSize.height) {
                onDelete(elementSize) // Вызываем onDelete, если стикер попадает в padding
            }
        }
        )
        .graphicsLayer {
            scaleX = 1f//scale/pageSize.width
            scaleY = 1f//scale/pageSize.width
            rotationZ = rotation
        }


    ) {
        Box(
            modifier = Modifier
                .onSizeChanged { size ->
                    elementSize = size // Запоминаем фактический размер стикера
                }
                .clipToBounds() // Ограничивает offset внутри padding
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
                    Image(
                        painter = painter,
                        contentDescription = "Sticker",
                        modifier = Modifier
                            .size(elementHeight)
                            .fillMaxSize()

                    )
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
                                .size(elementHeight)
                                .fillMaxSize()

                        )
                    }
                }

                ElementType.TEXT_FIELD -> {
                    key(element.id) {
                        OutlinedTextField(
                            value = textForTextField,
                            placeholder = { Text("Input text") },
                            onValueChange = { textForTextField = it },
                            modifier = Modifier
                                .focusRequester(focusRequester)
                                .width(elementHeight)
                                .fillMaxWidth()
                                .heightIn()
                                .padding(10.dp) // Отступы внутри TextField
                                .onFocusChanged { focusState ->
                                    Log.d(
                                        "updateText",
                                        "${focusState.isFocused} \n $textForTextField \n ${element}"
                                    )
                                    if (!focusState.isFocused && element.resource != textForTextField) {
                                        // Обновляем глобальное состояние при потере фокуса
                                        onElementUpdate(
                                            pageNumber,
                                            element.copy(resource = textForTextField),
                                            element.id
                                        )
                                    }
                                }
                            //.focusable() // Обеспечиваем, что TextField может получать фокус
                            ,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                disabledContainerColor = Color.Transparent,
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = Color.Transparent
                            )
                        )
                    }
                }

                ElementType.DEFAULT -> {}
            }
        }
    }
}
