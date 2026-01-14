package com.listshop.bff.services.impl

import com.listshop.analytics.ListShopAnalytics
import com.listshop.bff.data.model.ShoppingList
import com.listshop.bff.data.remote.MergeItem
import com.listshop.bff.data.remote.PutMergeRequest
import com.listshop.bff.data.state.ConnectionStatus
import com.listshop.bff.remote.ShoppingListApi
import com.listshop.bff.repositories.ListRepository
import com.listshop.bff.services.ListService
import com.listshop.bff.services.SessionService

class ListServiceImpl internal constructor(
    private val remoteApi: ShoppingListApi,
    private val listRepo: ListRepository,
    private val sessionService: SessionService,
    private val listShopAnalytics : ListShopAnalytics
) : ListService {
    override suspend fun retrieveListOfLists(): List<ShoppingList> {
        return remoteApi.getAllShoppingLists()
    }

    override suspend fun getMostRecentList(connectionStatus: ConnectionStatus): ShoppingList? {
        // retrieve api list as bff model
        val shoppingList: ShoppingList = remoteApi.retrieveMostRecentList()
        // save server list id in session
        sessionService.setServerListId(shoppingList.externalId ?: "0")
        // deal with legends (later....)
        return saveLocallyAndReturnList(shoppingList)

    }

    private fun saveLocallyAndReturnList(shoppingList: ShoppingList): ShoppingList? {
        // save as local list
        listRepo.saveListLocally(shoppingList)
        // update session info
        sessionService.setServerListId(shoppingList.externalId ?: "")
        sessionService.setLocalListUpdated()
        // return list
        return shoppingList
    }

    override suspend fun retrieveOrCreateLocalList(): ShoppingList? {
        val shoppingList =  listRepo.retrieveLocalList()
        if (shoppingList == null) {
            return listRepo.createAndSaveLocalList()
        }
        return shoppingList
    }

    override suspend fun mergeLocalWithServerList(): ShoppingList? {
        // pull local list
        val shoppingList = listRepo.retrieveLocalList()
        val listId = shoppingList?.externalId ?: ""

        if (shoppingList == null || listId.trim().length == 0) {
            return null;
        }
        // convert into PutMergeRequest
        val mergeItemList =
            shoppingList?.categories?.flatMap { it.items }?.map { MergeItem.create(modelItem = it, listId = listId) }
        val listMergeRequest = PutMergeRequest(
            listId = listId.toLong(),
            lastChanged = shoppingList?.lastLocalChange,
            layoutId = shoppingList?.layoutId?.toLong() ?: 0,
            mergeItems = mergeItemList ?: emptyList()
        )

        // make call to merge
        val mergedList = remoteApi.mergeLocalListWithServer(listMergeRequest)

        // update list info
        sessionService.setLocalLastSynced()
        sessionService.setServerListLastSynced()

        // save result locally and return
        return saveLocallyAndReturnList(mergedList)
    }


}
