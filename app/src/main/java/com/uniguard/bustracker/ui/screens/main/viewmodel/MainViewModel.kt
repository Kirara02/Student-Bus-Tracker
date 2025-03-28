package com.uniguard.bustracker.ui.screens.main.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uniguard.bustracker.core.data.datasource.local.SettingDataStore
import com.uniguard.bustracker.core.data.model.User
import com.uniguard.bustracker.core.domain.usecase.GetUserByUidUseCase
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
    private val getUserByUidUseCase: GetUserByUidUseCase
) : ViewModel() {

    private val _displayText = MutableStateFlow<String>("")
    val displayText: StateFlow<String> = _displayText.asStateFlow()

    private val _mStr = MutableStateFlow<String>("")
    val mStr: StateFlow<String> = _mStr.asStateFlow()

    private val _userData = MutableStateFlow<User?>(null)
    val userData: StateFlow<User?> = _userData.asStateFlow()

    private var resetJob: Job? = null

    private fun startResetTimer() {
        // Cancel previous reset job if it exists
        resetJob?.cancel()

        // Start a new reset job
        resetJob = viewModelScope.launch {
            delay(10000) // 10 seconds delay
            _displayText.value = "" // Clear the display text
            _mStr.value = "" // Clear the mStr as well
            _userData.value = null
        }
    }

    fun updateNfcId(id: String) {
        // Cancel any existing reset timer
        resetJob?.cancel()
        _displayText.value = id
        _userData.value = null // Reset user data immediately
        fetchUserData(id)
        startResetTimer()
    }

    fun updateBarcodeInfo(barcode: String, stdd: String) {
        // Cancel any existing reset timer
        resetJob?.cancel()
        _displayText.value = barcode
        _mStr.value = "barcode length: ${barcode.length},md5sum:$stdd"
        _userData.value = null // Reset user data immediately
        fetchUserData(barcode)
        startResetTimer()
    }

    private fun fetchUserData(uid: String) {
        viewModelScope.launch {
            try {
                getUserByUidUseCase.execute(uid).collect { user ->
                    _userData.value = user
                }
            } catch (e: Exception) {
                _mStr.value = "Error: ${e.message}"
            }
        }
    }
}