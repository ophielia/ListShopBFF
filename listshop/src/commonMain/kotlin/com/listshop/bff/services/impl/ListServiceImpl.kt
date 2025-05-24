package com.listshop.bff.services.impl

import com.listshop.bff.data.model.ShoppingList
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


}