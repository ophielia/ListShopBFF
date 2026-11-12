package com.listshop.bff.usecases.listmanagement

import com.listshop.analytics.AnalyticsHandle
import com.listshop.analytics.debug
import com.listshop.analytics.error
import com.listshop.bff.data.bff.BFFError.Companion.rawErrorFromException
import com.listshop.bff.data.bff.BFFResult
import com.listshop.bff.data.model.ListShoppingList
import com.listshop.bff.data.state.ConnectionStatus
import com.listshop.bff.services.ListService
import com.listshop.bff.services.SessionService
import com.listshop.bff.usecases.validators.ConnectionStatusValidator
import com.listshop.bff.usecases.validators.UserLoggedInValidator

class GetAllLists(
    private val connectionStatus: ConnectionStatus,
    private val listService: ListService,
    private val sessionService: SessionService,
    private val analyticsHandle: AnalyticsHandle
) : ConnectionStatusValidator, UserLoggedInValidator {
    suspend fun process(): BFFResult<ListShoppingList> {
        analyticsHandle.debug("Get All Lists - begin use case")
        try {
            checkOnlineStatus(connectionStatus)
            checkUserLoggedIn(sessionService.currentUserSession())
            val lists = listService.retrieveListOfLists()
            val listOfLists = ListShoppingList(lists)
            analyticsHandle.debug("Get All Lists - end use case")
            return BFFResult.Success(value = listOfLists)
        } catch (e: Exception) {
            analyticsHandle.error("Error in Get All Lists call")
            return BFFResult.Failure(rawErrorFromException(e))
        }
    }

}



