package com.listshop.bff.ucp

import com.listshop.analytics.AnalyticsHandle
import com.listshop.bff.data.bff.BFFResult
import com.listshop.bff.data.model.ShoppingList
import com.listshop.bff.data.state.ConnectionStatus
import com.listshop.bff.services.ListService
import com.listshop.bff.usecases.list.GetCurrentList

class ListUCP internal constructor(
    private val listService: ListService,
    private val analyticsHandle: AnalyticsHandle
) {


    @Throws(Exception::class)
    suspend fun getCurrentList(connectionStatus: ConnectionStatus): BFFResult<ShoppingList> {
        val useCase = GetCurrentList(
            connectionStatus = connectionStatus,
            listService = listService,
            analyticsHandle = analyticsHandle
        )
        return useCase.process()
    }


}


