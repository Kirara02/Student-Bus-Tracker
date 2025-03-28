package com.uniguard.bustracker.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class AppStartReceiver : BroadcastReceiver() {
    init {
        Log.d(TAG, "AppStartReceiver initialized")
    }

    override fun onReceive(context: Context, intent: Intent) {
        try {
            when (intent.action) {
                Intent.ACTION_BOOT_COMPLETED,
                "android.intent.action.LOCKED_BOOT_COMPLETED" -> {
                    Log.d(TAG, "Received boot completed broadcast")
                    if (!isStarted) {
                        val i =
                            Intent(context, com.uniguard.bustracker.app.MainActivity::class.java)
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        context.startActivity(i)
                        isStarted = true
                        Log.d(TAG, "MainActivity started successfully")
                    } else {
                        Log.d(TAG, "App already started, skipping")
                    }
                }

                "com.jason.intent.action.BOOT_COMPLETED" -> {
                    Log.d(TAG, "Received custom boot completed broadcast")
                    // Handle custom boot completed if needed
                }

                else -> {
                    Log.d(TAG, "Received unknown action: ${intent.action}")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in onReceive: ${e.message}", e)
        }
    }

    companion object {
        private const val TAG = "AppStartReceiver"
        var isStarted: Boolean = false
    }
}