package com.uniguard.bustracker.ui.screens.main.viewmodel

import android.location.Location
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uniguard.bustracker.core.data.datasource.local.SettingDataStore
import com.uniguard.bustracker.core.data.model.User
import com.uniguard.bustracker.core.data.model.request.UserLocationRequest
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

    private val _displayText = MutableStateFlow("")
    val displayText: StateFlow<String> = _displayText.asStateFlow()

    private val _userData = MutableStateFlow<User?>(null)
    val userData: StateFlow<User?> = _userData.asStateFlow()

    private var resetJob: Job? = null


    override fun onCleared() {
        super.onCleared()
    }

    private fun startResetTimer() {
        // Cancel previous reset job if it exists
        resetJob?.cancel()

        // Start a new reset job
        resetJob = viewModelScope.launch {
            delay(10000) // 10 seconds delay
            _displayText.value = "" // Clear the display text
            _userData.value = null
        }
    }

    fun updateNfcId(id: String) {
        // Cancel any existing reset timer
        resetJob?.cancel()
        _displayText.value = id
        _userData.value = null // Reset user data immediately
        fetchUserData(id, 106.8456, -6.2088)
        startResetTimer()
    }

    fun updateBarcodeInfo(barcode: String) {
        // Cancel any existing reset timer
        resetJob?.cancel()
        _displayText.value = barcode
        _userData.value = null // Reset user data immediately
        fetchUserData(barcode, 106.8456, -6.2088)
        startResetTimer()
    }

    private fun fetchUserData(uid: String, latitude: Double, longitude: Double) {
        viewModelScope.launch {
            try {
                val request = UserLocationRequest(uid, latitude, longitude)
                getUserByUidUseCase.execute(request).collect { user ->
                    _userData.value = user
                }
            } catch (e: Exception) {
                Log.e("fetchUserData", e.message.toString())
            }
        }
    }
}