package com.example.albumapp.ui.screens

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.albumapp.R
import com.example.albumapp.ui.components.ColouredButtonWithIcon
import com.example.albumapp.ui.components.EditAlbum
import com.example.albumapp.ui.theme.AlbumAppTheme


@Composable
fun AddAlbum(onHomeScreen: () -> Unit, modifier: Modifier = Modifier) {
    var imageUri by rememberSaveable { mutableStateOf<Uri?>(null) }
    val picker =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.PickVisualMedia()) { uri: Uri? ->
            if (uri != null) {
                Log.d("PhotoPicker", "Selected URI: $uri")
                imageUri = uri
            } else {
                Log.d("PhotoPicker", "No media selected")
            }
        }
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(dimensionResource(id = R.dimen.padding_from_edge))
    ) {
        EditAlbum(imageUri, picker)
        Column(
            modifier = modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Bottom
        ) {

            Row(
                modifier = modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ColouredButtonWithIcon(
                    modifier = modifier,
                    onClick = onHomeScreen,
                    buttonImage = Icons.Rounded.Close,
                    contentColor = MaterialTheme.colorScheme.onTertiary,
                    containerColor = MaterialTheme.colorScheme.tertiary,
                    contentDescription = "Cancel adding"
                )
                ElevatedButton(
                    modifier = modifier
                        .height(dimensionResource(id = R.dimen.height_for_button)),
                    colors = ButtonDefaults.elevatedButtonColors(
                        containerColor = Color.Transparent,
                        contentColor = MaterialTheme.colorScheme.tertiary
                    ),

                    //elevation = ButtonDefaults.elevatedButtonElevation(5.dp, ),
                    onClick = { picker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) })
                {
                    Text(text = "Pick Image")
                }
                ColouredButtonWithIcon(
                    modifier = modifier,
                    onClick = onHomeScreen,
                    buttonImage = Icons.Rounded.Check,
                    contentColor = MaterialTheme.colorScheme.onTertiary,
                    containerColor = MaterialTheme.colorScheme.tertiary,
                    contentDescription = "Save adding"
                )
            }
        }
    }

}

@Composable
fun PickPhotoOrVideo(imageUri: Uri?) {


}

@Preview(showBackground = true)
@Composable
fun NewAlbumPreview() {
    AlbumAppTheme {
        AddAlbum({})
    }
}

