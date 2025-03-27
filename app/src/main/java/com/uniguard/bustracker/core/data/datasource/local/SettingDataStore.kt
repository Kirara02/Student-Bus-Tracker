package com.uniguard.bustracker.core.data.datasource.local


import android.app.Application
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


private val Application.dataStore by preferencesDataStore(name = "settings")

class SettingDataStore(
    private val application: Application
) {

    companion object {
        private val URL_KEY = stringPreferencesKey("url")
        private val ID_DEVICE_KEY = stringPreferencesKey("id_device")

        // Default values
        private const val DEFAULT_URL = "http://192.168.1.116:3000"
        private const val DEFAULT_ID_DEVICE = ""
    }

    private val dataStore = application.dataStore

    val url: Flow<String> = dataStore.data.map { preferences ->
        preferences[URL_KEY] ?: DEFAULT_URL
    }

    val idDevice: Flow<String> = dataStore.data.map { preferences ->
        preferences[ID_DEVICE_KEY] ?: DEFAULT_ID_DEVICE
    }


    suspend fun updateUrl(newUrl: String) {
        dataStore.edit { preferences ->
            preferences[URL_KEY] = newUrl
        }
    }


    suspend fun updateIdDevice(newIdDevice: String) {
        dataStore.edit { preferences ->
            preferences[ID_DEVICE_KEY] = newIdDevice
        }
    }

}