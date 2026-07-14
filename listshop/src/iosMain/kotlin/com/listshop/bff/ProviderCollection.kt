package com.listshop.bff

import com.listshop.analytics.AppAnalytics
import com.listshop.bff.services.SessionService
import com.listshop.bff.ucp.DashboardUCP
import com.listshop.bff.ucp.ListManagementUCP
import com.listshop.bff.ucp.ListUCP
import com.listshop.bff.ucp.OnboardingUCP
import com.listshop.bff.ucp.SystemUCP
import com.listshop.bff.ucp.TagUCP

data class ProviderCollection (
    val onboardingUCP: OnboardingUCP,
    val dashboardUCP: DashboardUCP,
    val systemUCP: SystemUCP,
    val tagUCP: TagUCP,
    val listManagementUCP: ListManagementUCP,
    val listUCP: ListUCP,
    val sessionService: SessionService,
    val appAnalytics: AppAnalytics,
)
