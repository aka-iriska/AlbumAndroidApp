import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.example.albumapp.R

@Composable
fun SureChoice(
    color: Color,
    onYesClick: () -> Unit,
    onNoClick: () -> Unit,
    onCancelClick: () -> Unit = {},
    onCancelClickFlag: Boolean = false,
    text: String
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text, textAlign = TextAlign.Center, color = color)
        Row {
            TextButton(onClick = onYesClick) {
                Text(stringResource(R.string.yes))
            }
            TextButton(onClick = onNoClick) {
                Text(stringResource(R.string.no))
            }
            if (onCancelClickFlag) {
                TextButton(onClick = onCancelClick) {
                    Text(stringResource(R.string.cancel))
                }
            }
        }
    }

}