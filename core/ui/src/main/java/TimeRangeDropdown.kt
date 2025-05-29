import androidx.compose.foundation.layout.Box
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

@Composable
fun TimeRangeDropdown(
    selected: TimeRange,
    onRangeSelected: (TimeRange) -> Unit,
    onRequestCustom: () -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier) {
        OutlinedButton(onClick = { expanded = true }) {
            Text("Период: ${selected.label}")
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
            }
        ) {
            TimeRange.values().forEach {
                DropdownMenuItem(
                    text = { Text(it.label) },
                    onClick = {
                        expanded = false
                        if (it == TimeRange.CUSTOM) {
                            onRequestCustom()
                        } else {
                            onRangeSelected(it)
                        }
                    }
                )
            }
        }
    }
}
