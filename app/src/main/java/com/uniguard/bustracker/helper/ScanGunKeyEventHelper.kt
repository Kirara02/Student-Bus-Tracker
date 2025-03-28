package com.uniguard.bustracker.helper

import android.util.Log
import android.view.KeyEvent

/**
 * 扫码枪事件解析类 by chen
 */
class ScanGunKeyEventHelper(private val onScanSuccessListener: OnScanSuccessListener) {
    private val barcodeBuilder = StringBuilder()
    private var lastKeyTime = 0L

    companion object {
        private const val TAG = "ScanGunKeyEventHelper"
        private const val SCAN_TIMEOUT = 100L // milliseconds
    }

    init {
        Scanner_Tools.getUtil().onCreate(onScanSuccessListener)
    }

    /**
     * 扫码枪事件解析
     *
     * @param event
     */
    fun analysisKeyEvent(event: KeyEvent) {
       
    }

    interface OnScanSuccessListener {
        fun onScanSuccess(barcode: String?)
    }

    fun onDestroy() {
        Scanner_Tools.getUtil().onDestroy()
    }

    fun isScanGunEvent(event: KeyEvent): Boolean {
        // Check if the event is from a scan gun by checking the device name and vendor ID
        val deviceName = event.device?.name ?: return false
        val vendorId = event.device?.vendorId ?: return false
        
        // Log the device info for debugging
        Log.d(TAG, "Device: name=$deviceName, vendorId=$vendorId")
        
        // Return true only if it's from a known scan gun device
        // You may need to adjust these values based on your specific scan gun
        return deviceName.contains("scanner", ignoreCase = true) || 
               deviceName.contains("barcode", ignoreCase = true) ||
               vendorId == 0x0483 || // Example vendor ID for a common scan gun
               vendorId == 0x0482
    }
} 