package com.example.util.models

import android.graphics.drawable.Drawable

data class InstalledApp(
    val appName: String,
    val packageName: String,
    val icon: Drawable,
    var unlockDurationMillis: Long? = null
)
