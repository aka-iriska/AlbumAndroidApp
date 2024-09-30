package com.example.albumapp.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.albumapp.SplashScreen
import com.example.albumapp.SplashScreenDestination
import com.example.albumapp.ui.screens.createNewAlbum.CreateNewAlbumDestination
import com.example.albumapp.ui.screens.createNewAlbum.CreateNewAlbumInGallery
import com.example.albumapp.ui.screens.createNewPages.CreateNewPages
import com.example.albumapp.ui.screens.createNewPages.CreateNewPagesDestination
import com.example.albumapp.ui.screens.currentAlbum.CurrentAlbum
import com.example.albumapp.ui.screens.currentAlbum.CurrentAlbumDestination
import com.example.albumapp.ui.screens.editAlbumInGallery.EditAlbumInGallery
import com.example.albumapp.ui.screens.editAlbumInGallery.EditAlbumInGalleryDestination
import com.example.albumapp.ui.screens.home.HomeDestination
import com.example.albumapp.ui.screens.home.HomeScreen


@Composable
fun AlbumApp(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = HomeDestination.route
    ) {
        composable(route = SplashScreenDestination.route) {
            SplashScreen(navController = navController)
        }
        composable(route = HomeDestination.route) {
            HomeScreen(
                onEditClick = { navController.navigate("${EditAlbumInGalleryDestination.route}/$it") },
                onCreateNewButtonClick = { navController.navigate(CreateNewAlbumDestination.route) },
                onAlbumClick = { navController.navigate("${CurrentAlbumDestination.route}/$it") },
            )
        }
        composable(route = CreateNewAlbumDestination.route) {
            CreateNewAlbumInGallery(navigateBack = { navController.navigateUp() })
        }
        composable(
            route = EditAlbumInGalleryDestination.routeWithArgs,
            arguments = listOf(navArgument(EditAlbumInGalleryDestination.AlbumIdArg) {
                type = NavType.IntType
            })
        )
        {
            EditAlbumInGallery(navigateBack = { navController.navigateUp() })
        }
        composable(
            route = CurrentAlbumDestination.routeWithArgs,
            arguments = listOf(navArgument(CurrentAlbumDestination.AlbumIdArg) {
                type = NavType.IntType
            })
        ) {
            CurrentAlbum(navigateBack = { navController.navigate(HomeDestination.route) },
                onEditClick = { navController.navigate("${CreateNewPagesDestination.route}/$it") }
            )
//                ItemDetailsScreen(
//                    navigateToEditItem = { navController.navigate("${ItemEditDestination.route}/$it") },
//                    navigateBack = { navController.navigateUp() }
//                )
        }
        composable(
            route = CreateNewPagesDestination.routeWithArgs,
            arguments = listOf(navArgument(CreateNewPagesDestination.AlbumIdArg) {
                type = NavType.IntType
            })
        ) {
            CreateNewPages(navigateBack = { navController.navigate("${CurrentAlbumDestination.route}/$it") })
        }
//            composable(route = AlbumsScreen.ChangeSettings.name) {
//                /*TODO add screen*/
//            }
//            composable(route = AlbumsScreen.ShowAccount.name) {
//                /*TODO add screen*/
//            }
    }

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    title: String,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    navigateBack: () -> Unit = {},
    canNavigateBack: Boolean = true,
    modifier: Modifier = Modifier
) {
    Surface(shadowElevation = 10.dp, modifier = modifier) {
        CenterAlignedTopAppBar(
            title = {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold
                )
            },
            navigationIcon = {
                if (canNavigateBack) {
                    IconButton(onClick = navigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Back to the previous screen"
                        )
                    }
                }
            },
            scrollBehavior = scrollBehavior,
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.background.copy(
                    alpha = 0.5f
                )
            )
        )
    }
}