package com.example.albumapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.albumapp.ui.navigation.AlbumApp
import com.example.albumapp.ui.theme.AlbumAppTheme

/**
 * AlbumsRepository ->
 * AppContainer ->
 * AppApplication ->
 * AppViewModelProvider ->
 * ViewModel in every screen
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AlbumAppTheme {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                )
                {
                    AlbumApp()
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun LightPreview() {

    AlbumAppTheme {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
        ) { innerPadding ->
            AlbumApp(modifier = Modifier.padding(innerPadding))
        }
    }

}

@Preview(showBackground = true)
@Composable
fun DarkPreview() {
    AlbumAppTheme(darkTheme = true) {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
        ) { innerPadding ->
            AlbumApp(modifier = Modifier.padding(innerPadding))
        }
    }

}