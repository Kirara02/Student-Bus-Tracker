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
        if (event.action == KeyEvent.ACTION_DOWN) {
            val currentTime = System.currentTimeMillis()

            // Reset barcode if too much time has passed since last key
            if (currentTime - lastKeyTime > SCAN_TIMEOUT) {
                barcodeBuilder.setLength(0)
            }

            lastKeyTime = currentTime

            // Get the key character
            val keyChar = event.unicodeChar.toChar()

            // Add to barcode if it's a printable character
            if (keyChar.isLetterOrDigit() || keyChar == '-' || keyChar == '_') {
                barcodeBuilder.append(keyChar)
            }

            // Check if this is the end of the barcode (usually Enter key)
            if (event.keyCode == KeyEvent.KEYCODE_ENTER && barcodeBuilder.isNotEmpty()) {
                val barcode = barcodeBuilder.toString()
                Log.d(TAG, "Barcode scanned: $barcode")
                Scanner_Tools.getUtil().onDataReceived(barcode.toByteArray(), barcode.length, 0)
                barcodeBuilder.setLength(0)
            }
        }
    }

    interface OnScanSuccessListener {
        fun onScanSuccess(barcode: String?)
    }

    fun onDestroy() {
        Scanner_Tools.getUtil().onDestroy()
    }

    fun isScanGunEvent(event: KeyEvent): Boolean {
        // Log the device info for debugging
        Log.d(TAG, "Device: ${event.device.name}, " +
                "VID: ${Integer.toHexString(event.device.vendorId)}, " +
                "PID: ${Integer.toHexString(event.device.productId)}")

        // You can add specific device checks here if needed
        // For now, we'll accept all keyboard events
        return true
    }
} 