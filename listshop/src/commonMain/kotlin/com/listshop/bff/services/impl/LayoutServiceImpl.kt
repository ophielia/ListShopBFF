package com.listshop.bff.services.impl

import com.listshop.analytics.AppInfo
import com.listshop.analytics.ListShopAnalytics
import com.listshop.bff.remote.TagApi
import com.listshop.bff.repositories.SessionInfoRepository
import com.listshop.bff.services.LayoutService

class LayoutServiceImpl internal constructor(
    private val sessionRepo: SessionInfoRepository,
    private val tagApi: TagApi,
    val appInfo: AppInfo,
    val listShopAnalytics: ListShopAnalytics
) : LayoutService {

    override fun retrieveLayoutsAndSaveLocally() {
        TODO("Not yet implemented")
    }


}
