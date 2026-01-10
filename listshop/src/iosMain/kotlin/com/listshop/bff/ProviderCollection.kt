package com.listshop.bff

import com.listshop.analytics.AppAnalytics
import com.listshop.bff.services.SessionService
import com.listshop.bff.ucp.DashboardUCP
import com.listshop.bff.ucp.OnboardingUCP
import com.listshop.bff.ucp.TagUCP

data class ProviderCollection (
    val onboardingUCP: OnboardingUCP,
    val dashboardUCP: DashboardUCP,
    val tagUCP: TagUCP,
    val sessionService: SessionService,
    val appAnalytics: AppAnalytics,
)
