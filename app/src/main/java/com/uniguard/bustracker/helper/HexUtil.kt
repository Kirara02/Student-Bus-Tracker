package com.uniguard.bustracker.helper

import java.math.BigInteger

object HexUtil {
    /**
     * 整形转换成网络传输的字节流（字节数组）型数据
     *
     * @param num 一个整型数据
     * @return 4个字节的自己数组
     */
    fun intToBytes(num: Int): ByteArray {
        val bytes = ByteArray(4)
        bytes[0] = (0xff and (num shr 0)).toByte()
        bytes[1] = (0xff and (num shr 8)).toByte()
        bytes[2] = (0xff and (num shr 16)).toByte()
        bytes[3] = (0xff and (num shr 24)).toByte()
        return bytes
    }

    /**
     * 四个字节的字节数据转换成一个整形数据
     *
     * @param bytes 4个字节的字节数组
     * @return 一个整型数据
     */
    fun byteToInt(bytes: ByteArray): Int {
        var num = 0
        var temp: Int
        temp = (0x000000ff and (bytes[0].toInt())) shl 0
        num = num or temp
        temp = (0x000000ff and (bytes[1].toInt())) shl 8
        num = num or temp
        temp = (0x000000ff and (bytes[2].toInt())) shl 16
        num = num or temp
        temp = (0x000000ff and (bytes[3].toInt())) shl 24
        num = num or temp
        return num
    }

    /**
     * 长整形转换成网络传输的字节流（字节数组）型数据
     *
     * @param num 一个长整型数据
     * @return 4个字节的自己数组
     */
    fun longToBytes(num: Long): ByteArray {
        val bytes = ByteArray(8)
        for (i in 0..7) {
            bytes[i] = ((num shr (i * 8)) and 0xFFL).toInt().toByte()
        }
        return bytes
    }

    /**
     * 大数字转换字节流（字节数组）型数据
     *
     * @param n
     * @return
     */
    fun byteConvert32Bytes(n: BigInteger?): ByteArray? {
        var tmpd: ByteArray? = null
        if (n == null) {
            return null
        }

        val byteArray = n.toByteArray()
        if (byteArray.size == 33) {
            tmpd = ByteArray(32)
            System.arraycopy(byteArray, 1, tmpd, 0, 32)
        } else if (byteArray.size == 32) {
            tmpd = byteArray
        } else {
            tmpd = ByteArray(32)
            for (i in 0 until 32 - byteArray.size) {
                tmpd[i] = 0
            }
            System.arraycopy(byteArray, 0, tmpd, 32 - byteArray.size, byteArray.size)
        }
        return tmpd
    }

    /**
     * 换字节流（字节数组）型数据转大数字
     *
     * @param b
     * @return
     */
    fun byteConvertInteger(b: ByteArray): BigInteger {
        return if (b[0] < 0) {
            val temp = ByteArray(b.size + 1)
            temp[0] = 0
            System.arraycopy(b, 0, temp, 1, b.size)
            BigInteger(temp)
        } else {
            BigInteger(b)
        }
    }

    /**
     * 根据字节数组获得值(十六进制数字)
     *
     * @param bytes
     * @return
     */
    fun getHexString(bytes: ByteArray): String {
        return getHexString(bytes, true)
    }

    /**
     * 根据字节数组获得值(十六进制数字)
     *
     * @param bytes
     * @param upperCase
     * @return
     */
    fun getHexString(bytes: ByteArray, upperCase: Boolean): String {
        var ret = ""
        for (b in bytes) {
            ret += Integer.toString((b.toInt() and 0xff) + 0x100, 16).substring(1)
        }
        return if (upperCase) ret.uppercase() else ret
    }

    /**
     * 打印十六进制字符串
     *
     * @param bytes
     */
    fun printHexString(bytes: ByteArray) {
        for (b in bytes) {
            var hex = Integer.toHexString(b.toInt() and 0xFF)
            if (hex.length == 1) {
                hex = "0$hex"
            }
            print("0x${hex.uppercase()},")
        }
        println("")
    }

