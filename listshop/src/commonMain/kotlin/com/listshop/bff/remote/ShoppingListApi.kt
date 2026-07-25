package com.listshop.bff.remote

import com.listshop.bff.data.model.ShoppingList
import com.listshop.bff.data.remote.PostListProperties
import com.listshop.bff.data.remote.PostShoppingList
import com.listshop.bff.data.remote.PutMergeRequest

internal interface ShoppingListApi {

    suspend fun getAllShoppingLists(): List<ShoppingList>
    suspend fun retrieveMostRecentList(): ShoppingList
    suspend fun mergeLocalListWithServer(listMergeRequest: PutMergeRequest): ShoppingList
    suspend fun retrieveListById(serverId: String): ShoppingList
    suspend fun createList(payload: PostShoppingList): String
    suspend fun deleteList(listIdToDelete: String)
    suspend fun updateList(listId: String, payload: PostListProperties)
}
