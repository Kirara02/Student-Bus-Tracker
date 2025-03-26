package com.uniguard.bustracker.core.network.interceptor

import com.uniguard.bustracker.core.data.datasource.local.SettingDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.net.URL

internal class HttpRequestInterceptor(
    private val dataStore: SettingDataStore
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val currentUrl = originalRequest.url.toString()
        
        // Get the base URL from DataStore
        val baseUrl = runBlocking { dataStore.url.first() }
        
        // Only modify the request if we need to change the base URL
        val newRequest = if (shouldRebuildUrl(currentUrl, baseUrl)) {
            rebuildRequestWithNewBaseUrl(originalRequest, baseUrl)
        } else {
            originalRequest
        }

        return chain.proceed(newRequest)
    }
    
    private fun shouldRebuildUrl(currentUrl: String, baseUrl: String): Boolean {
        // Check if the current URL needs to be modified
        // This prevents unnecessary URL rebuilding and potential infinite loops
        return !currentUrl.startsWith(baseUrl)
    }
    
    private fun rebuildRequestWithNewBaseUrl(originalRequest: Request, baseUrl: String): Request {
        val oldUrl = originalRequest.url
        
        // Construct the new URL with the base URL from DataStore
        val newUrl = "${baseUrl.trimEnd('/')}/${oldUrl.encodedPath.trimStart('/')}"
        
        // Build a new request with the updated URL
        return originalRequest.newBuilder()
            .url(newUrl)
            .build()
    }
}