    /**
     * Convert hex string to byte[]
     *
     * @param hexString the hex string
     * @return byte[]
     */
    fun hexStringToBytes(hexString: String?): ByteArray? {
        if (hexString.isNullOrEmpty()) {
            return null
        }

        val hexStringUpper = hexString.uppercase()
        val length = hexStringUpper.length / 2
        val hexChars = hexStringUpper.toCharArray()
        val d = ByteArray(length)
        for (i in 0 until length) {
            val pos = i * 2
            d[i] =
                (charToByte(hexChars[pos]).toInt() shl 4 or charToByte(hexChars[pos + 1]).toInt()).toByte()
        }
        return d
    }

    /**
     * Convert char to byte
     *
     * @param c char
     * @return byte
     */
    fun charToByte(c: Char): Byte {
        return "0123456789ABCDEF".indexOf(c).toByte()
    }

    /**
     * 用于建立十六进制字符的输出的小写字符数组
     */
    private val DIGITS_LOWER = charArrayOf(
        '0', '1', '2', '3', '4', '5',
        '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'
    )

    /**
     * 用于建立十六进制字符的输出的大写字符数组
     */
    private val DIGITS_UPPER = charArrayOf(
        '0', '1', '2', '3', '4', '5',
        '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'
    )

    /**
     * 将字节数组转换为十六进制字符数组
     *
     * @param data byte[]
     * @return 十六进制char[]
     */
    fun encodeHex(data: ByteArray): CharArray {
        return encodeHex(data, true)
    }

    /**
     * 将字节数组转换为十六进制字符数组
     *
     * @param data byte[]
     * @param toLowerCase <code>true</code> 传换成小写格式 ， <code>false</code> 传换成大写格式
     * @return 十六进制char[]
     */
    fun encodeHex(data: ByteArray, toLowerCase: Boolean): CharArray {
        return encodeHex(data, if (toLowerCase) DIGITS_LOWER else DIGITS_UPPER)
    }

    /**
     * 将字节数组转换为十六进制字符数组
     *
     * @param data byte[]
     * @param toDigits 用于控制输出的char[]
     * @return 十六进制char[]
     */
    private fun encodeHex(data: ByteArray, toDigits: CharArray): CharArray {
        val l = data.size
        val out = CharArray(l shl 1)
        // two characters form the hex value.
        var j = 0
        for (i in 0 until l) {
            out[j++] = toDigits[(0xF0 and data[i].toInt()) ushr 4]
            out[j++] = toDigits[0x0F and data[i].toInt()]
        }
        return out
    }

    /**
     * 将字节数组转换为十六进制字符串
     *
     * @param data byte[]
     * @return 十六进制String
     */
    fun encodeHexString(data: ByteArray): String {
        return encodeHexString(data, true)
    }

    /**
     * 将字节数组转换为十六进制字符串
     *
     * @param data byte[]
     * @param toLowerCase <code>true</code> 传换成小写格式 ， <code>false</code> 传换成大写格式
     * @return 十六进制String
     */
    fun encodeHexString(data: ByteArray, toLowerCase: Boolean): String {
        return encodeHexString(data, if (toLowerCase) DIGITS_LOWER else DIGITS_UPPER)
    }

    /**
     * 将字节数组转换为十六进制字符串
     *
     * @param data byte[]
     * @param toDigits 用于控制输出的char[]
     * @return 十六进制String
     */
    private fun encodeHexString(data: ByteArray, toDigits: CharArray): String {
        return String(encodeHex(data, toDigits))
    }

    /**
     * 将十六进制字符数组转换为字节数组
     *
     * @param data 十六进制char[]
     * @return byte[]
     * @throws IllegalArgumentException 如果源十六进制字符数组是一个奇怪的长度，将抛出该异常
     */
    @Throws(IllegalArgumentException::class)
    fun decodeHex(data: CharArray): ByteArray {
        val len = data.size

        if (len and 0x1 != 0) {
            throw IllegalArgumentException("Odd number of characters.")
        }

        val out = ByteArray(len shr 1)

        // two characters form the hex value.
        var i = 0
        var j = 0
        while (j < len) {
            var f = toDigit(data[j], j) shl 4
            j++
            f = f or toDigit(data[j], j)
            j++
            out[i] = (f and 0xFF).toByte()
            i++
        }

        return out
    }

