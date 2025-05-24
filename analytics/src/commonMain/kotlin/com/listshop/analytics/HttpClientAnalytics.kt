package com.listshop.analytics

class HttpClientAnalytics internal constructor() {
    
    fun logMessage(message: String) {
        sendEvent("httpClientMessage", "message" to message)
    }
}