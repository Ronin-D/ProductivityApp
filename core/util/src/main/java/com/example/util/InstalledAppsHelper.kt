package com.example.util

import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.util.models.InstalledApp

object InstalledAppsHelper {
    fun getInstalledApps(context: Context): List<InstalledApp> {
        val pm = context.packageManager
        val startupIntent =
            Intent(Intent.ACTION_MAIN).apply { addCategory(Intent.CATEGORY_LAUNCHER) }
        return pm.queryIntentActivities(startupIntent,0)
            .mapNotNull { app ->
                try {
                    InstalledApp(
                        appName = app.loadLabel(pm).toString(),
                        packageName = app.activityInfo.packageName,
                        icon = app.loadIcon(pm)
                    )
                } catch (e: Exception) {
                    Log.d("InstalledAppsHelper", "error parsing installedApp", e)
                    null
                }
            }.sortedBy { it.appName.lowercase() }
    }

}
