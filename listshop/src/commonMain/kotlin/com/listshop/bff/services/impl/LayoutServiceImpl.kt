package com.listshop.bff.services.impl

import com.listshop.analytics.AppInfo
import com.listshop.analytics.ListShopAnalytics
import com.listshop.bff.data.remote.ApiLayout
import com.listshop.bff.remote.LayoutApi
import com.listshop.bff.repositories.LayoutRepository
import com.listshop.bff.services.LayoutService
import com.listshop.bff.services.SessionService

class LayoutServiceImpl internal constructor(
    private val layoutApi: LayoutApi,
    private val layoutRepo: LayoutRepository,
    private val sessionService: SessionService,
    val appInfo: AppInfo,
    val listShopAnalytics: ListShopAnalytics
) : LayoutService {

    override suspend fun retrieveLayoutsAndSaveLocally() {
        // clear all layouts
        layoutRepo.clearLayoutDataLocally()
        retrieveAndSetDefaultLayout()
        if (sessionService.currentUserSession().userToken == null) return
        retrieveAndSetUserLayouts()
    }

    private suspend fun retrieveAndSetDefaultLayout() {
        // get default layout
        val defaultLayout = layoutApi.retrieveDefaultLayout() ?: ApiLayout.empty()
        layoutRepo.saveLayoutLocally(defaultLayout)

    }

    private suspend fun retrieveAndSetUserLayouts() {
        // get user layouts
        val userLayouts = layoutApi.retrieveUserLayouts()
        // save all layouts
        userLayouts
            ?.map { layout -> layout }
            ?.forEach { layout -> layoutRepo.saveLayoutLocally(layout) }
    }

    override suspend fun clearUserLayouts() {
        // clear all layouts
        layoutRepo.clearLayoutDataLocally()
        retrieveAndSetDefaultLayout()
    }


}

