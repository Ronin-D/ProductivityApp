package com.example.data_store

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val DATA_STORE_FILE_NAME = "auth"
private val Context.dataStore by preferencesDataStore(name = DATA_STORE_FILE_NAME)

class AuthDataStoreRepository(private val context: Context) {
    suspend fun setUserRememberedFlag(value: Boolean) {
        context.dataStore.edit { settings ->
            settings[isUserRememberedKey] = value
        }
    }

    suspend fun setAccessToken(token: String) {
        context.dataStore.edit { settings ->
            settings[accessTokenKey] = token
        }
    }

    suspend fun setRefreshToken(token: String) {
        context.dataStore.edit { settings ->
            settings[refreshTokenKey] = token
        }
    }

    suspend fun setUserRole(role: String) {
        context.dataStore.edit { settings ->
            settings[userRoleKey] = role
        }
    }

    fun getUserRole(): Flow<String?> {
        return context.dataStore.data.map { settings ->
            settings[userRoleKey]
        }
    }

    fun getUserRememberedFlag(): Flow<Boolean?> {
        return context.dataStore.data.map { settings ->
            settings[isUserRememberedKey]
        }
    }

    fun getAccessToken(): Flow<String?> {
        return context.dataStore.data.map { settings ->
            settings[accessTokenKey]
        }
    }

    fun getRefreshToken(): Flow<String?> {
        return context.dataStore.data.map { settings ->
            settings[refreshTokenKey]
        }
    }

    suspend fun clearTokens() {
        context.dataStore.edit { settings ->
            settings.remove(accessTokenKey)
            settings.remove(refreshTokenKey)
            settings.remove(isUserRememberedKey)
            settings.remove(userRoleKey)
        }
    }

    companion object DataStoreKeys {
        val isUserRememberedKey = booleanPreferencesKey("is_user_remembered")
        val accessTokenKey = stringPreferencesKey("access_token")
        val refreshTokenKey = stringPreferencesKey("refresh_token")
        val userRoleKey = stringPreferencesKey("user_role")
    }
}
