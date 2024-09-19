package com.example.albumapp.ui.components.forNewPage

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.albumapp.R
import com.example.albumapp.ui.components.forHome.SureChoice
import com.example.albumapp.ui.theme.AlbumAppTheme

@Composable
fun SaveChangesModal(
    saveChanges: () -> Unit,
    onNavigateBack: () -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val additionalColor: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
    Dialog(onDismissRequest = onDismissRequest) {
        ElevatedCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = MaterialTheme.shapes.medium,
        ) {
            Box(
                modifier = Modifier.clip(MaterialTheme.shapes.medium)
            ) {
                Column(
                    modifier = Modifier
                        .padding(dimensionResource(id = R.dimen.padding_from_edge))
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.SpaceAround
                ) {
                    SureChoice(
                        color = additionalColor,
                        onYesClick =
                            saveChanges
                        ,
                        onCancelClick = onDismissRequest,
                        onNoClick = onNavigateBack,
                        text = "Do you want to save changes?",
                    )

                }
            }
        }

    }
}

@Preview(showBackground = true)
@Composable
fun SaveChangesQuestionPreview() {
    AlbumAppTheme {
        Column(modifier = Modifier.fillMaxSize()) {
            SaveChangesModal({}, {}, {})
        }
    }
}