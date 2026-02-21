package com.listshop.bff.usecases.navigation

import com.listshop.analytics.AnalyticsHandle
import com.listshop.analytics.debug
import com.listshop.analytics.error
import com.listshop.bff.data.bff.BFFError
import com.listshop.bff.data.bff.BFFResult
import com.listshop.bff.data.model.ListShoppingList
import com.listshop.bff.data.state.ConnectionStatus
import com.listshop.bff.data.state.TransitionViewState
import com.listshop.bff.data.state.UserSessionState
import com.listshop.bff.services.ListService
import com.listshop.bff.services.SessionService

class NavigateToListManagement(
    private val connectionStatus: ConnectionStatus,
    private val listService: ListService,
    private val sessionService: SessionService,
    private val analyticsHandle: AnalyticsHandle
) {
    suspend fun process(): BFFResult<TransitionViewState> {
        analyticsHandle.debug("NavigateToListManagementUseCase - begin use case")
        sessionService.setUserLastSeenToNow()
        val isConnected = connectionStatus == ConnectionStatus.Online
        val userLoggedIn = sessionService.currentUserSession().sessionState == UserSessionState.User
        if (!isConnected || !userLoggedIn) {
            // go to list management screen with empty list
            val wrappedLists = ListShoppingList(emptyList())
            analyticsHandle.debug("NavigateToListManagementUseCase - end use case, offline or no user")
            return BFFResult.success(TransitionViewState.ListManagementScreen(wrappedLists))
        }
        try {
            val listOfLists = listService.retrieveListOfLists()
            val wrappedLists = ListShoppingList(listOfLists)
            analyticsHandle.debug("NavigateToListManagementUseCase - end use case")
            return BFFResult.success(TransitionViewState.ListManagementScreen(wrappedLists))
        } catch (e: Exception) {
            analyticsHandle.error("Error while navigating to list management screen")
            return BFFError.errorFromException(e)
        }
    }

}
