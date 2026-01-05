package com.listshop.bff.repositories

import com.listshop.bff.data.model.ShoppingList
import com.listshop.bff.data.remote.ApiLayout
import com.listshop.bff.db.ListCategoryEntity
import com.listshop.bff.db.ListItemEntity

interface ListRepository {
    fun saveListLocally(shoppingList: ShoppingList)


    fun clearLocalListData()
}
