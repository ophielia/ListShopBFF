package com.listshop.bff.usecases.list

import com.listshop.analytics.AnalyticsHandle
import com.listshop.analytics.debug
import com.listshop.analytics.error
import com.listshop.bff.data.bff.BFFError
import com.listshop.bff.data.bff.BFFResult
import com.listshop.bff.data.model.ShoppingList
import com.listshop.bff.data.state.ConnectionStatus
import com.listshop.bff.services.ListService
import com.listshop.bff.usecases.validators.ConnectionStatusValidator

class GetCurrentList(
    private val connectionStatus: ConnectionStatus,
    private val listService: ListService,
    private val analyticsHandle: AnalyticsHandle
) : ConnectionStatusValidator {
    suspend fun process(): BFFResult<ShoppingList> {
        analyticsHandle.debug("Get Current List - begin use case")
        try {
            checkOnlineStatus(connectionStatus)
            val list = listService.retrieveServerList()
            analyticsHandle.debug("Get Current List - end use case")
            return BFFResult.success(value = list ?: ShoppingList.empty())
        } catch (e: Exception) {
            analyticsHandle.error("Error in Get Current List call")
            return BFFError.Companion.errorFromException(e)
        }
    }

}
