package com.uniguard.bustracker.helper

import android.os.Build
import android.util.Log
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

object PasswdUtils {
    private const val TAG = "pass"

    fun getMd5(plainText: String): String {
        try {
            val md = MessageDigest.getInstance("MD5")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                md.update(plainText.toByteArray(StandardCharsets.UTF_8))
            } else {
                Log.e(TAG, " if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {")
            }

            val b = md.digest()
            val buf = StringBuilder()
            for (b1 in b) {
                var i = b1.toInt()
                if (i < 0) {
                    i += 256
                }
                if (i < 16) {
                    buf.append("0")
                }
                buf.append(Integer.toHexString(i))
            }
            Log.d(TAG, "getMd5 out:" + buf.toString())
            return buf.toString()

        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
            return " "
        }
    }
} 