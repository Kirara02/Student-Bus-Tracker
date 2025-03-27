package com.uniguard.bustracker.helper

import gidb.com.Sderb
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.io.UnsupportedEncodingException
import java.lang.ref.WeakReference
import java.security.InvalidParameterException
import java.nio.charset.StandardCharsets

class SderbUtils {
    private var mOnScanSuccessListener: WeakReference<ScanGunKeyEventHelper.OnScanSuccessListener>? = null
    protected var mSderb: Sderb? = null
    protected var mOutputStream: OutputStream? = null
    private var mInputStream: InputStream? = null
    protected var mReadThread: ReadThread? = null
    private var n = 0
    var stop = false

    inner class ReadThread : Thread() {
        private val TAG = "ddd"

        override fun run() {
            super.run()
            while (!stop) {
                try {
                    val buffer = ByteArray(1000)

                    if (mInputStream == null) return
                    if (mInputStream!!.available() > 0) {
                        val size = mInputStream!!.read(buffer)
                        if (size > 0) {
                            onDataReceived(buffer, size, n)
                        }
                    } else {
                        try {
                            sleep(300)
                        } catch (e: InterruptedException) {
                            e.printStackTrace()
                        }
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                    return
                }
            }
        }
    }

    fun onCreate(onScanSuccessListener: ScanGunKeyEventHelper.OnScanSuccessListener) {
        mOnScanSuccessListener = WeakReference(onScanSuccessListener)
        this.stop = false
        try {
            mSderb = getSerialPort()
            mOutputStream = mSderb?.getOutputStream()
            mInputStream = mSderb?.getInputStream()

            /* Create a receiving thread */
            mReadThread = ReadThread()
            mReadThread?.start()
        } catch (e: SecurityException) {
            //DisplayError(R.string.error_security);
        } catch (e: IOException) {
            //DisplayError(R.string.error_unknown);
        } catch (e: Exception) {
            //DisplayError(R.string.error_configuration);
        }
    }

    fun onDataReceived(buffer: ByteArray, size: Int, n: Int) {
        val listener = mOnScanSuccessListener?.get()
        if (listener != null && size != 0) {
            try {
                listener.onScanSuccess("acm:" + String(buffer, 0, size, StandardCharsets.UTF_8))
            } catch (e: UnsupportedEncodingException) {
                e.printStackTrace()
            }
        }
    }

    fun onDestroy() {
        this.stop = true
        mReadThread?.interrupt()
        closeSerialPort()
        mSderb = null
        try {
            mOutputStream?.close()
            mInputStream?.close()
        } catch (e: IOException) {
        }
    }

    @Throws(SecurityException::class, IOException::class, InvalidParameterException::class)
    fun getSerialPort(): Sderb {
        if (mSderb == null) {
            mSderb = Sderb()
        }
        return mSderb!!
    }

    fun closeSerialPort() {
        if (mSderb != null) {
            mSderb?.close()
            mSderb = null
        }
    }
} 