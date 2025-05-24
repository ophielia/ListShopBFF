package com.listshop.bff

import android.content.Context
import com.listshop.analytics.Analytics
import com.listshop.analytics.AppInfo
import com.listshop.analytics.initAnalytics

//MM nfl add build info here
fun startSDK(analytics: Analytics, context: Context,
             appInfo : AppInfo
): SDKHandle {

    val analyticsHandle = initAnalytics(analytics)
    val locator = AndroidServiceLocator(context, analyticsHandle, appInfo)

    return SDKHandle(
        appAnalytics = analyticsHandle.appAnalytics,
        tagUCP = locator.tagUCP,
        sessionService = locator.sessionService,
        onboardingUCP = locator.onboardingUCP,
        dashboardUCP = locator.dashboardUCP

    )
}