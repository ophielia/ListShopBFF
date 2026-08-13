package com.listshop.bff.usecases.list

import com.listshop.analytics.AnalyticsHandle
import com.listshop.analytics.debug
import com.listshop.analytics.error
import com.listshop.bff.data.bff.BFFError
import com.listshop.bff.data.bff.BFFResult
import com.listshop.bff.data.model.ShoppingList
import com.listshop.bff.data.state.ConnectionStatus
import com.listshop.bff.data.state.UserSessionState
import com.listshop.bff.services.ListService
import com.listshop.bff.services.SessionService
import com.listshop.bff.usecases.validators.ConnectionStatusValidator

class GetCurrentList(
    private val connectionStatus: ConnectionStatus,
    private val sessionService: SessionService,
    private val listService: ListService,
    private val analyticsHandle: AnalyticsHandle
) : ConnectionStatusValidator {
    suspend fun process(): BFFResult<ShoppingList> {
        analyticsHandle.debug("Get Current List - begin use case")
        try {

            val session = sessionService.currentUserSession()
            val isOnline = connectionStatus == ConnectionStatus.Online

            val shoppingList: ShoppingList? = when (session.sessionState) {
                UserSessionState.User ->
                    // server list
                    if (isOnline) {
                        listService.retrieveServerList()
                    } else {
                        listService.retrieveOrCreateLocalList()
                    }

                else ->
                    // login
                    listService.retrieveOrCreateLocalList()
            }

            analyticsHandle.debug("Get Current List - end use case")
            return BFFResult.success(value = shoppingList ?: ShoppingList.empty())
        } catch (e: Exception) {
            analyticsHandle.error("Error in Get Current List call")
            return BFFError.Companion.errorFromException(e)
        }
    }

}
