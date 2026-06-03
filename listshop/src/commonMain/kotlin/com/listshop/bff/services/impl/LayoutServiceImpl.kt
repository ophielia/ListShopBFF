package com.listshop.bff.services.impl

import com.listshop.analytics.AppInfo
import com.listshop.analytics.ListShopAnalytics
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
        retrieveAndSetAllLayouts()

    }

    /* This method always retrieves and saves the default layout.  If a user
    is logged in, any layouts belonging to that user will also be retrieved and saved.
     */
    private suspend fun retrieveAndSetAllLayouts() {
        // get all layouts
        val userLayouts = layoutApi.retrieveAllLayouts()
        // save all layouts
        userLayouts
            ?.map { layout -> layout }
            ?.forEach { layout -> layoutRepo.saveLayoutLocally(layout) }
    }

    override suspend fun clearUserLayouts() {
        // clear all layouts
        layoutRepo.clearLayoutDataLocally()
        retrieveAndSetAllLayouts()
    }


}

