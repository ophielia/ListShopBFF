package com.listshop.bff.services.impl

import com.listshop.analytics.ListShopAnalytics
import com.listshop.bff.data.model.ShoppingList
import com.listshop.bff.data.remote.MergeItem
import com.listshop.bff.data.remote.PostListProperties
import com.listshop.bff.data.remote.PostShoppingList
import com.listshop.bff.data.remote.PutMergeRequest
import com.listshop.bff.remote.ShoppingListApi
import com.listshop.bff.repositories.ListRepository
import com.listshop.bff.services.ListService
import com.listshop.bff.services.SessionService

class ListServiceImpl internal constructor(
    private val remoteApi: ShoppingListApi,
    private val listRepo: ListRepository,
    private val sessionService: SessionService,
    private val listShopAnalytics: ListShopAnalytics
) : ListService {
    val DEFAULT_LIST_NAME: String = "Shopping List"

    override suspend fun retrieveListOfLists(): List<ShoppingList> {
        return remoteApi.getAllShoppingLists()
    }

    override suspend fun retrieveMostRecentList(): ShoppingList? {
        // retrieve api list as bff model
        val shoppingList: ShoppingList = remoteApi.retrieveMostRecentList()
        // save server list id in session
        sessionService.setServerListId(shoppingList.externalId ?: "0")
        // deal with legends (later....)
        return saveLocallyAndReturnList(shoppingList)

    }

    override suspend fun retrieveServerList(): ShoppingList? {
        try {
            val serverList = doRetrieveServerList()
            if (serverList != null) {
                return serverList
            }
        } catch (e: Exception) {
            // swallowing this exception for now
            listShopAnalytics.error("Error while retrieving server list - ${e.message}")
        }
        try {
            return retrieveMostRecentList()
        } catch (e: Exception) {
            // swallowing this exception for now
            listShopAnalytics.error("Error while retrieving most recent list - ${e.message}")
        }
        return ShoppingList.empty()
    }

    override suspend fun retrieveServerListById(listId: String): ShoppingList? {
        return doRetrieveServerListById(listId)
    }

    override suspend fun addServerList(): String? {

        // create payload (empty in this case)
        val payload = PostShoppingList(name = null)

        val newListId = remoteApi.createList(payload)
        return newListId

    }

    override suspend fun deleteList(listIdToDelete: String) {
        remoteApi.deleteList(listIdToDelete)
    }

    override suspend fun updateListProperties(listId: String, listName: String, starterList: Boolean?) {
        val payload = PostListProperties(listId, listName, starterList)
        remoteApi.updateList(listId, payload)
        // if this is the current list, we should reload the current list to take changes into account
        if (listId.equals(sessionService.currentListSession().serverListId)) {
            doRetrieveServerList()
        }
    }

    override suspend fun retrieveLocalList(): ShoppingList? {
        return listRepo.retrieveLocalList()
    }

    override suspend fun retrieveOrCreateLocalList(): ShoppingList? {
        val shoppingList = listRepo.retrieveLocalList()
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
            listId = listId,
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

    override suspend fun clearLocalList() {
        // pull local list
        listRepo.deleteLocalList()
    }

    private suspend fun doRetrieveServerList(): ShoppingList? {
        val serverId = sessionService.currentListSession().serverListId

        return doRetrieveServerListById(serverId ?: "0")
    }

    private suspend fun doRetrieveServerListById(listId: String): ShoppingList? {
        // retrieve api list
        val shoppingList: ShoppingList = remoteApi.retrieveListById(listId )
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


}