    /**
     * 将十六进制字符转换成一个整数
     *
     * @param ch    十六进制char
     * @param index 十六进制字符在字符数组中的位置
     * @return 一个整数
     * @throws IllegalArgumentException 当ch不是一个合法的十六进制字符时，抛出该异常
     */
    @Throws(IllegalArgumentException::class)
    private fun toDigit(ch: Char, index: Int): Int {
        val digit = Character.digit(ch, 16)
        if (digit == -1) {
            throw IllegalArgumentException("Illegal hexadecimal character $ch at index $index")
        }
        return digit
    }

    /**
     * 字符串转换成十六进制字符串
     *
     * @param content
     * @return
     */
    fun StringToAsciiString(content: String): String {
        val length = content.length
        val out = StringBuilder(length * 2)
        for (i in 0 until length) {
            val a = content[i]
            val b = Integer.toHexString(a.code)
            if (b.length == 1) {
                out.append("0")
            }
            out.append(b)
        }
        return out.toString()
    }

    /**
     * hex字符串转String
     *
     * @param hexString
     * @param encodeType
     * @return
     */
    fun hexStringToString(hexString: String, encodeType: Int): String {
        var result = ""
        val max = hexString.length / encodeType
        for (i in 0 until max) {
            val char = hexString.substring(i * encodeType, (i + 1) * encodeType)
            result += char.toInt(16).toChar()
        }
        return result
    }

    /**
     * 十六进制字符串转换成十进制整数
     *
     * @param hex
     * @return
     */
    fun hexStringToAlgorism(hex: String): Int {
        hex.uppercase()
        val max = hex.length
        var result = 0
        for (i in 0 until max) {
            val c = hex[i]
            val temp = when (c) {
                '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' -> c.code - '0'.code
                'A', 'B', 'C', 'D', 'E', 'F' -> c.code - 'A'.code + 10
                else -> throw IllegalArgumentException("Invalid hex character: $c")
            }
            result += temp shl (max - i - 1) * 4
        }
        return result
    }

    /**
     * hex转byte
     *
     * @param hex
     * @return
     */
    fun hexStringToBinary(hex: String): String {
        hex.uppercase()
        var result = ""
        var max = hex.length
        for (i in 0 until max) {
            val c = hex[i]
            val temp = when (c) {
                '0' -> "0000"
                '1' -> "0001"
                '2' -> "0010"
                '3' -> "0011"
                '4' -> "0100"
                '5' -> "0101"
                '6' -> "0110"
                '7' -> "0111"
                '8' -> "1000"
                '9' -> "1001"
                'A' -> "1010"
                'B' -> "1011"
                'C' -> "1100"
                'D' -> "1101"
                'E' -> "1110"
                'F' -> "1111"
                else -> throw IllegalArgumentException("Invalid hex character: $c")
            }
            result += temp
        }
        return result
    }

    /**
     * 将十六进制转换为十进制
     *
     * @param content
     * @return
     */
    fun AsciiStringToString(content: String): String {
        val out = StringBuilder()
        for (i in 0 until content.length step 2) {
            val str = content.substring(i, i + 2)
            out.append(str.toInt(16).toChar())
        }
        return out.toString()
    }

    /**
     * 十进制转换为十六进制
     *
     * @param algorism
     * @param maxLength
     * @return
     */
    fun algorismToHexString(algorism: Int, maxLength: Int): String {
        var result = Integer.toHexString(algorism)
        if (result.length % 2 == 1) {
            result = "0$result"
        }
        return patchHexString(result, maxLength)
    }

