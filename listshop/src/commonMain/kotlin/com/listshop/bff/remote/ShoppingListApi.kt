package com.listshop.bff.remote

import com.listshop.bff.data.model.ShoppingList

internal interface ShoppingListApi {

    suspend fun getAllShoppingLists(): List<ShoppingList>
}
