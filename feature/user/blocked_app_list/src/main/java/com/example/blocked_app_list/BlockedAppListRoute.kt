package com.example.blocked_app_list

import ScreenLabel
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import coil.compose.rememberAsyncImagePainter
import com.example.util.models.InstalledApp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun BlockedAppListRoute(
    viewModel: BlockedAppListViewModel = hiltViewModel(),
    onNavigateToAppBlocker: () -> Unit
) {
    val context = LocalContext.current

    val blockedApps by viewModel.blockedApps.collectAsState(initial = emptyList())

    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.loadBlockedApps(context)
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    BlockedAppsScreen(
        apps = blockedApps,
        onUnblockApp = { packageName ->
            viewModel.unblockApp(packageName)
        },
        onBlock = onNavigateToAppBlocker,
        modifier = Modifier.fillMaxSize()
    )
}

@Composable
fun BlockedAppsScreen(
    apps: List<InstalledApp>,
    onUnblockApp: (String) -> Unit,
    onBlock: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
    ) {
        LazyColumn(
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                ScreenLabel(
                    title = "Заблокированные приложения",
                    modifier = Modifier.fillMaxWidth()
                )
            }
            items(apps) { app ->
                BlockedAppItem(app = app, onUnblockApp = onUnblockApp)
            }
        }
        IconButton(
            onClick = {
                onBlock()
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .clip(RoundedCornerShape(30.dp))
                .padding(16.dp)
        ) {
            Icon(
                contentDescription = "block app button",
                imageVector = Icons.Default.Add,
                modifier = Modifier
                    .background(Color(0xFFFF9800))
                    .fillMaxSize()
            )
        }
    }

}

@Composable
fun BlockedAppItem(
    app: InstalledApp,
    onUnblockApp: (String) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Разблокировать ${app.appName}?") },
            text = {
                Text("Вы уверены, что хотите досрочно разблокировать это приложение?")
            },
            confirmButton = {
                Button(onClick = {
                    onUnblockApp(app.packageName)
                    showDialog = false
                }) {
                    Text("Разблокировать")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Отмена")
                }
            }
        )
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { showDialog = true }
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = rememberAsyncImagePainter(model = app.icon),
            contentDescription = null,
            modifier = Modifier.size(48.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(app.appName, style = MaterialTheme.typography.bodyLarge)
            Text(
                text = "До ${
                    SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(
                        Date(app.unlockDurationMillis ?: 0)
                    )
                }",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