    /**
     * 字节数组转16进制
     *
     * @param bytearray
     * @return
     */
    fun byteToString(bytearray: ByteArray): String {
        val out = StringBuilder(bytearray.size * 2)
        for (b in bytearray) {
            val st = Integer.toHexString(b.toInt() and 0xff)
            if (st.length == 1) {
                out.append("0")
            }
            out.append(st)
        }
        return out.toString().uppercase()
    }

    /**
     * 二进制转十进制
     *
     * @param binary
     * @return
     */
    fun binaryToAlgorism(binary: String): Int {
        var max = binary.length
        var result = 0
        for (i in 0 until max) {
            result += (binary[i].code - '0'.code) shl (max - i - 1)
        }
        return result
    }

    /**
     * 十进制转换为十六进制
     *
     * @param algorism
     * @return
     */
    fun algorismToHEXString(algorism: Int): String {
        var result = Integer.toHexString(algorism)
        if (result.length % 2 == 1) {
            result = "0$result"
        }
        result = result.uppercase()
        return result
    }

    /**
     * 十六进制字符串补零
     *
     * @param str
     * @param maxLength
     * @return
     */
    fun patchHexString(str: String, maxLength: Int): String {
        var temp = str
        while (temp.length < maxLength) {
            temp = "0$temp"
        }
        return temp
    }

    /**
     * 解析十六进制字符串
     *
     * @param s
     * @param defaultInt
     * @param radix
     * @return
     */
    fun parseToInt(s: String?, defaultInt: Int, radix: Int): Int {
        var i = 0
        try {
            if (s != null) {
                i = s.toInt(radix)
            }
        } catch (e: NumberFormatException) {
            i = defaultInt
        }
        return i
    }

    /**
     * 解析十六进制字符串
     *
     * @param s
     * @param defaultInt
     * @return
     */
    fun parseToInt(s: String?, defaultInt: Int): Int {
        var i = 0
        try {
            if (s != null) {
                i = s.toInt(16)
            }
        } catch (e: NumberFormatException) {
            i = defaultInt
        }
        return i
    }

    /**
     * 将十六进制字符串转换为字节数组
     *
     * @param hex
     * @return
     */
    @Throws(IllegalArgumentException::class)
    fun hexToByte(hex: String): ByteArray {
        val max = hex.length / 2
        val bytes = ByteArray(max)
        var binary = 0
        for (i in 0 until max) {
            binary = 0
            for (j in 0..1) {
                val b = hex[2 * i + j]
                val t = when (b) {
                    '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' -> b.code - '0'.code
                    'a', 'b', 'c', 'd', 'e', 'f' -> b.code - 'a'.code + 10
                    'A', 'B', 'C', 'D', 'E', 'F' -> b.code - 'A'.code + 10
                    else -> throw IllegalArgumentException("Invalid hex character: $b")
                }
                binary = (binary shl 4) + t
            }
            bytes[i] = binary.toByte()
        }
        return bytes
    }

    /**
     * 字节数组转16进制
     *
     * @param b
     * @return
     */
    fun byteToHex(b: ByteArray): String {
        val out = StringBuilder(b.size * 2)
        for (aB in b) {
            val st = Integer.toHexString(aB.toInt() and 0xff)
            if (st.length == 1) {
                out.append("0")
            }
            out.append(st)
        }
        return out.toString().uppercase()
    }

    /**
     * 字节数组转16进制
     *
     * @param b
     * @return
     */
    fun byteToHex2(b: ByteArray): String {
        val out = StringBuilder(b.size * 2)
        for (aB in b) {
            val st = Integer.toHexString(aB.toInt() and 0xff)
            if (st.length == 1) {
                out.append("0")
            }
            out.append(st)
        }
        return out.toString()
    }

    /**
     * 截取字节数组
     *
     * @param input
     * @param startIndex
     * @param length
     * @return
     */
    fun subByte(input: ByteArray, startIndex: Int, length: Int): ByteArray {
        val start = startIndex
        val end = start + length
        val result = ByteArray(length)
        System.arraycopy(input, start, result, 0, length)
        return result
    }
} 