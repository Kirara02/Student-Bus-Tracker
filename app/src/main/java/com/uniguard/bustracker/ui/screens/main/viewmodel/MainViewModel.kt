package com.uniguard.bustracker.ui.screens.main.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uniguard.bustracker.core.data.datasource.local.SettingDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val settingsDataStore: SettingDataStore,
    private val application: Application,
) : ViewModel() {

    private val _nfcId = MutableStateFlow<String>("")
    val nfcId: StateFlow<String> = _nfcId.asStateFlow()
    
    private var resetJob: Job? = null

    fun updateNfcId(id: String) {
        _nfcId.value = id
        
        // Cancel previous reset job if it exists
        resetJob?.cancel()
        
        // Start a new reset job
        resetJob = viewModelScope.launch {
            delay(3000) // 3 seconds delay
            _nfcId.value = "" // Clear the NFC ID
        }
    }
}