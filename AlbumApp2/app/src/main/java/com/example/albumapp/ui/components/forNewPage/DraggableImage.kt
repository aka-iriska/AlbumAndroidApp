package com.example.albumapp.ui.components.forNewPage

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.example.albumapp.ui.screens.createNewPages.PageElement

@Composable
fun DraggableImage(
    imageId:Int,
    context: Context = LocalContext.current,
    element: PageElement,
    onElementUpdate:(PageElement)->Unit
){

}