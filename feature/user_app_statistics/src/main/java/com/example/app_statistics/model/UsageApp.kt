package com.example.app_statistics.model

import android.graphics.drawable.Drawable

data class UsageApp(
    val appName: String,
    val icon: Drawable,
    val foregroundTime: Long,
    val packageName: String
)
