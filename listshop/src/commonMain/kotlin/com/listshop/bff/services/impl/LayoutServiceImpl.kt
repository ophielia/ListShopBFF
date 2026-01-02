package com.listshop.bff.services.impl

import com.listshop.analytics.AppInfo
import com.listshop.analytics.ListShopAnalytics
import com.listshop.bff.remote.LayoutApi
import com.listshop.bff.repositories.LayoutRepository
import com.listshop.bff.repositories.SessionInfoRepository
import com.listshop.bff.repositories.TagRepository
import com.listshop.bff.services.LayoutService

class LayoutServiceImpl internal constructor(
    private val sessionRepo: SessionInfoRepository,
    private val layoutApi: LayoutApi,
    private val layoutRepo: LayoutRepository,
    private val tagRepo: TagRepository,
    val appInfo: AppInfo,
    val listShopAnalytics: ListShopAnalytics
) : LayoutService {

    override suspend fun retrieveLayoutsAndSaveLocally() {
        // get default layout
        val defaultLayout = layoutApi.retrieveDefaultLayout()
        // get user layouts
        val userLayouts = layoutApi.retrieveUserLayouts()
        // save all layouts


/*
        if connectionStatus != .connected {
            os_log("SyncServiceImpl - skipping retrieveMappingLayouts - currently offline ", log: Log.service, type: .info)
            return Promise.value([])
        }

        let defaultPromise = try remoteApi.retrieveDefaultLayout()

            let userPromise = try remoteApi.retrieveUserLayouts()

                return when(fulfilled: defaultPromise, userPromise)
                .then { defaultLayout, userLayouts -> Promise<[ApiMappingLayout]> in
                    var collectedLayouts = [defaultLayout]
                    userLayouts.forEach {
                        collectedLayouts.append($0)
                    }
                    return Promise.value(collectedLayouts)
                }

        os_log("SyncServiceImpl - Begin retrieve Mapping Information retrieve mapping layouts", log: Log.service, type: .info)

     */
    }


}
