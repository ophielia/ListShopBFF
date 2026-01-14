package com.listshop.bff.remote

import com.listshop.bff.data.model.ShoppingList
import com.listshop.bff.data.remote.PutMergeRequest

internal interface ShoppingListApi {

    suspend fun getAllShoppingLists(): List<ShoppingList>
    suspend fun retrieveMostRecentList(): ShoppingList
    suspend fun mergeLocalListWithServer(listMergeRequest: PutMergeRequest): ShoppingList
}
