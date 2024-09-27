package com.example.albumapp.ui.screens.currentAlbum

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.albumapp.R
import com.example.albumapp.data.AppViewModelProvider
import com.example.albumapp.ui.components.forCurrentAlbum.DisplayElement
import com.example.albumapp.ui.navigation.AppTopBar
import com.example.albumapp.ui.navigation.NavigationDestination
import com.example.albumapp.ui.theme.AlbumAppTheme
import kotlin.math.min

object CurrentAlbumDestination : NavigationDestination {
    override val route = "chosen_album"
    override val titleRes = R.string.home_screen_title
    const val AlbumIdArg = "itemId"
    val routeWithArgs = "$route/{$AlbumIdArg}"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrentAlbum(
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    onEditClick: (Int) -> Unit = {},
    albumViewModel: CurrentAlbumViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val uiState = albumViewModel.pagesUiState
    val coroutineScope = rememberCoroutineScope()
    var albumTitle by rememberSaveable { mutableStateOf("") }

    Scaffold(
        topBar = {
            AppTopBar(
                title = albumTitle,
                navigateBack = navigateBack
            )
        }
    ) { innerpadding ->
        BackHandler { navigateBack() }
        LaunchedEffect(uiState.albumId) {
            albumTitle =
                albumViewModel.findTitle(uiState.albumId)

        }

        CurrentAlbumBody(
            albumUiState = uiState,
            title = albumTitle,
            onEditClick = onEditClick,
            //onItemValueChange = albumViewModel::updateUiState,
            modifier = modifier
                .padding(innerpadding)
                .onSizeChanged { Log.d("NEW SIZE UPPER", "----") }
        )
    }
}

@Composable
fun CurrentAlbumBody(
    albumUiState: CurrentAlbumUiState,
    title: String,
    onEditClick: (Int) -> Unit = {},
    modifier: Modifier = Modifier
) {
    /*todo сделать отображение страниц*/
    var edittingButtonShown = remember { mutableStateOf(true) }
    var pageSize by remember { mutableStateOf(IntSize.Zero) }
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(
                dimensionResource(id = R.dimen.padding_from_edge)
            )
            .clickable(onClick = {
                edittingButtonShown.value = !edittingButtonShown.value
            })
            .onSizeChanged { newsize -> Log.d("NEW SIZE UPPER1", "$newsize") }
    ) {

        /*todo придумать как ещё сказать oops, flag ??*/
        if (albumUiState.pagesMap.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    "Oops, there no pages.\n Press the button to add some.",
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .onSizeChanged { newsize -> Log.d("NEW SIZE UPPER2", "$newsize") }
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                var paddingSizeForPage = (min(pageSize.height, pageSize.width) / 22.0).dp
                Column(
                    modifier = Modifier
                        .onSizeChanged { newsize -> Log.d("NEW SIZE UPPER3", "$newsize") }

                        .aspectRatio(2f / 3f)
                        //.padding(dimensionResource(id = R.dimen.padding_from_edge)) // padding до shadow
                        .shadow(
                            10.dp,
                            shape = RoundedCornerShape(8.dp)
                        ) // shadow с закруглением
                        .clip(RoundedCornerShape(8.dp)) // Clip для правильной тени
                        .background(MaterialTheme.colorScheme.surfaceContainerHighest)


                ) {
                    CurrentAlbumPagesView(
                        elements = albumUiState.pagesMap,
                        modifier = Modifier

                            .fillMaxSize()
                            .padding(paddingSizeForPage)
                            .onSizeChanged { newSize ->
                                pageSize = newSize
                                Log.d("new size", "new size: $newSize,\n pageSize: $pageSize")
                            },
                        pageSize = pageSize
                    )
                }
            }
        }
        Row(
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.Bottom,
            modifier = Modifier.fillMaxSize()
        ) {
            if (edittingButtonShown.value) {
                FloatingActionButton(
                    onClick = { onEditClick(albumUiState.albumId) }
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Edit,
                        contentDescription = "Turn to the AlbumCanvas"
                    )

                }
            }
        }
    }
}


@Composable
fun CurrentAlbumPagesView(
    elements: Map<Int, List<PageElement>>,
    modifier: Modifier = Modifier,
    pageSize: IntSize,

    ) {
    Box(modifier = modifier) {
        elements.forEach { content ->
            val pageNumber = content.key
            content.value.forEach { element ->
                when (element.type) {
                    ElementType.STICKER ->
                        DisplayElement(
                            pageNumber = pageNumber,
                            elementId = element.resourceId,
                            context = LocalContext.current,
                            pageSize = pageSize,
                            element = element,
                        )

                    ElementType.DEFAULT -> {}
                    ElementType.IMAGE -> {}
                    ElementType.TEXT_FIELD -> {}
                }
            }

        }
    }
}

@Preview(showBackground = true)
@Composable
fun ShowAlbumDetailesPreview() {
    AlbumAppTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            Row(modifier = Modifier.fillMaxWidth()) {
                FloatingActionButton(
                    onClick = { }
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Edit,
                        contentDescription = "Turn to the AlbumCanvas"
                    )
                }
            }
        }
    }
}
