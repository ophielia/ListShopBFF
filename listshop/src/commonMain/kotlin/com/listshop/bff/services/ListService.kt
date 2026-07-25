package com.listshop.bff.services

import com.listshop.bff.data.model.ShoppingList
import com.listshop.bff.data.state.ConnectionStatus

interface ListService {

    suspend fun retrieveListOfLists(): List<ShoppingList>
    suspend fun retrieveServerList(): ShoppingList?
    suspend fun retrieveOrCreateLocalList(): ShoppingList?
    suspend fun mergeLocalWithServerList(): ShoppingList?
    suspend fun clearLocalList()
    suspend fun retrieveMostRecentList(): ShoppingList?
    suspend fun retrieveLocalList(): ShoppingList?
    suspend fun addServerList(): String?
    suspend fun deleteList(listIdToDelete: String)
}
