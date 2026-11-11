package com.listshop.bff.ucp

import com.listshop.analytics.AnalyticsHandle
import com.listshop.bff.data.bff.BFFResult
import com.listshop.bff.data.model.ShoppingList
import com.listshop.bff.data.state.ConnectionStatus
import com.listshop.bff.services.ListService
import com.listshop.bff.services.SessionService
import com.listshop.bff.usecases.list.CheckOffItem
import com.listshop.bff.usecases.list.GetCurrentList

class ListUCP internal constructor(
    private val listService: ListService,
    private val sessionService: SessionService,
    private val analyticsHandle: AnalyticsHandle
) {
    @Throws(Exception::class)
    suspend fun getCurrentList(connectionStatus: ConnectionStatus): BFFResult<ShoppingList> {
        val useCase = GetCurrentList(
            connectionStatus = connectionStatus,
            listService = listService,
            analyticsHandle = analyticsHandle,
            sessionService = sessionService
        )
        return useCase.process()
    }

    @Throws(Exception::class)
    suspend fun checkOffItem(
        listId: String?,
        itemId: String?,
        tagId: String,
        checkedOff: Boolean,
        connectionStatus: ConnectionStatus
    ): BFFResult<ShoppingList> {
        val useCase = CheckOffItem(
            listId = listId,
            itemId = itemId,
            tagId = tagId,
            checkedOff = checkedOff,
            connectionStatus = connectionStatus,
            sessionService = sessionService,
            listService = listService,
            analyticsHandle = analyticsHandle
        )
        return useCase.process()
    }
}


