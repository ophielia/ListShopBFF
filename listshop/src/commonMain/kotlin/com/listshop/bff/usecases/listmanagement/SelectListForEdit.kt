package com.listshop.bff.usecases.listmanagement

import com.listshop.analytics.AnalyticsHandle
import com.listshop.analytics.debug
import com.listshop.analytics.error
import com.listshop.bff.data.bff.BFFError
import com.listshop.bff.data.bff.BFFResult
import com.listshop.bff.data.model.ListShoppingList
import com.listshop.bff.data.model.ShoppingList
import com.listshop.bff.data.state.ConnectionStatus
/*
// should load list for edit
 - retrieve from api
 - replace list locally in db
 - set id as current id
 - return list
*/
import com.listshop.bff.services.ListService
import com.listshop.bff.usecases.validators.ConnectionStatusValidator

class SelectListForEdit(
    private val connectionStatus: ConnectionStatus,
    private val listId: String,
    private val listService: ListService,
    private val analyticsHandle: AnalyticsHandle
) : ConnectionStatusValidator {
    suspend fun process(): BFFResult<ShoppingList> {
        analyticsHandle.debug("Select List - begin use case")
        try {
            checkOnlineStatus(connectionStatus)
            val shoppingList = listService.retrieveServerListById(listId)
            analyticsHandle.debug("Select List - end use case")
            // exception should be thrown if list wasn't found, or there was an issue saving it
            // so we're just going to swallow the null and return an empty list
            return BFFResult.success(value = shoppingList ?: ShoppingList.empty())

        } catch (e: Exception) {
            analyticsHandle.error("Error in Select List for Edit call")
            return BFFError.errorFromException(e)
        }
    }

}

