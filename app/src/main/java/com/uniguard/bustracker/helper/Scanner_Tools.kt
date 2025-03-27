package com.uniguard.bustracker.helper

object Scanner_Tools {
    private var baseConfig: SderbUtils? = null

    fun getUtil(): SderbUtils {
        if (baseConfig == null) {
            baseConfig = SderbUtils()
        }
        return baseConfig!!
    }
} 