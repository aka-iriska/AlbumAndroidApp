package com.example.albumapp.ui.components.forCurrentAlbum

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.example.albumapp.ui.screens.currentAlbum.ElementType
import com.example.albumapp.ui.screens.currentAlbum.PageElement
import com.example.albumapp.ui.screens.currentAlbum.stickersMap
import com.example.albumapp.ui.theme.bodyFontFamily
import kotlin.math.min

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

    val orienter = min(pageSize.width, pageSize.height)
    
    var scale by remember {
        mutableFloatStateOf(
            element.scale * orienter
        )
    } // для увеличения
    if (element.resource=="scotch"){Log.d("Scsize", "$scale, ${pageSize.width}")}

    val elementHeight = (scale).dp

    LaunchedEffect(pageSize, element.offsetX, element.offsetY) {
        position = Offset(element.offsetX * pageSize.width, element.offsetY * pageSize.height)
        rotation = element.rotation
        scale = element.scale * orienter
    }

    var fontSize by remember { mutableFloatStateOf(16f) }

    val primaryColor = MaterialTheme.colorScheme.primary
    var colorForTextField: Color by remember { mutableStateOf<Color>(primaryColor) }

    var textForTextField by remember { mutableStateOf("") }

    if (element.type == ElementType.TEXT_FIELD){
        val (fontSizeString, colorString, text) = element.resource.split("/")
        fontSize = fontSizeString.toFloat()
        colorForTextField = Color(android.graphics.Color.parseColor(colorString))
        textForTextField = text
    }

    Box(modifier = Modifier
        .offset {
            IntOffset(
                position.x.toInt(), position.y.toInt()
            )
        }
        .wrapContentSize(unbounded = true)
        .graphicsLayer {
            scaleX = 1f//scale/pageSize.width
            scaleY = 1f//scale/pageSize.width
            rotationZ = rotation
        }

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
                    contentDescription = "element",
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

            ElementType.DEFAULT -> {}
            ElementType.TEXT_FIELD -> {
                OutlinedTextField(
                    value = textForTextField,
                    readOnly = true,
                    onValueChange = {},
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn()
                        .widthIn(max = elementHeight)
                        .padding(10.dp) // Отступы внутри TextField
                    ,
                    textStyle = TextStyle(
                        fontFamily = bodyFontFamily,
                        color = colorForTextField,
                        fontSize = fontSize.sp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent
                    )
                )
            }
        }
    }
}