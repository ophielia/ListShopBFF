package com.listshop.bff.usecases.navigation

import com.listshop.analytics.AnalyticsHandle
import com.listshop.analytics.debug
import com.listshop.analytics.error
import com.listshop.bff.data.bff.BFFError
import com.listshop.bff.data.bff.BFFResult
import com.listshop.bff.data.model.ListShoppingList
import com.listshop.bff.data.model.ShoppingList
import com.listshop.bff.data.state.ConnectionStatus
import com.listshop.bff.data.state.TransitionViewState
import com.listshop.bff.data.state.UserSessionState
import com.listshop.bff.services.ListService
import com.listshop.bff.services.SessionService

class NavigateToListScreen(
    private val connectionStatus: ConnectionStatus,
    private val sessionService: SessionService,
    private val listService: ListService,
    private val analyticsHandle: AnalyticsHandle
) {

    suspend fun process(): BFFResult<TransitionViewState> {
        analyticsHandle.debug("NavigateToListScreen - begin use case")
        sessionService.setUserLastSeenToNow()

        val isConnected = connectionStatus == ConnectionStatus.Online
        val userLoggedIn = sessionService.currentUserSession().sessionState == UserSessionState.User
        if (!isConnected || !userLoggedIn) {
            // go to local lists
            return goToLocalList()
        } else {
            // go to server list
            return goToServerList()
        }
    }

    private suspend fun goToServerList(): BFFResult<TransitionViewState> {
        try {
            val serverList = listService.retrieveServerList() ?: ShoppingList.Factory.empty()
            val wrappedLists = getListOfLists()
            analyticsHandle.debug("NavigateToListScreen - end use case")
            return BFFResult.Companion.success(TransitionViewState.ListScreen(serverList, wrappedLists))

        } catch (e: Exception) {
            return BFFError.errorFromException(e)
        }
    }

    private suspend fun getListOfLists(): ListShoppingList {
        try {
            val listOfLists = listService.retrieveListOfLists()
            return ListShoppingList(listOfLists)
        } catch (e: Exception) {
            analyticsHandle.error("Error while retrieving list of lists")
            return ListShoppingList(emptyList())
        }
    }

    private suspend fun goToLocalList(): BFFResult<TransitionViewState> {
        try {
            val localList = listService.retrieveOrCreateLocalList() ?: ShoppingList.Factory.empty()
            val wrappedLists = getListOfLists()
            analyticsHandle.debug("NavigateToListScreen - end use case")
            return BFFResult.Companion.success(TransitionViewState.ListScreen(localList, wrappedLists))
        } catch (e: Exception) {
            return BFFError.errorFromException(e)
        }
    }


}


