package com.example.albumapp.ui.components.forHome

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.albumapp.ui.theme.AlbumAppTheme

@Composable
fun FrameForAlbum(
    modifier: Modifier = Modifier,
    FrameColor: Color = MaterialTheme.colorScheme.surfaceContainerLow,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color.Unspecified,
                        FrameColor.copy(alpha = 0.7f)
                    )
                )
            )
    ) {}
}

@Preview(showBackground = true)
@Composable
fun FramePreview() {
    AlbumAppTheme {
        Surface(modifier = Modifier.fillMaxSize()) {

            Card(
                modifier = Modifier
                    .width(128.dp)
                    .aspectRatio(5f / 3f)
                    .padding(10.dp)
            ) {
                FrameForAlbum()
            }

        }
    }
}