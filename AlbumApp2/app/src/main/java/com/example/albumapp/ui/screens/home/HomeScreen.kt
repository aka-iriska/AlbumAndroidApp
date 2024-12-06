package com.example.albumapp.ui.screens.home


import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
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
    CREATE("Album date of creation"),
    BEGIN_DATE("Date of event"),
    END_DATE("End date of event"),
    TITLE("Title")
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

    val homeUiState = viewModel.homeUiState.collectAsState()
    var openSortOptions: Boolean by remember { mutableStateOf(false) }
    var dropSortMenu: Boolean by remember { mutableStateOf(false) }
    var finalChoice: SortOptions by rememberSaveable { mutableStateOf(SortOptions.CREATE) }

    var lastBackPressedTime by remember { mutableStateOf(0L) }

    val context = LocalContext.current

    Scaffold(topBar = {
        AppTopBar(
            title = stringResource(id = HomeDestination.titleRes),
            canNavigateBack = false,
            modifier = Modifier.clickable { openSortOptions = !openSortOptions })
    }) { innerpadding ->
        BackHandler {
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastBackPressedTime < 3000) {
                // Если нажали "Назад" дважды за 3 секунды, выходим из приложения
                (context as? Activity)?.finish()
            } else {
                // Если это первое нажатие, показываем сообщение и обновляем время
                lastBackPressedTime = currentTime
                Toast.makeText(context, "Нажмите ещё раз для выхода", Toast.LENGTH_SHORT).show()
            }
        }
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerpadding)
        ) {
            if (openSortOptions) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text("Sort by:", fontSize = 15.sp)
                        Row {
                            TextButton(
                                onClick = {
                                    dropSortMenu = !dropSortMenu
                                },
                                colors = ButtonColors(
                                    containerColor = Color.Unspecified,
                                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                    disabledContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                    disabledContainerColor = Color.Unspecified
                                )
                            ) {
                                Text(
                                    text = finalChoice.sortOptionName,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 15.sp,

                                    )
                                Icon(
                                    Icons.Rounded.ArrowDropDown,
                                    contentDescription = "open sort options"
                                )
                            }
                            DropdownMenu(
                                expanded = dropSortMenu,
                                onDismissRequest = { dropSortMenu = false },
                            ) {
                                SortOptions.entries.forEach { sortOptionClear ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                text = sortOptionClear.sortOptionName,
                                                fontSize = 15.sp
                                            )
                                        },
                                        onClick = {
                                            finalChoice = sortOptionClear
                                            dropSortMenu = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        /*todo for future account*/
                        /*IconButton(onClick = {}) {
                            Icon(
                                Icons.Rounded.AccountCircle,
                                "view account screen"
                            )
                        }*/
                        IconButton(onClick = {}) {
                            Icon(
                                Icons.Outlined.Settings,
                                "view settings screen"
                            )
                        }
                    }
                }
            }
            viewModel.sortAlbumsListByChoice(sortOption = finalChoice)
            HomeScreenPadding(
                onEditClick = onEditClick,
                onAlbumClick = onAlbumClick,
                albumList = homeUiState.value.albumList,
                onPlusButtonClick = onCreateNewButtonClick,
                modifier = modifier
            )
        }
    }
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