package com.example.albumapp.ui.components.forHome

import android.net.Uri
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.albumapp.ui.screens.createNewAlbum.Album


@Composable
fun AlbumsOnHomeScreen(
    onEditClick: (Int) -> Unit,
    onAlbumClick: (Int) -> Unit,
    onPlusButtonClick: () -> Unit,
    modifier: Modifier = Modifier,
    albumList: List<Album> = emptyList()
) {
    Column(
        modifier = modifier
            .padding(10.dp)
            .fillMaxSize()
    ) {
        AlbumsCards(
            onEditClick = onEditClick,
            onAlbumClick = onAlbumClick,
            albumList,
            onPlusButtonClick = onPlusButtonClick,
            modifier
        )
    }
}

@Composable
fun AlbumsCards(
    onEditClick: (Int) -> Unit,
    onAlbumClick: (Int) -> Unit,
    albumList: List<Album>,
    onPlusButtonClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    //Text("Title")
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 128.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        item {
            ElevatedCard(
                modifier = modifier
                    .aspectRatio(5f / 3f),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                    contentColor = MaterialTheme.colorScheme.onSurface
                ),
                shape = MaterialTheme.shapes.medium
            ) {

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = modifier.fillMaxSize()
                ) {

                    IconButton(
                        onClick = onPlusButtonClick,
                        modifier = modifier
                            .fillMaxSize(1f / 3f) // Устанавливаем размер в 1/3 от размеров карточки
                    ) {
                        Icon(
                            imageVector = Icons.Filled.AddCircle,
                            contentDescription = "AddNewAlbum",
                            modifier = Modifier.fillMaxSize() // Иконка заполняет всю доступную область внутри IconButton
                        )
                    }
                }
            }
        }

        if (!albumList.isEmpty()) {
            items(albumList, key = { it.id }) { album ->
                EveryAlbumCard(
                    onEditClick = onEditClick,
                    onAlbumClick = onAlbumClick,
                    album,
                    modifier
                )
            }
        }
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun EveryAlbumCard(
    onEditClick: (Int) -> Unit,
    onAlbumClick: (Int) -> Unit,
    albumElement: Album,
    modifier: Modifier = Modifier
) {
    val imageUriString = albumElement.imageCover
    val imageUri: Uri? = if (imageUriString.isNotEmpty()) Uri.parse(imageUriString) else null
    val openAlertDialog = remember { mutableStateOf(false) }

    ElevatedCard(
        modifier = modifier
            .aspectRatio(5f / 3f),

        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        shape = MaterialTheme.shapes.medium
    ) {
        Box(
            modifier = Modifier
                .clip(MaterialTheme.shapes.medium)
                .combinedClickable(
                    onLongClick = { openAlertDialog.value = true },
                    onClick = { onAlbumClick(albumElement.id) }),
        ) {
            if (imageUri != null) {
                Image(
                    painter = rememberAsyncImagePainter(albumElement.imageCover),
                    contentDescription = "Album Cover",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
            FrameForAlbum()
            Column(
                modifier = modifier
                    .padding(10.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.Start
            ) {

                Text(
                    text = albumElement.title,//.uppercase(),
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    //maxLines = 1,
                    modifier = modifier.verticalScroll(rememberScrollState())
                )
            }
        }
    }
    when {
        openAlertDialog.value -> {
            MinimalDialog(
                onEditClick = {
                    onEditClick(it)
                    openAlertDialog.value = false
                },
                onDismissRequest = { openAlertDialog.value = false },
                elementId = albumElement.id,
                elementTitle = albumElement.title,
                elementDesc = albumElement.description,
                elementDate = albumElement.dateOfActivity,
                elementEndDate = albumElement.endDateOfActivity
            )
        }
    }

}

