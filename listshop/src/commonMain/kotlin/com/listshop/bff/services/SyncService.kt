package com.listshop.bff.services

import com.listshop.bff.data.model.ShoppingList
import com.listshop.bff.data.state.ConnectionStatus

interface SyncService {
     suspend fun checkApiCompatibility(connectionStatus: ConnectionStatus) : Boolean
    suspend fun syncLookupData(connectionStatus: ConnectionStatus) : TagTree
    suspend fun syncWithServerList(connectionStatus: ConnectionStatus) : ShoppingList?
 suspend fun getMostRecentList(connectionStatus: ConnectionStatus): ShoppingList?


}
