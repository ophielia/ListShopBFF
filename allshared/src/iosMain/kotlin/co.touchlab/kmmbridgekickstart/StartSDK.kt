package co.touchlab.kmmbridgekickstart

fun legacyStartSDK(analytics: Analytics): LegacySDKHandle {
    val analyticsHandle = initAnalytics(analytics)
    return LegacySDKHandle(
        breedRepository = breedStartup(analyticsHandle),
        appAnalytics = analyticsHandle.appAnalytics,
        breedAnalytics = analyticsHandle.breedAnalytics
    )
}

fun sayHello() = "Hello from Kotlin!"