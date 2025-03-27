package gidb.com

import android.util.Log
import java.io.*

class Sderb @Throws(SecurityException::class, IOException::class) constructor() {
    companion object {
        private const val TAG = "Sderb"

        init {
            System.loadLibrary("serial_port_d")
        }

        fun execCommand(cmd: String): String {
            var process: Process? = null
            var os: DataOutputStream? = null
            var msg = ""
            try {
                process = Runtime.getRuntime().exec("hdxsu")
                os = DataOutputStream(process.outputStream)
                os.writeBytes("$cmd\n")
                os.flush()
                os.writeBytes("exit\n")
                os.flush()

                val bufferedReader = BufferedReader(InputStreamReader(process.inputStream))
                var line: String?
                while (bufferedReader.readLine().also { line = it } != null) {
                    msg += "$line\n"
                    Log.i(TAG, "bufferedReader read: $line")
                }
                process.waitFor()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                try {
                    os?.close()
                    process?.destroy()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            return msg
        }

        fun execShellCmdForStatue(command: String): Int {
            var status = -1
            try {
                var process: Process? = null
                var error: BufferedReader? = null
                var reader: BufferedReader? = null
                var writer: BufferedWriter? = null
                process = Runtime.getRuntime().exec("hdxsu")
                writer = BufferedWriter(OutputStreamWriter(process.outputStream))
                error = BufferedReader(InputStreamReader(process.errorStream))
                reader = BufferedReader(InputStreamReader(process.inputStream))

                writer.write(command)
                writer.flush()

                Log.d(TAG, " _________ddd----- command: $command    status = $status")
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return status
        }
    }

    /*
     * Do not remove or rename the field mFd: it is used by native method close();
     */
    private val mFd: FileDescriptor?
    private val mFileInputStream: FileInputStream
    private val mFileOutputStream: FileOutputStream

    init {
        mFd = getdesc()

        if (mFd == null) {
            Log.d(TAG, "native open returns null")
            throw IOException()
        }
        mFileInputStream = FileInputStream(mFd)
        mFileOutputStream = FileOutputStream(mFd)
    }

    // Getters and setters
    fun getInputStream(): InputStream {
        return mFileInputStream
    }

    fun getOutputStream(): OutputStream {
        return mFileOutputStream
    }

    private fun do_exec(cmd: String): String {
        var s = "/n"
        try {
            val p = Runtime.getRuntime().exec(cmd)
            val `in` = BufferedReader(InputStreamReader(p.inputStream))
            var line: String?
            while (`in`.readLine().also { line = it } != null) {
                s += "$line/n"
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return cmd
    }

    // JNI
    private external fun getdesc(): FileDescriptor?
    external fun close()
} 