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

class UpdateList(
    private val connectionStatus: ConnectionStatus,
    private val listId: String,
    private val listName: String,
    private val isStarterList: Boolean?,
    private val listService: ListService,
    private val analyticsHandle: AnalyticsHandle
) : ConnectionStatusValidator {
    suspend fun process(): BFFResult<ListShoppingList> {
        analyticsHandle.debug("Update List Properties - begin use case")
        try {
            checkOnlineStatus(connectionStatus)
            listService.updateListProperties(listId, listName, isStarterList)
            val lists = listService.retrieveListOfLists()
            val listOfLists = ListShoppingList(lists)
            analyticsHandle.debug("Update List Properties - end use case")
            return BFFResult.success(value = listOfLists)
        } catch (e: Exception) {
            analyticsHandle.error("Error in Update List Properties call")
            return BFFError.errorFromException(e)
        }
    }

}

