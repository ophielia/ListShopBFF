package com.listshop.analytics

class AppAnalytics internal constructor() {
    
    fun appStarted() {
        sendEvent("appStarted")
    }
}