package com.listshop.bff.usecases.navigation

import com.listshop.analytics.AnalyticsHandle
import com.listshop.analytics.debug
import com.listshop.bff.data.bff.BFFError
import com.listshop.bff.data.bff.BFFResult
import com.listshop.bff.data.model.DishList
import com.listshop.bff.data.state.ConnectionStatus
import com.listshop.bff.data.state.TransitionViewState
import com.listshop.bff.data.state.UserSessionState
import com.listshop.bff.services.DishService
import com.listshop.bff.services.SessionService

class NavigateToDishManagement(
    private val connectionStatus: ConnectionStatus,
    private val sessionService: SessionService,
    private val dishService: DishService,
    private val analyticsHandle: AnalyticsHandle
) {
    suspend fun process(): BFFResult<TransitionViewState> {
        analyticsHandle.debug("NavigateToDishManagementUseCase - begin use case")
        sessionService.setUserLastSeenToNow()

        val isConnected = connectionStatus == ConnectionStatus.Online
        val userLoggedIn = sessionService.currentUserSession().sessionState == UserSessionState.User
        if (!isConnected || !userLoggedIn) {
            // go to list management screen with empty list
            val wrappedList = DishList(emptyList())
            analyticsHandle.debug("NavigateToDishManagementUseCase - end use case")
            return BFFResult.success(TransitionViewState.DishManagementScreen(wrappedList))
        }
        try {
            val searchParameters = sessionService.currentDishMemory().searchParameters
            val dishList = dishService.retrieveDishList(searchParameters)
            val wrappedLists = DishList(dishList)
            analyticsHandle.debug("NavigateToDishManagementUseCase - end use case")
            return BFFResult.success(TransitionViewState.DishManagementScreen(wrappedLists))
        } catch (e: Exception) {
            analyticsHandle.listShopAnalytics.error("Error while navigating to list management screen")
            return BFFError.errorFromException(e)
        }

    }
}
