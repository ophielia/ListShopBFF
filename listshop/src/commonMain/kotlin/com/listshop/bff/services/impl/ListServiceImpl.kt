package com.listshop.bff.services.impl

import com.listshop.bff.data.model.ShoppingList
import com.listshop.bff.data.state.ConnectionStatus
import com.listshop.bff.remote.ShoppingListApi
import com.listshop.bff.services.ListService
import com.listshop.bff.services.UserSessionService

class ListServiceImpl internal constructor(
    private val remoteApi: ShoppingListApi,
    private val sessionService: UserSessionService
) : ListService {
    override suspend fun retrieveListOfLists(): List<ShoppingList> {
        return remoteApi.getAllShoppingLists()
    }

    override suspend fun getMostRecentList(connectionStatus: ConnectionStatus): ShoppingList? {
        TODO("Not yet implemented")

        // retrieve api list as bff model
        val shoppingList: ShoppingList = remoteApi.retrieveMostRecentList()
        // save server list id in session
        sessionService.setServerListId(shoppingList.externalId ?: "0")
        // deal with legends (later....)
        // save as local list
//MM START HERE!!!!! - save locally!!
        // return list


        /*
        list service - most recent list

            public func retrieveMostRecentList() -> Promise<ShoppingList> {
        os_log("beginning retrieveMostRecentList()", log: Log.service, type: .info)

        return firstly {
            try remoteApi.retrieveMostRecentList()
        }
                .map { [weak self] apiList in
                    if let listId = apiList.externalId {
                        self?.userSessionService.setServerListId(list: listId)
                    }
                    var shoppingList = ShoppingList(networkShoppingList: apiList)
                    let apiLegendEntries = apiList.legend ?? []
                    let legend = self?.processLegend(api: apiLegendEntries)
                    shoppingList.legend = legend
                    // fire and forget saving list locally
                    _ = self?.replaceLocalList(shoppingList: shoppingList).done({ _ in
                        // do nothing - fire and forget
                    })
                    os_log("ending retrieveMostRecentList()", log: Log.service, type: .info)
                    return shoppingList
                }
    }

         */
    }


}
