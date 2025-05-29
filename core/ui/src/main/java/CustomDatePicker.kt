import android.app.DatePickerDialog
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.Calendar

@Composable
fun showCustomDatePicker(
    onDatesSelected: (ZonedDateTime, ZonedDateTime) -> Unit,
    onPickerDismissed: () -> Unit
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    val startPickerShown = remember { mutableStateOf(false) }
    val endPickerShown = remember { mutableStateOf(false) }

    val startDate = remember { mutableStateOf<ZonedDateTime?>(null) }

    val zoneId = ZoneId.systemDefault()

    if (startPickerShown.value) {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val start = ZonedDateTime.of(year, month + 1, dayOfMonth, 0, 0, 0, 0, zoneId)
                startDate.value = start
                startPickerShown.value = false
                endPickerShown.value = true
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).apply {
            setOnDismissListener {
                if (startPickerShown.value) {
                    startPickerShown.value = false
                    onPickerDismissed()
                }
            }
        }.show()
    }

    if (endPickerShown.value && startDate.value != null) {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val end =
                    ZonedDateTime.of(year, month + 1, dayOfMonth, 23, 59, 59, 999_000_000, zoneId)
                endPickerShown.value = false
                onDatesSelected(startDate.value!!, end)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).apply {
            setOnDismissListener {
                if (endPickerShown.value) {
                    endPickerShown.value = false
                    onPickerDismissed()
                }
            }
        }.show()
    }

    LaunchedEffect(Unit) {
        startPickerShown.value = true
    }
}
