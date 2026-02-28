package com.listshop.bffpoc

import com.listshop.analytics.Analytics
import com.listshop.analytics.AppInfo
import com.listshop.analytics.initAnalytics
import com.listshop.bff.getProviders

fun startSDK(analytics: Analytics, appInfo : AppInfo): SDKHandle {
    val analyticsHandle = initAnalytics(analytics, false)
    val providerCollection = getProviders(analyticsHandle, appInfo)
    return SDKHandle(
        tagUCP = providerCollection.tagUCP,
        onboardingUCP = providerCollection.onboardingUCP,
        dashboardUCP = providerCollection.dashboardUCP,
        systemUCP = providerCollection.systemUCP,
        sessionService = providerCollection.sessionService,
        appAnalytics = providerCollection.appAnalytics
    )
}

