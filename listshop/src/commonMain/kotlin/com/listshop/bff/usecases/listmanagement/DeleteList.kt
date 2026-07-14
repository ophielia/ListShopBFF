package com.listshop.bff.usecases.listmanagement

import com.listshop.analytics.AnalyticsHandle
import com.listshop.analytics.debug
import com.listshop.analytics.error
import com.listshop.bff.data.bff.BFFError
import com.listshop.bff.data.bff.BFFResult
import com.listshop.bff.data.model.ListShoppingList
import com.listshop.bff.data.state.ConnectionStatus
import com.listshop.bff.services.ListService
import com.listshop.bff.services.UserService
import com.listshop.bff.usecases.validators.ConnectionStatusValidator

class DeleteList(
    private val connectionStatus: ConnectionStatus,
    private val listIdToDelete: String,
    private val listService: ListService,
    private val analyticsHandle: AnalyticsHandle
) : ConnectionStatusValidator {
    suspend fun process(): BFFResult<ListShoppingList> {
        analyticsHandle.debug("DeleteList - begin use case")
        try {
            checkOnlineStatus(connectionStatus)
            listService.deleteList(listIdToDelete)
            val lists = listService.retrieveListOfLists()
            val listOfLists = ListShoppingList(lists)
            analyticsHandle.debug("DeleteList - end use case")
            return BFFResult.success(value = listOfLists)

        } catch (e: Exception) {
            analyticsHandle.error("Error in DeleteList call")
            return BFFError.errorFromException(e)
        }
    }

}

