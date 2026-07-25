package com.listshop.bff.ucp

import com.listshop.analytics.AnalyticsHandle
import com.listshop.bff.data.bff.BFFResult
import com.listshop.bff.data.model.ListShoppingList
import com.listshop.bff.data.state.ConnectionStatus
import com.listshop.bff.services.ListService
import com.listshop.bff.usecases.listmanagement.AddList
import com.listshop.bff.usecases.listmanagement.DeleteList

class ListUCP internal constructor(
    private val listService: ListService,
    private val analyticsHandle: AnalyticsHandle
) {

    @Throws(Exception::class)
    suspend fun addList(connectionStatus: ConnectionStatus): BFFResult<ListShoppingList> {
        val useCase = AddList(
            connectionStatus = connectionStatus,
            listService = listService,
            analyticsHandle = analyticsHandle
        )
        return useCase.process()
    }

    @Throws(Exception::class)
    suspend fun deleteList(connectionStatus: ConnectionStatus,listId:String): BFFResult<ListShoppingList> {
        val useCase = DeleteList(
            connectionStatus = connectionStatus,
            listIdToDelete = listId,
            listService = listService,
            analyticsHandle = analyticsHandle
        )
        return useCase.process()
    }
}


