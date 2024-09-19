package com.example.albumapp.ui.screens.currentAlbum

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.albumapp.R
import com.example.albumapp.data.AppViewModelProvider
import com.example.albumapp.ui.navigation.AppTopBar
import com.example.albumapp.ui.navigation.NavigationDestination
import com.example.albumapp.ui.theme.AlbumAppTheme

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
    val uiState = albumViewModel.uiState.collectAsState()
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
        LaunchedEffect(uiState.value.albumId) {
            albumTitle =
                albumViewModel.findTitle(uiState.value.albumId)

        }

        CurrentAlbumBody(
            albumUiState = uiState.value,
            title = albumTitle,
            onEditClick = onEditClick,
            //onItemValueChange = albumViewModel::updateUiState,
            modifier = modifier.padding(innerpadding)
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
    var edittingButtonShown = remember { mutableStateOf(true) }
    Box(
        modifier = modifier

            .clickable(onClick = {
                edittingButtonShown.value = !edittingButtonShown.value
            })
    ) {
        Box(
            modifier = Modifier.padding(
                dimensionResource(id = R.dimen.padding_from_edge)
            )
        ) {
            if (albumUiState.pagesMap.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        "Oops, there no pages.\n Press the button to add some.",
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
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
}

@Preview(showBackground = true)
@Composable
fun ShowAlbumDetailesPreview() {
    AlbumAppTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            CurrentAlbumBody(albumUiState = CurrentAlbumUiState(), title = "Example")
        }
    }
}
