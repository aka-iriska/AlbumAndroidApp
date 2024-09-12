package com.example.albumapp.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.albumapp.R
import com.example.albumapp.ui.theme.AlbumAppTheme

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AlbumPresent(modifier: Modifier = Modifier) {
    val pagerState = rememberPagerState(pageCount = {
        10
    })
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    )
    {
        HorizontalPager(state = pagerState) { page ->
            ColouredCard(content = {
                Column(
                    modifier = modifier
                        .padding(dimensionResource(id = R.dimen.padding_for_album))
                ) {
                    Text("Album preview $page")
                    /*TODO rechange to image of album
                *  add settings with showing one page or two*/
                    Image(
                        painter = painterResource(id = R.drawable._840x),
                        contentDescription = "Picture",
                    )
                }
            })
//            Card(
//                elevation = CardDefaults.cardElevation(10.dp),
//                shape = RectangleShape,
//                modifier = modifier
//                    .fillMaxSize()
//                    .padding(horizontal = 10.dp)
//                    .aspectRatio(3f / 4f),
//                colors = CardDefaults.cardColors(
//                    containerColor = MaterialTheme.colorScheme.background,
//                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
//                )
//            ) {
//
//                Column(
//                    modifier = modifier
//                        .padding(20.dp)
//                ) {
//                    Text("Album preview $page")
//                    /*TODO rechange to image of album
//                *  add settings with showing one page or two*/
//                    Image(
//                        painter = painterResource(id = R.drawable._840x),
//                        contentDescription = "Picture",
//                    )
//                }
//            }

        }
    }
}

@Preview(showBackground = true)
@Composable
fun AlbumPreview() {
    AlbumAppTheme() {
        AlbumPresent()
    }
}