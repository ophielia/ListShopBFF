package com.listshop.bff.usecases.listmanagement

import com.listshop.analytics.AnalyticsHandle
import com.listshop.analytics.debug
import com.listshop.analytics.error
import com.listshop.bff.data.bff.BFFError
import com.listshop.bff.data.bff.BFFResult
import com.listshop.bff.data.model.ListShoppingList
import com.listshop.bff.data.model.ShoppingList
import com.listshop.bff.data.state.ConnectionStatus
import com.listshop.bff.services.ListService
import com.listshop.bff.usecases.validators.ConnectionStatusValidator

class AddList(
    private val connectionStatus: ConnectionStatus,
    private val listService: ListService,
    private val analyticsHandle: AnalyticsHandle
) : ConnectionStatusValidator {
    suspend fun process(): BFFResult<ListShoppingList> {

        analyticsHandle.debug("AddList - begin use case")
        try {
            checkOnlineStatus(connectionStatus)
            val newListId = listService.addServerList()
            analyticsHandle.debug("AddList - added list with id $newListId")
            val lists = listService.retrieveListOfLists()
            val listOfLists = ListShoppingList(lists)
            return BFFResult.success(value = listOfLists)
        } catch (e: Exception) {
            analyticsHandle.error("Error in AddList call")
            return BFFError.errorFromException(e)
        }
    }

    

}

