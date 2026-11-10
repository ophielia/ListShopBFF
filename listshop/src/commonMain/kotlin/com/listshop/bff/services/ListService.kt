package com.listshop.bff.services

import com.listshop.bff.data.model.ShoppingList

interface ListService {

    suspend fun retrieveListOfLists(): List<ShoppingList>
    suspend fun retrieveServerList(): ShoppingList?
    suspend fun retrieveOrCreateLocalList(): ShoppingList?
    suspend fun mergeLocalWithServerList(): ShoppingList?
    suspend fun clearLocalList()
    suspend fun retrieveMostRecentList(): ShoppingList
    suspend fun retrieveServerListById(listId: String): ShoppingList?
    suspend fun retrieveLocalList(): ShoppingList?
    suspend fun addServerList(): String
    suspend fun deleteList(listIdToDelete: String)
    suspend fun updateListProperties(listId: String, listName: String, starterList: Boolean?)
    suspend fun checkOffItemLocally(listId: String?, itemId: String?, tagId: String, checkedOff: Boolean)
    suspend fun checkOffItemOnServer(listId: String, validatedItemId: String, tagId: String, checkedOff: Boolean) {

    }


}
