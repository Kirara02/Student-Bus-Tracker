package com.uniguard.bustracker.app

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.uniguard.bustracker.helper.PasswdUtils
import com.uniguard.bustracker.helper.ScanGunKeyEventHelper
import com.uniguard.bustracker.ui.screens.main.MainScreen
import com.uniguard.bustracker.ui.screens.main.viewmodel.MainViewModel
import com.uniguard.bustracker.ui.screens.setting.SettingScreen
import com.uniguard.bustracker.ui.theme.BusTrackerTheme
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity(), ScanGunKeyEventHelper.OnScanSuccessListener {
    private var nfcAdapter: NfcAdapter? = null
    private var pendingIntent: PendingIntent? = null
    private lateinit var mainViewModel: MainViewModel
    private lateinit var mScanGunKeyEventHelper: ScanGunKeyEventHelper

    var begin_time: Long = 0
    var from_hid: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize ScanGunKeyEventHelper
        mScanGunKeyEventHelper = ScanGunKeyEventHelper(this)

        // Initialize NFC
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        pendingIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )

        setContent {
            val navController = rememberNavController()

            BusTrackerTheme {
                NavHost(
                    navController = navController,
                    startDestination = "/main"
                ) {
                    composable("/main") {
                        mainViewModel = hiltViewModel()
                        MainScreen(navController = navController, viewModel = mainViewModel)
                    }

                    composable("/setting") {
                        SettingScreen(
                            navController = navController
                        )
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        nfcAdapter?.enableForegroundDispatch(this, pendingIntent, null, null)
    }

    override fun onPause() {
        super.onPause()
        nfcAdapter?.disableForegroundDispatch(this)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleNfcIntent(intent)
    }

    private fun handleNfcIntent(intent: Intent?) {
        if (intent == null) return

        when (intent.action) {
            NfcAdapter.ACTION_TECH_DISCOVERED,
            NfcAdapter.ACTION_TAG_DISCOVERED,
            NfcAdapter.ACTION_NDEF_DISCOVERED -> {
                val tag: Tag? = intent.getParcelableExtraCompat(NfcAdapter.EXTRA_TAG)
                tag?.let {
                    val id = bytesToHexString(it.id)
                    Log.d("NFC", "Tag ID: $id")
                    // Update the NFC ID in ViewModel
                    mainViewModel.updateNfcId(id)
                }
            }
        }
    }

    private inline fun <reified T : android.os.Parcelable> Intent.getParcelableExtraCompat(key: String): T? {
        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            getParcelableExtra(key, T::class.java)
        } else {
            @Suppress("DEPRECATION")
            getParcelableExtra(key)
        }
    }

    private fun bytesToHexString(bytes: ByteArray): String {
        val sb = StringBuilder()
        for (b in bytes) {
            sb.append(String.format("%02X", b))
        }
        return sb.toString()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return super.onKeyDown(keyCode, event)
    }

    @SuppressLint("RestrictedApi")
    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        val current = System.currentTimeMillis()
        if (current - begin_time > 1000) {
            begin_time = System.currentTimeMillis()
            Log.d(
                "ca1",
                "name:" + event.device.name + ",vid:" + Integer.toHexString(event.device.vendorId) + ",pid:" + Integer.toHexString(
                    event.device.productId
                )
            )
        }

        if (mScanGunKeyEventHelper.isScanGunEvent(event)) {
            from_hid = true
        }
        mScanGunKeyEventHelper.analysisKeyEvent(event)
        return true
    }

    override fun onScanSuccess(barcode: String?) {
        var current = System.currentTimeMillis()
        var ddd = (current - begin_time).toFloat()
        ddd = ddd / 1000
        current = (ddd * 100).toLong()
        var str = "[$current]from_ser:"
        begin_time = 0
        if (from_hid) {
            str = "[" + (current - begin_time) + "]from_ser:"
        }

        // Remove "acm:" prefix if exists
        val cleanBarcode = barcode?.removePrefix("acm:") ?: ""
        val stdd = str + PasswdUtils.getMd5(cleanBarcode)

        runOnUiThread {
            mainViewModel.updateBarcodeInfo(cleanBarcode)
            // Reset from_hid flag after successful scan
            from_hid = false
        }

        Log.d(
            "ca1",
            "jason: " + cleanBarcode.length + "," + PasswdUtils.getMd5(cleanBarcode) + ",code:" + cleanBarcode
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        mScanGunKeyEventHelper.onDestroy()
    }
}
