package com.example.data_store

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.util.models.AppBlockInfo
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

private const val DATASTORE_NAME = "app_blocker_prefs"
private val Context.dataStore by preferencesDataStore(name = DATASTORE_NAME)

class AppBlockDataStore(private val context: Context) {

    companion object {
        private val BLOCKED_APPS_KEY = stringSetPreferencesKey("blocked_apps")
    }

    suspend fun blockApp(packageName: String, durationMillis: Long) {
        val unblockTime = System.currentTimeMillis() + durationMillis
        context.dataStore.edit { prefs ->
            val current = prefs[BLOCKED_APPS_KEY] ?: emptySet()
            val updated = current
                .filterNot { it.startsWith("$packageName|") }
                .toMutableSet()
            updated += "$packageName|$unblockTime"
            prefs[BLOCKED_APPS_KEY] = updated
        }
    }

    suspend fun getBlockedApps(): List<AppBlockInfo> {
        return context.dataStore.data.map { prefs ->
            prefs[BLOCKED_APPS_KEY]
                ?.mapNotNull {
                    val parts = it.split("|")
                    if (parts.size == 2) {
                        val packageName = parts[0]
                        val time = parts[1].toLongOrNull()
                        if (time != null && time > System.currentTimeMillis()) {
                            AppBlockInfo(packageName, time)
                        } else null
                    } else null
                } ?: emptyList()
        }.first()
    }

    suspend fun getUnblockTime(packageName: String): Long? {
        return context.dataStore.data.map { prefs ->
            prefs[BLOCKED_APPS_KEY]
                ?.firstOrNull { it.startsWith("$packageName|") }
                ?.split("|")
                ?.getOrNull(1)
                ?.toLongOrNull()
        }.firstOrNull()
    }

    suspend fun unblockApp(packageName: String) {
        context.dataStore.edit { prefs ->
            val current = prefs[BLOCKED_APPS_KEY] ?: emptySet()
            val updated = current.filterNot { it.startsWith("$packageName|") }.toSet()
            prefs[BLOCKED_APPS_KEY] = updated
        }
    }
}