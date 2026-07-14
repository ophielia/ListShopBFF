package com.listshop.bffpoc

import com.listshop.analytics.AppAnalytics
import com.listshop.bff.services.SessionService
import com.listshop.bff.ucp.DashboardUCP
import com.listshop.bff.ucp.ListManagementUCP
import com.listshop.bff.ucp.ListUCP
import com.listshop.bff.ucp.OnboardingUCP
import com.listshop.bff.ucp.SystemUCP
import com.listshop.bff.ucp.TagUCP

data class SDKHandle(
    val tagUCP: TagUCP,
    val listManagementUCP: ListManagementUCP,
    val listUCP: ListUCP,
    val onboardingUCP: OnboardingUCP,
    val dashboardUCP: DashboardUCP,
    val systemUCP: SystemUCP,
    val sessionService: SessionService,
    val appAnalytics: AppAnalytics
)
