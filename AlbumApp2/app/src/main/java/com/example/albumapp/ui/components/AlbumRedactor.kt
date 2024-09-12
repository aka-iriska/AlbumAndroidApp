package com.example.albumapp.ui.components

import android.net.Uri
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.albumapp.ui.theme.AlbumAppTheme

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun EditAlbum(
    imageUri: Uri?,
    picker: ManagedActivityResultLauncher<PickVisualMediaRequest, Uri?>,
    modifier: Modifier = Modifier
) {

    val pagerState = rememberPagerState(pageCount = {
        3
    })
    HorizontalPager(state = pagerState) { page ->
        var text by remember { mutableStateOf("") }
        ColouredCard {
            if (imageUri != null) {
                AsyncImage(
                    modifier = Modifier
                        .size(250.dp)
                        .clickable {
                            picker.launch(
                                PickVisualMediaRequest(
                                    ActivityResultContracts.PickVisualMedia.ImageOnly
                                )
                            )
                        },
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(imageUri)
                        .crossfade(enable = true)
                        .build(),
                    contentDescription = "Avatar Image",
                    contentScale = ContentScale.Crop,
                )
            }
            TextField(
                modifier = modifier
                    .fillMaxSize(),
                placeholder = { Text("Input your impressions") },
                value = text,
                onValueChange = { text = it },
                colors = TextFieldDefaults.colors(
                    //textColor = Color.Gray,
                    disabledTextColor = Color.Transparent,
                    // backgroundColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
                ),
                keyboardActions = KeyboardActions(onDone = {}),
            )
        }


    }

}

@Preview(showBackground = true)
@Composable
fun EditAlbumPreview() {
    AlbumAppTheme {
        Surface {
            val picker =
                rememberLauncherForActivityResult(contract = ActivityResultContracts.PickVisualMedia()) { uri: Uri? ->
                    if (uri != null) {
                        Log.d("PhotoPicker", "Selected URI: $uri")
                    } else {
                        Log.d("PhotoPicker", "No media selected")
                    }
                }
            EditAlbum(null, picker)
        }
    }
}