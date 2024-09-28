package com.example.albumapp.ui.screens.currentAlbum

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
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
import com.example.albumapp.ui.components.forCountPages.ShowActivePageByRadioButton
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
                .padding(innerpadding),
            updateCurrentPage = albumViewModel::updateCurrentPage
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CurrentAlbumBody(
    albumUiState: CurrentAlbumUiState,
    title: String,
    onEditClick: (Int) -> Unit = {},
    modifier: Modifier = Modifier,
    updateCurrentPage: (Int) -> Unit
) {
    var addedElements = albumUiState.pagesMap
    /*todo сделать отображение страниц*/
    var edittingButtonShown = remember { mutableStateOf(true) }
    var pageSize by remember { mutableStateOf(IntSize.Zero) }

    var pageNumber = albumUiState.pageNumber

    Box(
        modifier = modifier
            .fillMaxSize()
            .clickable(onClick = {
                edittingButtonShown.value = !edittingButtonShown.value
            })
    ) {

        /*todo придумать как ещё сказать oops, flag ??*/
        if (pageNumber == 0) {
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
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                var paddingSizeForPage = (min(pageSize.height, pageSize.width) / 22.0).dp
                val pagerState = rememberPagerState(pageCount = { pageNumber })
                HorizontalPager(state = pagerState) { _ ->
                    LaunchedEffect(pagerState.settledPage) {
                        updateCurrentPage(pagerState.settledPage + 1)
                    }
                    Column(
                        modifier = Modifier
                            .aspectRatio(2f / 3f)
                            .padding(dimensionResource(id = R.dimen.padding_from_edge)) // padding до shadow
                            .shadow(
                                10.dp,
                                shape = RoundedCornerShape(8.dp)
                            ) // shadow с закруглением
                            .clip(RoundedCornerShape(8.dp)) // Clip для правильной тени
                            .background(MaterialTheme.colorScheme.surfaceContainerHighest)


                    ) {
                        CurrentAlbumPagesView(
                            elements = addedElements.getOrDefault(
                                albumUiState.currentPage,
                                emptyList()
                            ),
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(paddingSizeForPage)
                                .onSizeChanged { newSize ->
                                    pageSize = newSize
                                },
                            pageSize = pageSize,
                            currentPage = albumUiState.currentPage
                        )
                    }
                }
                ShowActivePageByRadioButton(
                    pagerState,
                    Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }
        Row(
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.Bottom,
            modifier = Modifier
                .fillMaxSize()
                .padding(dimensionResource(R.dimen.padding_from_edge))
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
    elements: List<PageElement>,
    modifier: Modifier = Modifier,
    pageSize: IntSize,
    currentPage: Int,
) {
    Box(modifier = modifier) {
        elements.forEach { element ->
            when (element.type) {
                ElementType.STICKER ->
                    DisplayElement(
                        pageNumber = currentPage,
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


@Preview(showBackground = true)
@Composable
fun ShowAlbumDetailsPreview() {
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
