package com.listshop.bff.services.impl

import com.listshop.analytics.AppInfo
import com.listshop.analytics.ClientType
import com.listshop.analytics.ListShopAnalytics
import com.listshop.bff.data.model.SemanticVersion
import com.listshop.bff.data.model.ShoppingList
import com.listshop.bff.data.remote.ApiRequiredClientVersion
import com.listshop.bff.data.state.ConnectionStatus
import com.listshop.bff.remote.UserApi
import com.listshop.bff.services.*

class SyncServiceImpl internal constructor(
    private val sessionService: SessionService,
    private val userApi: UserApi,
    private val tagService: TagService,
    private val listService: ListService,
    private val layoutService: LayoutService,
    val appInfo: AppInfo,
    val listShopAnalytics: ListShopAnalytics
) : SyncService {
    override suspend fun checkApiCompatibility(connectionStatus: ConnectionStatus): Boolean {
        val currentVersionString = appInfo.clientVersion ?: "0"
        if (connectionStatus == ConnectionStatus.Online) {
            // get the required version
            val requiredApi: ApiRequiredClientVersion = userApi.retrieveRequiredClientVersion()
            val requiredVersionString = requiredVersionForApp(requiredApi, appInfo.clientType)
            val required = SemanticVersion.create(requiredVersionString)
            val actual = SemanticVersion.create(currentVersionString)
            return SemanticVersion.isGreaterThanOrEquals(actual, required)
        }
        return true
    }

    override suspend fun getClientRequiredVersion(connectionStatus: ConnectionStatus): String {
        val currentVersionString = appInfo.clientVersion ?: "0"
        if (connectionStatus == ConnectionStatus.Online) {
            // get the required version
            val requiredApi: ApiRequiredClientVersion = userApi.retrieveRequiredClientVersion()
            val requiredVersionString = requiredVersionForApp(requiredApi, appInfo.clientType)
            return requiredVersionString
        }
        return "unknown"
    }

    private fun requiredVersionForApp(
        requiredApi: ApiRequiredClientVersion,
        clientType: ClientType
    ): String {
        return when (clientType) {
            ClientType.IOS -> requiredApi.iosMinVersion ?: "0"
            ClientType.Android -> requiredApi.androidMinVersion ?: "0"
        }
    }

    override suspend fun syncLookupData(connectionStatus: ConnectionStatus): TagTree {
        listShopAnalytics.debug("SyncServiceImpl - Begin retrieve Mapping Information sync lookup data")
        // error if offline
        if (connectionStatus == ConnectionStatus.Online) {
            // only refreshes data if we're online

            // remote call to retrieve tag lookup data (array of ApiTagLookup objects)
            tagService.retrieveTagsAndSaveLocally()

            // remote call to retrieve layout data (array of ApiMappingLayouts objects)
            // process and save layouts
            layoutService.retrieveLayoutsAndSaveLocally()

            // set last local data synced
            sessionService.setLookupDataLastSyncedToNow()
        }

        // use tag service to build tag tree and return
        // as long as tags are present locally, tag tree can be built
        return tagService.buildTagTree()
    }

    override suspend fun loadMergedShoppingList(connectionStatus: ConnectionStatus): ShoppingList? {
        return listService.mergeLocalWithServerList()

    }

    override suspend fun getMostRecentList(connectionStatus: ConnectionStatus): ShoppingList? {
        return listService.getMostRecentList(connectionStatus)


    }
}
