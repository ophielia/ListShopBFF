package com.listshop.analytics

import co.touchlab.stately.concurrency.AtomicReference

var runAsDummy = false
var dummyAnalytics : Analytics? = null

interface Analytics {
    fun sendEvent(eventName: String, eventArgs: Map<String, Any>)
}

fun initAnalytics(analytics: Analytics): AnalyticsHandle {
     if (!AnalyticsHandler.analyticsAtom.compareAndSet(null, analytics)) {
        throw IllegalStateException("Analytics can only be set once")
    }
    return AnalyticsHandle(
        appAnalytics = AppAnalytics(),
        listShopAnalytics = ListShopAnalytics(),
        httpClientAnalytics = HttpClientAnalytics()
    )
}

fun initDummyAnalytics(analytics: Analytics): AnalyticsHandle {
    dummyAnalytics = analytics
    return AnalyticsHandle(
        appAnalytics = AppAnalytics(),
        listShopAnalytics = ListShopAnalytics(),
        httpClientAnalytics = HttpClientAnalytics()
    )
}

data class AnalyticsHandle(
    val appAnalytics: AppAnalytics,
    val listShopAnalytics: ListShopAnalytics,
    val httpClientAnalytics: HttpClientAnalytics
)

internal fun sendEvent(name: String, vararg args: Pair<String, Any>) {
    if (dummyAnalytics == null) {
    AnalyticsHandler.analyticsAtom.get()!!.sendEvent(name, args.toMap())
    } else {
        dummyAnalytics?.sendEvent(name, args.toMap())
    }
}

internal object AnalyticsHandler {
     val analyticsAtom: AtomicReference<Analytics?> = AtomicReference(null)
}