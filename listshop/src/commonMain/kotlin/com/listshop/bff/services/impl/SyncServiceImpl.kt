package com.listshop.bff.services.impl

import com.listshop.analytics.AppInfo
import com.listshop.analytics.ClientType
import com.listshop.analytics.ListShopAnalytics
import com.listshop.bff.data.model.SemanticVersion
import com.listshop.bff.data.model.ShoppingList
import com.listshop.bff.data.remote.ApiRequiredClientVersion
import com.listshop.bff.data.state.ConnectionStatus
import com.listshop.bff.exceptions.OfflineException
import com.listshop.bff.remote.UserApi
import com.listshop.bff.services.LayoutService
import com.listshop.bff.services.SyncService
import com.listshop.bff.services.TagService
import com.listshop.bff.services.TagTree
import com.listshop.bff.services.UserSessionService

class SyncServiceImpl  internal constructor(
    private val userSessionService: UserSessionService,
    private val userApi: UserApi,
    private val tagService: TagService,
    private val layoutService: LayoutService,
    val appInfo: AppInfo,
    val listShopAnalytics: ListShopAnalytics
) : SyncService {
    override suspend fun checkApiCompatibility(connectionStatus: ConnectionStatus): Boolean {
        val currentVersionString = appInfo.clientVersion ?: "0"
        if (connectionStatus == ConnectionStatus.Online) {
            // get the required version
            val requiredApi : ApiRequiredClientVersion  = userApi.retrieveRequiredClientVersion()
            val requiredVersionString = requiredVersionForApp(requiredApi, appInfo.clientType)
            val required = SemanticVersion.create(requiredVersionString)
            val actual = SemanticVersion.create(currentVersionString)
            return SemanticVersion.isGreaterThanOrEquals(actual, required)
        }
        return true
    }

    private fun requiredVersionForApp(
        requiredApi: ApiRequiredClientVersion,
        clientType: ClientType
    ) : String {
        return when (clientType)  {
            ClientType.IOS -> requiredApi.iosMinVersion ?: "0"
            ClientType.Android -> requiredApi.androidMinVersion ?: "0"
        }
    }

    override suspend fun syncLookupData(connectionStatus: ConnectionStatus) : TagTree {
        listShopAnalytics.debug("SyncServiceImpl - Begin retrieve Mapping Information sync lookup data")
        // error if offline
        if (connectionStatus == ConnectionStatus.Online) {
            throw OfflineException(message = "Can't reach the server - syncing lookup data")
        }

        // remote call to retrieve tag lookup data (array of ApiTagLookup objects)
        val apiTags = tagService.retrieveTagsAndSaveLocally()

        // remote call to retrieve layout data (array of ApiMappingLayouts objects)
        // process and save layouts
        layoutService.retrieveLayoutsAndSaveLocally()

        // set last local data synced
        userSessionService.setLookupDataLastSyncedToNow()

        // use tag service to build tag tree and return
//MM note - this guy could be synchronous with lookup data

        // return the tag tree
//os_log("SyncServiceImpl - skipping syncLookupData - currently offline ", log: Log.service, type: .info)
//os_log("SyncServiceImpl - syncLookupData - saved tags, now retrieving categories", log: Log.service, type: .debug)
//os_log("SyncServiceImpl - syncLookupData - retrieved layouts, now saving them", log: Log.service, type: .debug)
//os_log("SyncServiceImpl - Finished saving syncLookupData", log: Log.service, type: .info)
//os_log("SyncServiceImpl - syncLookupData - building tag tree", log: Log.service, type: .debug)
//os_log("SyncServiceImpl - syncLookupData - returning empty tag tree", log: Log.service, type: .info)
//os_log("SyncServiceImpl - syncLookupData - finished building tag tree", log: Log.service, type: .debug)


        return TagTree()
        }

    override suspend fun syncWithServerList(connectionStatus: ConnectionStatus): ShoppingList? {
        TODO("Not yet implemented")
    }

    override suspend fun getMostRecentList(connectionStatus: ConnectionStatus): ShoppingList? {
        TODO("Not yet implemented")
    }
}
