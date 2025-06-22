package com.example.app_blocker

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.text.TextUtils
import android.view.accessibility.AccessibilityEvent
import com.example.data_store.AppBlockDataStore
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AppBlockerAccessibilityService : AccessibilityService() {

    @Inject
    lateinit var appBlockDataStore: AppBlockDataStore

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event?.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) return

        val packageName = event.packageName?.toString() ?: return

        CoroutineScope(Dispatchers.Default).launch {
            val unblockTime = appBlockDataStore.getUnblockTime(packageName)
            if (unblockTime != null && System.currentTimeMillis() < unblockTime) {
                val intent = Intent(
                    this@AppBlockerAccessibilityService,
                    BlockedAppOverlayActivity::class.java
                ).apply {
                    putExtra("package", packageName)
                    putExtra("unblock_time", unblockTime)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                startActivity(intent)
            }
        }
    }

    override fun onInterrupt() {}

    companion object {
        fun isAccessibilityServiceEnabled(
            context: Context,
            serviceClass: Class<out AccessibilityService>
        ): Boolean {
            val expectedComponentName = "${context.packageName}/${serviceClass.name}"
            val enabledServicesSetting = Settings.Secure.getString(
                context.contentResolver,
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
            ) ?: return false

            val colonSplitter = TextUtils.SimpleStringSplitter(':')
            colonSplitter.setString(enabledServicesSetting)

            for (componentName in colonSplitter) {
                if (componentName.equals(expectedComponentName, ignoreCase = true)) {
                    return true
                }
            }

            return false
        }
    }
}

