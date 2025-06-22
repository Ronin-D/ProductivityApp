package com.example.app_blocker

import ScreenLabel
import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.example.app_blocker.AppBlockerAccessibilityService.Companion.isAccessibilityServiceEnabled
import com.example.util.models.InstalledApp

@Composable
fun AppBlockerRoute(
    viewModel: AppBlockerViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val apps by viewModel.installedApps.collectAsState()
    val context = LocalContext.current

    var showAccessibilityDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.loadInstalledApps(context)

        if (!isAccessibilityServiceEnabled(context, AppBlockerAccessibilityService::class.java)) {
            showAccessibilityDialog = true
        }
    }

    if (showAccessibilityDialog) {
        AlertDialog(
            onDismissRequest = { showAccessibilityDialog = false },
            title = { Text("Включите службу блокировки") },
            text = { Text("Чтобы блокировка работала, включите службу в разделе специальных возможностей.") },
            confirmButton = {
                Button(onClick = {
                    val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(intent)
                    showAccessibilityDialog = false
                }) {
                    Text("Открыть настройки")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showAccessibilityDialog = false
                }) {
                    Text("Позже")
                }
            }
        )
    }

    AppBlockerScreen(
        apps = apps,
        onBlockApp = { packageName, duration ->
            viewModel.blockApp(packageName, duration)
        },
        onNavigateBack = onNavigateBack
    )
}


@Composable
fun AppBlockerScreen(
    apps: List<InstalledApp>,
    onBlockApp: (String, Long) -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            ScreenLabel(
                title = "Блокировка приложений",
                modifier = Modifier.fillMaxWidth(),
                onNavigateBack = onNavigateBack
            )
        }
        items(apps) { app ->
            AppBlockerItem(app = app, onBlockApp = onBlockApp)
        }
    }
}

@Composable
fun AppBlockerItem(
    app: InstalledApp,
    onBlockApp: (String, Long) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Заблокировать ${app.appName}") },
            text = {
                Column {
                    Text("Выберите время блокировки:")
                    Spacer(modifier = Modifier.height(8.dp))

                    val options = listOf(
                        "15 минут" to 15 * 60 * 1000L,
                        "30 минут" to 30 * 60 * 1000L,
                        "1 час" to 60 * 60 * 1000L,
                        "2 часа" to 2 * 60 * 60 * 1000L
                    )

                    options.forEach { (label, duration) ->
                        Button(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            onClick = {
                                onBlockApp(app.packageName, duration)
                                showDialog = false
                            }
                        ) {
                            Text(label)
                        }
                    }
                }
            },
            confirmButton = {},
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
        Text(text = app.appName, style = MaterialTheme.typography.bodyLarge)
    }
}

