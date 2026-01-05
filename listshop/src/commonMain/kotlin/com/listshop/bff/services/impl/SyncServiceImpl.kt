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
            val requiredApi: ApiRequiredClientVersion = userApi.retrieveRequiredClientVersion()
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
            userSessionService.setLookupDataLastSyncedToNow()
        }

        // use tag service to build tag tree and return
        // as long as tags are present locally, tag tree can be built
        return tagService.buildTagTree()
    }

    override suspend fun loadMergedShoppingList(connectionStatus: ConnectionStatus): ShoppingList? {
        return null

        /*
        ios code

                os_log("SyncServiceImpl - beginning syncServerList", log: Log.service, type: .info)
        let userLoggedIn = checkUserLoggedIn()

        if connectionStatus != .connected || !userLoggedIn {
            return listService.retrieveLocalList()
        } else {
            return try doMergeLocalAndServerList()
        }


            private func checkUserLoggedIn() -> Bool {
        var sessionState = userSessionService.userSession.sessionState
        return sessionState == .User
    }


    public func retrieveLocalList() -> Promise<ShoppingList> {
        firstly {
            coreDataApi.retrieveList()
        }
                .map({ [weak self] coreList in
                    var shoppingList = ShoppingList(localShoppingList: coreList)
                    let modelList = coreList.listSourceKeys?.compactMap {
                                $0 as? ListSourceKey
                            }
                            .map {
                                ApiLegend(localSourceKey: $0)
                            }
                    let legend = self?.processLegend(api: modelList ?? [])
                    shoppingList.legend = legend
                    return shoppingList
                })
    }


    private func doSyncFromLocalList(shoppingList: ShoppingList, check userTagConflict: Bool) throws -> Promise<ShoppingList> {

        // not previously updated - merge this
        let postData = transformToMergePost(localList: shoppingList, check: userTagConflict)

        let promise = try remoteApi.mergeList(postData: postData)
                .then { apiShoppingList -> Promise<ShoppingList> in
                    var shoppingList = ShoppingList(networkShoppingList: apiShoppingList)
                    let apiLegendEntries = apiShoppingList.legend ?? []
                    let legend = self.listService.processLegend(api: apiLegendEntries)
                    shoppingList.legend = legend
                    self.userSessionService.setLocalListMerged()

                    // fire and forget saving list locally
                    _ = self.listService.replaceLocalList(shoppingList: shoppingList).done({ _ in
                        // do nothing - fire and forget
                    })

                    // process result
                    os_log("SyncServiceImpl - end createListFromLocal", log: Log.service, type: .info)

                    return Promise<ShoppingList>.value(shoppingList)
                }
        promise.catch { error in
            os_log("SyncServiceImpl - unable to sync local list:", log: Log.service, type: .error, error.localizedDescription)
        }
        return promise
    }

         */
    }

    override suspend fun getMostRecentList(connectionStatus: ConnectionStatus): ShoppingList? {
        TODO("Not yet implemented")
        //MM will be implemented in list service instead


    }
}
