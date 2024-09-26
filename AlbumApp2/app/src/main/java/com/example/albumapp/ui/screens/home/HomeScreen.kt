package com.example.albumapp.ui.screens.home


import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.albumapp.R
import com.example.albumapp.data.AppViewModelProvider
import com.example.albumapp.ui.components.forHome.AlbumsOnHomeScreen
import com.example.albumapp.ui.navigation.AppTopBar
import com.example.albumapp.ui.navigation.NavigationDestination
import com.example.albumapp.ui.screens.createNewAlbum.Album
import com.example.albumapp.ui.theme.AlbumAppTheme


object HomeDestination : NavigationDestination {
    override val route = "home"
    override val titleRes = R.string.home_screen_title
}
enum class SortOptions(
    val sortOptionName: String
) {
    CREATE("dateOfCreation"),
    BEGIN_DATE("dateOfActivity"),
    END_DATE("endDateOfActivity"),
    TITLE("title")
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    onEditClick: (Int) -> Unit,
    onCreateNewButtonClick: () -> Unit,
    onAlbumClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {

    val homeUiState = viewModel.homeUiState
    val coroutineScope = rememberCoroutineScope()
    var openSortOptions:Boolean by remember { mutableStateOf(false) }
    var dropSortMenu:Boolean by remember {mutableStateOf(false)}
    var finalChoice
//    var expanded by rememberSaveable {
//        mutableStateOf<Boolean>(true)
//    }
//    var buttonImage: ImageVector = if (expanded) Icons.Rounded.KeyboardArrowDown else
//        Icons.Rounded.KeyboardArrowUp
    Scaffold(topBar = {
        AppTopBar(title = stringResource(id = HomeDestination.titleRes), canNavigateBack = false, modifier = Modifier.clickable { openSortOptions = !openSortOptions })
    }) { innerpadding ->


        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerpadding)
        ) {
            Row(modifier = Modifier.fillMaxWidth()){
                Text("Sort by: ")
                DropdownMenu(
                    expanded = dropSortMenu,
                    onDismissRequest = { dropSortMenu = false },
//                    modifier = Modifier
//                        .width(with(LocalDensity.current){mTextFieldSize.width.toDp()})
                ) {
                    SortOptions.entries.forEach { sortOptionClear ->
                        DropdownMenuItem(
                            onClick = {
                            mSelectedText = label
                                dropSortMenu = false
                        }) {
                            Text(text = label)
                        }
                    }
                }

                /*
                DropdownMenu(
            expanded = mExpanded,
            onDismissRequest = { mExpanded = false },
            modifier = Modifier
                .width(with(LocalDensity.current){mTextFieldSize.width.toDp()})
        ) {
            mCities.forEach { label ->
                DropdownMenuItem(onClick = {
                    mSelectedText = label
                    mExpanded = false
                }) {
                    Text(text = label)
                }
            }
        }
                * */
            }
            viewModel.sortAlbumsListByChoice(sortOption = SortOptions.BEGIN_DATE)
            HomeScreenPadding(
                onEditClick = onEditClick,
                onAlbumClick = onAlbumClick,
                albumList = homeUiState.albumList,
                onPlusButtonClick = onCreateNewButtonClick,
                modifier = modifier
            )
        }
    }

//    Column(
//        modifier = modifier
//            .fillMaxSize()
//            .padding(dimensionResource(id = R.dimen.padding_from_edge)),
//        verticalArrangement = Arrangement.Bottom,
//        horizontalAlignment = Alignment.End
//    ) {
//        if (expanded) {
//            Column() {
//                ColouredButtonWithIcon(
//                    //modifier = modifier,
//                    onClick = onAddButtonClick,
//                    buttonImage = Icons.Rounded.Add,
//                    contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
//                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
//                    contentDescription = "Add new album button"
//                )
//                ColouredButtonWithIcon(
//                    //modifier = modifier,
//                    onClick = { /*TODO*/ },
//                    buttonImage = Icons.Rounded.AccountCircle,
//                    contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
//                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
//                    contentDescription = "View Account Screen"
//                )
//                ColouredButtonWithIcon(
//                    //modifier = modifier,
//                    onClick = { /*TODO*/ },
//                    buttonImage = Icons.Rounded.Settings,
//                    contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
//                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
//                    contentDescription = "Settings Button"
//                )
//            }
//        }
//        Row(
//            modifier = modifier
//                .fillMaxWidth(),
//            horizontalArrangement = Arrangement.End
//        ) {
//            ColouredButtonWithIcon(
//                //modifier = modifier,
//                onClick = { expanded = !expanded },
//                buttonImage = buttonImage,
//                contentColor = MaterialTheme.colorScheme.onTertiary,
//                containerColor = MaterialTheme.colorScheme.tertiary,
//                contentDescription = "Add button"
//            )
//
//        }
//    }
}

@Composable
fun HomeScreenPadding(
    onEditClick: (Int) -> Unit,
    onAlbumClick: (Int) -> Unit,
    albumList: List<Album>,
    onPlusButtonClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlbumsOnHomeScreen(
        onEditClick = onEditClick,
        onAlbumClick = onAlbumClick,
        albumList = albumList,
        onPlusButtonClick = onPlusButtonClick,
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    AlbumAppTheme {
        HomeScreenPadding({}, {},
            listOf(
                Album(0, "Egypt", "user", "", "", "", "", ""),
                Album(1, "France", "user", "my trip to Paris", "", "", "", "")
            ), {})
    }
}