package com.listshop.bff.services.impl

import com.listshop.analytics.AppInfo
import com.listshop.analytics.ListShopAnalytics
import com.listshop.bff.data.remote.ApiLayout
import com.listshop.bff.remote.LayoutApi
import com.listshop.bff.repositories.LayoutRepository
import com.listshop.bff.services.LayoutService

class LayoutServiceImpl internal constructor(
    private val layoutApi: LayoutApi,
    private val layoutRepo: LayoutRepository,
    val appInfo: AppInfo,
    val listShopAnalytics: ListShopAnalytics
) : LayoutService {

    override suspend fun retrieveLayoutsAndSaveLocally() {
        // clear all layouts
        layoutRepo.clearLayoutDataLocally()
        // get default layout
        val defaultLayout = layoutApi.retrieveDefaultLayout() ?: ApiLayout.empty()
        // get user layouts
        val userLayouts = layoutApi.retrieveUserLayouts()
        // save all layouts
        layoutRepo.saveLayoutLocally(defaultLayout)
        listOf(userLayouts)
            .map { layout -> layout ?: ApiLayout.empty() }
            .forEach { layout -> layoutRepo.saveLayoutLocally(layout as ApiLayout) }
    }


}

