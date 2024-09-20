package com.example.albumapp.ui.components.forHome

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.albumapp.R
import com.example.albumapp.data.AppViewModelProvider
import com.example.albumapp.ui.screens.createNewAlbum.AlbumsViewModel
import com.example.albumapp.ui.theme.AlbumAppTheme
import kotlinx.coroutines.launch


@Composable
fun MinimalDialog(
    onEditClick: (Int) -> Unit,
    onDismissRequest: () -> Unit,
    elementId: Int,
    elementTitle: String,
    elementDesc: String,
    elementDate: String,
    elementEndDate: String,
    modifier: Modifier = Modifier,
    albumsViewModel: AlbumsViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    /*TODO add edit button */
    val coroutineScope = rememberCoroutineScope()
    val openEditingButton = remember {
        mutableStateOf(false)
    }
    val openSureDelete = remember {
        mutableStateOf(false)
    }
    Dialog(onDismissRequest = onDismissRequest) {
        ElevatedCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = MaterialTheme.shapes.medium,
        ) {
            Box(
                modifier = Modifier
                    .clip(MaterialTheme.shapes.medium)
                    .clickable(onClick = { openEditingButton.value = !openEditingButton.value })

            ) {
                Column(
                    modifier = modifier
                        .padding(dimensionResource(id = R.dimen.padding_from_edge))
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.SpaceAround
                ) {
                    if (elementDate != "") {
                        val text: String =
                            if (elementEndDate != "") "$elementDate - $elementEndDate" else elementDate
                        Text(
                            text = text,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .fillMaxSize(),
                            textAlign = TextAlign.Center,
                        )
                    }
                    Text(
                        text = elementTitle,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier

                            .fillMaxSize(),
                        textAlign = TextAlign.Center,
                    )
                    if (elementDesc != "") {
                        Spacer(modifier = modifier.padding(dimensionResource(id = R.dimen.padding_from_edge)))
                        Text(
                            text = elementDesc,
                            modifier = Modifier
                                .fillMaxSize(),
                            textAlign = TextAlign.Center,
                        )
                    }
                    val additionalColor: Color =
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    if (openEditingButton.value) {
                        EditingMenu(
                            elementId = elementId,
                            onEditClick = onEditClick,
                            color = additionalColor,
                            deleteClick = { openSureDelete.value = !openSureDelete.value },
                            onDismissRequest = { openEditingButton.value = false })
                        if (openSureDelete.value) {
                            SureChoice(
                                color = additionalColor,
                                onYesClick = {
                                    coroutineScope.launch {
                                        albumsViewModel.deleteAlbum(
                                            elementId
                                        )
                                    }
                                },
                                onNoClick = { openSureDelete.value = !openSureDelete.value },
                                text = "Are you sure to delete the album?\nYou won't have an ability to recover it."
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EditingMenu(
    elementId: Int,
    onEditClick: (Int) -> Unit,
    color: Color,
    deleteClick: () -> Unit,
    onDismissRequest: () -> Unit
) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        IconButton(onClick = { onEditClick(elementId) }) {
            Icon(
                imageVector = Icons.Rounded.Edit,
                contentDescription = "Edit the description of album",
                tint = color
            )
        }
        IconButton(onClick = deleteClick) {
            Icon(
                imageVector = Icons.Rounded.Delete,
                contentDescription = "Delete the album",
                tint = color
            )
        }
    }

}

@Composable
fun SureChoice(
    color: Color, onYesClick: () -> Unit, onNoClick: () -> Unit, onCancelClick: () -> Unit = {},
    text: String
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text, textAlign = TextAlign.Center, color = color)
        Row() {
            TextButton(onClick = onYesClick) {
                Text(stringResource(R.string.yes))
            }
            TextButton(onClick = onNoClick) {
                Text(stringResource(R.string.no))
            }
            if (onCancelClick != {}) {
                TextButton(onClick = onCancelClick) {
                    Text(stringResource(R.string.cancel))
                }
            }
        }
    }

}

@Preview(showBackground = true)
@Composable
fun ModalPreview() {
    AlbumAppTheme() {
        Surface(modifier = Modifier.fillMaxSize()) {

//            MinimalDialog(
//                onDismissRequest = { /*TODO*/ },
//                elementTitle = "Example",
//                elementDesc = "aaaaaaaaaa",
//                elementDate = "10-10-2022",
//                elementEndDate = ""
//            )

        }
    }
}