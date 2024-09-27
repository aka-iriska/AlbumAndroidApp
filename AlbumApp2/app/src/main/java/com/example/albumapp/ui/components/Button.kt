package com.example.albumapp.ui.components


import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.albumapp.R
import com.example.albumapp.ui.theme.AlbumAppTheme

@Composable
fun ColouredButtonWithIcon(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    buttonImage: ImageVector,
    contentColor: Color = Color.Unspecified,
    containerColor: Color = Color.Unspecified,
    contentDescription: String
) {
    TextButton(
        modifier = modifier
            //.fillMaxSize()
            .height(dimensionResource(id = R.dimen.height_for_button))
            .width(dimensionResource(id = R.dimen.height_for_button))
            .padding(3.dp),
        //.shadow(30.dp, shape = RoundedCornerShape(50)),
        colors = ButtonDefaults.buttonColors(
            contentColor = contentColor,
            containerColor = containerColor.copy(alpha = 0.5f)
        ),
        onClick = onClick,
        elevation = ButtonDefaults.elevatedButtonElevation(50.dp)
    ) {
        Icon(
            imageVector = buttonImage,
            //tint = MaterialTheme.colorScheme.onSecondaryContainer,
            //modifier = modifier.fillMaxSize(),
            contentDescription = contentDescription
        )
    }
}

@Composable
fun ColouredCard(modifier: Modifier = Modifier, content: @Composable() (ColumnScope.() -> Unit)) {
    Card(
        elevation = CardDefaults.cardElevation(10.dp),
        shape = RectangleShape,
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 10.dp)
            .aspectRatio(3f / 4f),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
        ),
        content = content
    )
}

@Preview(showBackground = true)
@Composable
fun ButtonPreview() {
    AlbumAppTheme {
        Row(modifier = Modifier.fillMaxSize()) {
            ColouredButtonWithIcon(
                onClick = { /*TODO*/ },
                buttonImage = Icons.Rounded.Check,
                contentDescription = "Check",
                contentColor = Color.White,
                containerColor = Color.Red
            )
        }
    }
}

@Composable
fun MySpacer() {
    Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding_from_edge)))
}