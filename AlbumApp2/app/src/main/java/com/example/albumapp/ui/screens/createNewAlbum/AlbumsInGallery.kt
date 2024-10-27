package com.example.albumapp.ui.screens.createNewAlbum

import android.net.Uri
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Surface
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.albumapp.R
import com.example.albumapp.data.AppViewModelProvider
import com.example.albumapp.ui.components.MySpacer
import com.example.albumapp.ui.components.forNewPage.SaveChangesModal
import com.example.albumapp.ui.navigation.AppTopBar
import com.example.albumapp.ui.navigation.NavigationDestination
import com.example.albumapp.ui.theme.AlbumAppTheme
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object CreateNewAlbumDestination : NavigationDestination {
    override val route = "create_new_album_in_gallery"
    override val titleRes = R.string.create_new_album_on_home_screen
}

/*TODO redo colors and to add date of activity*/
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateNewAlbumInGallery(
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    albumsViewModel: AlbumsViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val albumsUiSate = albumsViewModel.albumsUiState

    val openAlertDialog = remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            AppTopBar(
                title = stringResource(id = CreateNewAlbumDestination.titleRes),
                navigateBack = {
                    if (albumsUiSate.isEntryValid) {
                        openAlertDialog.value = true
                    } else {
                        navigateBack()
                    }
                }
            )
        }
    ) { innerpadding ->
        BackHandler {
            if (albumsUiSate.isEntryValid) {
                openAlertDialog.value = !openAlertDialog.value
            } else {
                navigateBack()
            }
        }
        when {
            openAlertDialog.value -> {
                SaveChangesModal(
                    saveChanges = {
                        coroutineScope.launch {
                            albumsViewModel.saveItem(context)
                            openAlertDialog.value = false
                            navigateBack()
                        }
                    },
                    onDismissRequest = { openAlertDialog.value = false },
                    onNavigateBack = {
                        openAlertDialog.value = false
                        navigateBack()
                    })
            }

        }
        EnterAlbumDetails(
            albumUiState = albumsUiSate,
            onItemValueChange = albumsViewModel::updateUiState,
            onSaveClick = {
                coroutineScope.launch {
                    albumsViewModel.saveItem(context)
                    navigateBack()
                }
            },
            modifier = modifier.padding(innerpadding)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnterAlbumDetails(
    albumUiState: AlbumsUiState,
    onItemValueChange: (AlbumsUiState) -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val containerColor = MaterialTheme.colorScheme.primaryContainer

    /**
     * For Image Picker
     */
    var imageUri by rememberSaveable { mutableStateOf<Uri?>(null) }
    val picker =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.PickVisualMedia()) { uri: Uri? ->
            if (uri != null) {
                imageUri = uri
                onItemValueChange(albumUiState.copy(imageCover = uri.toString())) // Обновляем albumImageUri
            } else {
                Log.e("PhotoPicker", "No media selected")
            }
        }


    /*TODO make lazy column*/
    LazyColumn(
        /*TODO change padding to dimens one*/
        modifier = modifier
            .fillMaxSize()
            .padding(10.dp), verticalArrangement = Arrangement.Top
    ) {
        item {
            Box(
                modifier = Modifier
                    .clip(MaterialTheme.shapes.medium)
                    .aspectRatio(2f / 1f)
                    .border(
                        shape = MaterialTheme.shapes.medium,
                        width = 2.dp,
                        color = containerColor
                    )
                    .clickable { picker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }
            ) {
                if (imageUri != null) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(imageUri)
                            .crossfade(enable = true)
                            .build(),
                        contentDescription = "Album Image",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(MaterialTheme.shapes.medium)
                    )
                    IconButton(
                        onClick = {
                            imageUri = null
                            onItemValueChange(albumUiState.copy(imageCover = "")) // Сбрасываем albumImageUri
                        },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                            .shadow(50.dp)
                    ) {
                        Icon(
                            Icons.Rounded.Delete,
                            contentDescription = "Remove Image",
                            //modifier = modifier.border(width = 1.dp, color = MaterialTheme.colorScheme.onSecondaryContainer ),
                            tint = containerColor
                        )
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            Icons.Rounded.Add,
                            contentDescription = "Add Image",
                            modifier = Modifier.size(40.dp),
                            tint = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.6f)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Tap to add image",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.6f)
                        )
                    }
                }
            }
        }
        item { MySpacer() }
        /**
         * Input for title, important for saving
         */
        item {
            OutlinedTextField(
                value = albumUiState.title,
                onValueChange = { onItemValueChange(albumUiState.copy(title = it)) },
                label = { Text(stringResource(R.string.title_for_album_entry)) },
                modifier = Modifier
                    .fillMaxWidth(),

                shape = MaterialTheme.shapes.medium,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = containerColor,
                    unfocusedContainerColor = containerColor,
                    disabledContainerColor = containerColor,
                    focusedBorderColor = containerColor,
                    unfocusedBorderColor = containerColor
                ),
                singleLine = true
            )
        }
        item { MySpacer() }
        /**
         * Input for description, can be omitted
         */
        item {
            OutlinedTextField(
                value = albumUiState.description,
                onValueChange = { onItemValueChange(albumUiState.copy(description = it)) },
                label = { Text(stringResource(id = R.string.descr_for_album_entry)) },
                modifier = Modifier
                    .fillMaxWidth(),

                shape = MaterialTheme.shapes.medium,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = containerColor,
                    unfocusedContainerColor = containerColor,
                    disabledContainerColor = containerColor,
                    focusedBorderColor = containerColor,
                    unfocusedBorderColor = containerColor
                ),
                singleLine = false
            )
        }
        item { MySpacer() }
        /**
         * Input for date
         */
        item {
            DateTimePicker(
                albumUiState = albumUiState,
                onItemValueChange = onItemValueChange
            )
        }
        item { MySpacer() }

        item {
            ElevatedButton(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                enabled = albumUiState.isEntryValid,
                onClick =
                onSaveClick,
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

/*TODO make end date later than start date*/
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateTimePicker(
    albumUiState: AlbumsUiState,
    onItemValueChange: (AlbumsUiState) -> Unit
) {
    /**
     * Date Picker for Start Date (Date of Event)
     */

    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()
    var selectedDate by rememberSaveable { mutableStateOf<Long?>(null) }

    var selectedDateText: String = ""
    if (selectedDate != null) {
        val formattedDate =
            SimpleDateFormat("MM-dd-yyyy", Locale.getDefault()).format(Date(selectedDate!!))
        selectedDateText = formattedDate
        onItemValueChange(albumUiState.copy(dateOfActivity = selectedDateText))
    } else {
        selectedDateText = ""
    }

    TextFieldForDates(
        labelForDate = R.string.date_of_the_event,
        selectedDateText = selectedDateText,
        onIconClick = { showDatePicker = !showDatePicker },
        selected = showDatePicker,
        onDismiss = { showDatePicker = false },
        onSave = {
            selectedDate = datePickerState.selectedDateMillis
            showDatePicker = false
        },
        datePickerState = datePickerState,
    )

    MySpacer()

    /**
     * Extra Date Entry (End Date)
     */

    var chooseEndOfEvent by rememberSaveable { mutableStateOf(false) }
    val greyTextTitle = if (chooseEndOfEvent) {
        stringResource(id = R.string.remove_date_of_end)
    } else {
        stringResource(id = R.string.add_date_of_end)
    }

    if (selectedDateText.isNotEmpty()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextButton(onClick = { chooseEndOfEvent = !chooseEndOfEvent }) {
                Text(text = greyTextTitle)
            }
            TextButton(
                onClick = {
                    onItemValueChange(albumUiState.copy(dateOfActivity = ""))
                    selectedDate = null
                    chooseEndOfEvent = false
                }
            ) {
                Text(text = stringResource(R.string.clear_date_time))
            }
        }
    }

    if (chooseEndOfEvent) {
        var showEndDatePicker by remember { mutableStateOf(false) }
        var selectedEndDate by rememberSaveable { mutableStateOf<Long?>(null) }

        var selectedEndDateText: String = ""
        if (selectedEndDate != null) {
            val formattedEndDate =
                SimpleDateFormat("MM-dd-yyyy", Locale.getDefault()).format(Date(selectedEndDate!!))
            selectedEndDateText = formattedEndDate
            onItemValueChange(albumUiState.copy(endDateOfActivity = selectedEndDateText))
        } else {
            selectedEndDateText = ""
        }

        val endDatePickerState = rememberDatePickerState(
            selectableDates = object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                    return utcTimeMillis > (selectedDate
                        ?: Long.MAX_VALUE) // Разрешаем только даты после выбранной
                }
            }
        )

        TextFieldForDates(
            labelForDate = R.string.end_date_of_event_field,
            selectedDateText = selectedEndDateText,
            onIconClick = { showEndDatePicker = !showEndDatePicker },
            selected = showEndDatePicker,
            onDismiss = { showEndDatePicker = false },
            onSave = {
                selectedEndDate = endDatePickerState.selectedDateMillis
                showEndDatePicker = false
            },
            datePickerState = endDatePickerState,
        )

        MySpacer()

        if (selectedEndDateText.isNotEmpty()) {
            TextButton(
                onClick = {
                    onItemValueChange(albumUiState.copy(endDateOfActivity = ""))
                    selectedEndDate = null
                }
            ) {
                Text(text = stringResource(R.string.clear_date_time))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextFieldForDates(
    @StringRes labelForDate: Int,
    selectedDateText: String,
    onIconClick: () -> Unit,
    selected: Boolean = false,
    onDismiss: () -> Unit,
    onSave: () -> Unit,
    datePickerState: DatePickerState,
) {
    OutlinedTextField(
        value = selectedDateText,
        onValueChange = { },
        label = { Text(stringResource(id = labelForDate)) },
        readOnly = true,
        trailingIcon = {
            IconButton(onClick = onIconClick) {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = "Select date"
                )
            }
        },
        modifier = Modifier
            .fillMaxWidth(),

        shape = MaterialTheme.shapes.medium,
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
            unfocusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
            disabledContainerColor = MaterialTheme.colorScheme.primaryContainer,
            focusedBorderColor = MaterialTheme.colorScheme.primaryContainer,
            unfocusedBorderColor = MaterialTheme.colorScheme.primaryContainer
        ),
        singleLine = true
    )
    if (selected) {
        DatePickerDialog(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            onDismissRequest = onDismiss,
            confirmButton = {
                TextButton(onClick = onSave) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(
                state = datePickerState,
                showModeToggle = false,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EnterAlbumDetailsPreview() {
    AlbumAppTheme {
        EnterAlbumDetails(
            albumUiState = AlbumsUiState(),
            onItemValueChange = {},
            onSaveClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun EnterAlbumDetailsPreviewDark() {
    AlbumAppTheme(darkTheme = true) { // Corrected closing parenthesis
        EnterAlbumDetails(
            albumUiState = AlbumsUiState(),
            onItemValueChange = {},
            onSaveClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HowImageDisplay() {
    val imageUri: Painter = painterResource(id = R.drawable._840x)
    AlbumAppTheme {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .clip(MaterialTheme.shapes.medium)
                    .fillMaxWidth()
                    .aspectRatio(2f / 1f)
                    .border(
                        shape = MaterialTheme.shapes.medium,
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.primaryContainer
                    )
                //.clickable { picker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }
            ) {

                Image(
                    painter = imageUri,
                    contentDescription = "Album Image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(MaterialTheme.shapes.medium)
                )
                Image(
                    painter = imageUri,
                    contentDescription = "Album Image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .blur(radius = 50.dp)
                        .offset(x = 0.dp, y = 0.dp) // Настройте смещение для тонкой настройки
                )
                IconButton(
                    onClick = {
                    },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        //.background(Color.White)
                        .shadow(50.dp)
                ) {
                    Icon(
                        Icons.Rounded.Delete,
                        contentDescription = "Remove Image",
                        //modifier = Modifier.shadow(5.dp),
                        tint = MaterialTheme.colorScheme.primaryContainer
                    )
                }
            }
            IconButton(
                onClick = {
                },
                modifier = Modifier
                    //.align(Alignment.TopEnd)
                    .padding(8.dp)
                    //.background(Color.White)
                    .shadow(50.dp)
            ) {
                Icon(
                    Icons.Rounded.Delete,
                    contentDescription = "Remove Image",
                    //modifier = Modifier.shadow(50.dp),
                    tint = MaterialTheme.colorScheme.primaryContainer
                )
            }
            Surface(
                shape = MaterialTheme.shapes.medium,
                shadowElevation = 5.dp,
                modifier = Modifier
                    .clip(MaterialTheme.shapes.medium)
                    .fillMaxWidth()
                    .aspectRatio(2f / 1f)
                    .border(
                        shape = MaterialTheme.shapes.medium,
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.primaryContainer
                    )
            ) {
                Image(
                    painter = imageUri,
                    contentDescription = "Image with shadow",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}



