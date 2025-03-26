package com.uniguard.bustracker.ui.screens.setting.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uniguard.bustracker.core.data.datasource.local.SettingDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val settingsDataStore: SettingDataStore,
) : ViewModel() {

    val url: Flow<String> = settingsDataStore.url
    val idDevice: Flow<String> = settingsDataStore.idDevice

    fun updateUrl(url: String) {
        viewModelScope.launch {
            settingsDataStore.updateUrl(url)
        }
    }

    fun updateIdDevice(idDevice: String) {
        viewModelScope.launch {
            settingsDataStore.updateIdDevice(idDevice)
        }
    }
}