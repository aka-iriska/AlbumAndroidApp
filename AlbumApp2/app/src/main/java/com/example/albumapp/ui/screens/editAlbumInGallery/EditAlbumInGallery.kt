@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.albumapp.ui.screens.editAlbumInGallery

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.albumapp.R
import com.example.albumapp.data.AppViewModelProvider
import com.example.albumapp.ui.components.MySpacer
import com.example.albumapp.ui.navigation.AppTopBar
import com.example.albumapp.ui.navigation.NavigationDestination
import com.example.albumapp.ui.screens.createNewAlbum.AlbumsUiState
import com.example.albumapp.ui.screens.createNewAlbum.TextFieldForDates
import com.example.albumapp.ui.theme.AlbumAppTheme
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object EditAlbumInGalleryDestination : NavigationDestination {
    override val route = "edit_chosen_album"
    override val titleRes = R.string.edit_album_main_info
    const val AlbumIdArg = "itemId"
    val routeWithArgs = "$route/{$AlbumIdArg}"
}

@Composable
fun EditAlbumInGallery(
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: EditAlbumInGalleryViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val uiState = viewModel.albumsUiState
    val coroutineScope = rememberCoroutineScope()
    var context = LocalContext.current
    Scaffold(topBar = {
        AppTopBar(
            title = "Edit ${uiState.title}", navigateBack = navigateBack
        )
    }) { innerpadding ->

        EditAlbumInGalleryBody(
            albumUiState = uiState,
            onAlbumValueChange = viewModel::updateUiState,
            onSaveClick = {
                coroutineScope.launch {
                    viewModel.updateAlbum(context)
                    navigateBack()
                }
            },
            modifier = modifier
                .padding(innerpadding)
                .padding(dimensionResource(id = R.dimen.padding_from_edge)),

            )
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditAlbumInGalleryBody(
    albumUiState: AlbumsUiState,
    onAlbumValueChange: (AlbumsUiState) -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var containerColor = MaterialTheme.colorScheme.primaryContainer
    var addDescription by remember { mutableStateOf(albumUiState.description != "") }
    var addDateOfActivity by remember { mutableStateOf(albumUiState.dateOfActivity != "") }
    LazyColumn(modifier = modifier) {
        item {
            MyTextField(
                value = albumUiState.title,
                label = stringResource(id = R.string.title_for_album_entry),
                onValueChange = { onAlbumValueChange(albumUiState.copy(title = it)) },
                ContainerColor = containerColor
            )
        }
        item { MySpacer() }
        if (albumUiState.description != "" || addDescription) {
            item {
                MyTextField(
                    value = albumUiState.description,
                    label = stringResource(id = R.string.descr_for_album_entry),
                    onValueChange = { onAlbumValueChange(albumUiState.copy(description = it)) },
                    ContainerColor = containerColor,
                    singleLine = false
                )
            }
            item { MySpacer() }
        }
        if (albumUiState.dateOfActivity != "" || addDateOfActivity) {
            item {
                DateTimePickerForEdit(
                    albumUiState = albumUiState,
                    onItemValueChange = onAlbumValueChange
                )
            }
        }
//        item {
//            DateTimePicker(
//                albumUiState = albumUiState,
//                onItemValueChange = onAlbumValueChange
//            )
//        }
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (!addDescription) TextButton(onClick = {
                    addDescription = true
                }) { Text("Add description") }
                if (!addDateOfActivity) TextButton(onClick = {
                    addDateOfActivity = true
                }) { Text("Add date of the event") }
            }

        }
        item { MySpacer() }
        item {
            ElevatedButton(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                enabled = albumUiState.isEntryValid,
                onClick = onSaveClick,
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = containerColor,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                )
            ) {
                Text("Save")
            }
        }
    }
}

