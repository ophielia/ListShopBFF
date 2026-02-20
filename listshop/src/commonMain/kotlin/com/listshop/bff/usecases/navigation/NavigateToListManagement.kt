package com.listshop.bff.usecases.navigation

import com.listshop.analytics.AnalyticsHandle
import com.listshop.bff.data.bff.BFFError
import com.listshop.bff.data.bff.BFFErrorSubtype
import com.listshop.bff.data.bff.BFFErrorType
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
        sessionService.setUserLastSeenToNow()
        val isConnected = connectionStatus == ConnectionStatus.Online
        val userLoggedIn = sessionService.currentUserSession().sessionState == UserSessionState.User
        if (!isConnected || !userLoggedIn) {
            // go to list management screen with empty list
            val wrappedLists = ListShoppingList(emptyList())
            return BFFResult.success(TransitionViewState.ListManagementScreen(wrappedLists))
        }
        try {
            val listOfLists = listService.retrieveListOfLists()
            val wrappedLists = ListShoppingList(listOfLists)
            return BFFResult.success(TransitionViewState.ListManagementScreen(wrappedLists))
        } catch (e: Exception) {
            analyticsHandle.listShopAnalytics.error("Error while navigating to list management screen")
            val bfferror = BFFError(
                BFFErrorType.NAVIGATION,
                BFFErrorSubtype.CALL_FAILED,
                "unable to navigate to list management screen"
            )
            return BFFResult.Companion.error(bfferror)
        }
    }

}
