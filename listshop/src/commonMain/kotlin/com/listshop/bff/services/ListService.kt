package com.listshop.bff.services

import com.listshop.bff.data.model.ShoppingList
import com.listshop.bff.data.state.ConnectionStatus

interface ListService {

    suspend fun retrieveListOfLists(): List<ShoppingList>
    suspend fun getMostRecentList(connectionStatus: ConnectionStatus): ShoppingList?
}