@Composable
fun DateTimePickerForEdit(
    albumUiState: AlbumsUiState,
    onItemValueChange: (AlbumsUiState) -> Unit,
) {
    /**
     * For Date Picker
     */
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    var selectedDateText: String = albumUiState.dateOfActivity

    TextFieldForDates(
        labelForDate = R.string.date_of_the_event,
        selectedDateText = selectedDateText,
        onIconClick = { showDatePicker = !showDatePicker },
        selected = showDatePicker,
        onDismiss = { showDatePicker = false },
        onSave = {
            selectedDateText = SimpleDateFormat(
                "MM-dd-yyyy",
                Locale
                    .getDefault()
            )
                .format(Date(datePickerState.selectedDateMillis!!))
            onItemValueChange(albumUiState.copy(dateOfActivity = selectedDateText))
            showDatePicker = false
        },
        datePickerState = datePickerState
    )
    MySpacer()
    /**
     * for extra date entry
     */
    var chooseEndOfEvent by rememberSaveable {
        mutableStateOf<Boolean>(false)
    }
    var greyTextTitle: String =
        if (chooseEndOfEvent) stringResource(id = R.string.remove_date_of_end) else stringResource(
            id = R.string.add_date_of_end
        )
    var selectedEndDateText: String = albumUiState.endDateOfActivity
    if (selectedDateText != "" && selectedEndDateText == "") {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextButton(
                onClick = {
                    onItemValueChange(albumUiState.copy(endDateOfActivity = ""))
                    chooseEndOfEvent = !chooseEndOfEvent
                },
            ) { Text(text = greyTextTitle) }
            TextButton(
                onClick = {
                    onItemValueChange(albumUiState.copy(dateOfActivity = ""))
                    selectedDateText = ""
                    chooseEndOfEvent = false
                },
            ) { Text(text = stringResource(R.string.clear_date_time)) }
        }
        MySpacer()
    }

    if (chooseEndOfEvent || selectedEndDateText != "") {
        TextFieldForDates(
            labelForDate = R.string.end_date_of_event_field,
            selectedDateText = selectedEndDateText,
            onIconClick = { showDatePicker = !showDatePicker },
            selected = showDatePicker,
            onDismiss = { showDatePicker = false },
            onSave = {
                onItemValueChange(
                    albumUiState.copy(
                        endDateOfActivity = SimpleDateFormat(
                            "MM-dd-yyyy",
                            Locale.getDefault()
                        ).format(datePickerState.selectedDateMillis!!)
                    )
                )
                showDatePicker = false
            },
            datePickerState = datePickerState
        )
        MySpacer()
        if (selectedEndDateText != "") {
            Row(horizontalArrangement = Arrangement.End) {
                TextButton(
                    onClick = {
                        onItemValueChange(albumUiState.copy(endDateOfActivity = ""))
                        selectedEndDateText = ""
                    },
                ) { Text(text = stringResource(R.string.clear_date_time)) }
            }
        }

    }


}

@Composable
fun MyTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    ContainerColor: Color,
    singleLine: Boolean = true
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = ContainerColor,
            unfocusedContainerColor = ContainerColor,
            disabledContainerColor = ContainerColor,
            focusedBorderColor = ContainerColor,
            unfocusedBorderColor = ContainerColor
        ),
        singleLine = singleLine
    )
}

@Preview(showBackground = true)
@Composable
fun TitleOnlyPreview() {
    AlbumAppTheme {
        Column(modifier = Modifier.fillMaxSize()) {
            EditAlbumInGalleryBody(
                albumUiState = AlbumsUiState(1, "Example", "", "", "", "", "", "", true),
                onAlbumValueChange = {},
                onSaveClick = { /*TODO*/ })
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TitleDescOnlyPreview() {
    AlbumAppTheme {
        Column(modifier = Modifier.fillMaxSize()) {
            EditAlbumInGalleryBody(
                albumUiState = AlbumsUiState(1, "Example", "AAAA", "", "", "", "", "", true),
                onAlbumValueChange = {},
                onSaveClick = { /*TODO*/ })
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TitleDescEventOnlyPreview() {
    AlbumAppTheme {
        Column(modifier = Modifier.fillMaxSize()) {
            EditAlbumInGalleryBody(
                albumUiState = AlbumsUiState(
                    1,
                    "Example",
                    "AAAA",
                    "",
                    "",
                    "12-12-2022",
                    "",
                    "",
                    true
                ),
                onAlbumValueChange = {},
                onSaveClick = { /*TODO*/ })
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TitleEventOnlyPreview() {
    AlbumAppTheme {
        Column(modifier = Modifier.fillMaxSize()) {
            EditAlbumInGalleryBody(
                albumUiState = AlbumsUiState(1, "Example", "", "", "", "12-12-2022", "", "", true),
                onAlbumValueChange = {},
                onSaveClick = { /*TODO*/ })
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TitleEventEndOnlyPreview() {
    AlbumAppTheme {
        Column(modifier = Modifier.fillMaxSize()) {
            EditAlbumInGalleryBody(
                albumUiState = AlbumsUiState(
                    1,
                    "Example",
                    "",
                    "",
                    "",
                    "12-12-2022",
                    "22-12-2022",
                    "",
                    true
                ),
                onAlbumValueChange = {},
                onSaveClick = { /*TODO*/ })
        }
    }
}

@Preview(showBackground = true)
@Composable
fun allBodyPreview() {
    AlbumAppTheme {
        Column(modifier = Modifier.fillMaxSize()) {
            EditAlbumInGalleryBody(
                albumUiState = AlbumsUiState(
                    1,
                    "Example",
                    "AAAA",
                    "",
                    "",
                    "12-12-2022",
                    "13-12-2022",
                    "",
                    true
                ),
                onAlbumValueChange = {},
                onSaveClick = { /*TODO*/ })
        }
    }
}