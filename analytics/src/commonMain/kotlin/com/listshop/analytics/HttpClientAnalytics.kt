package com.listshop.analytics

class HttpClientAnalytics internal constructor(private val showHttpLogs: Boolean) {
    
    fun logMessage(message: String) {
        if (showHttpLogs) {
            sendEvent("httpClientMessage", "message" to message)
        }
    }
}
