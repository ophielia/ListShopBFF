package com.listshop.bff.services

import com.listshop.bff.data.model.ShoppingList
import com.listshop.bff.data.state.ConnectionStatus

interface SyncService {
    suspend fun checkApiCompatibility(connectionStatus: ConnectionStatus): Boolean
    suspend fun getClientRequiredVersion(connectionStatus: ConnectionStatus): String
    suspend fun syncLookupData(connectionStatus: ConnectionStatus): TagTree
    suspend fun loadMergedShoppingList(connectionStatus: ConnectionStatus): ShoppingList?
    suspend fun mergeLocalListWithServer()

}
