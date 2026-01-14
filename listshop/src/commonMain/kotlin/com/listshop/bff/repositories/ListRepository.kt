package com.listshop.bff.repositories

import com.listshop.bff.data.model.ShoppingList

interface ListRepository {
    fun saveListLocally(shoppingList: ShoppingList)

    fun retrieveLocalList(): ShoppingList?

    fun clearLocalListData()
    fun createAndSaveLocalList(): ShoppingList?
}
