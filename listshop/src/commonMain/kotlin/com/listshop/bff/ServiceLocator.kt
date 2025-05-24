package com.listshop.bff

import com.listshop.analytics.AppAnalytics
import com.listshop.analytics.HttpClientAnalytics
import com.listshop.analytics.ListShopAnalytics
import com.listshop.bff.services.UserSessionService
import com.listshop.bff.ucp.DashboardUCP
import com.listshop.bff.ucp.OnboardingUCP
import com.listshop.bff.ucp.TagUCP

internal interface ServiceLocator {
    val tagUCP: TagUCP
    val onboardingUCP: OnboardingUCP
    val dashboardUCP: DashboardUCP
    val sessionService: UserSessionService
    val appAnalytics: AppAnalytics
    val listShopAnalytics: ListShopAnalytics
    val httpClientAnalytics: HttpClientAnalytics
}
