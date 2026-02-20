package com.listshop.bff.usecases.navigation

import com.listshop.analytics.AnalyticsHandle
import com.listshop.bff.data.bff.BFFError
import com.listshop.bff.data.bff.BFFErrorSubtype
import com.listshop.bff.data.bff.BFFErrorType
import com.listshop.bff.data.bff.BFFResult
import com.listshop.bff.data.model.DishList
import com.listshop.bff.data.model.DishSearchParameters
import com.listshop.bff.data.model.ListShoppingList
import com.listshop.bff.data.state.ConnectionStatus
import com.listshop.bff.data.state.DashboardViewState
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
            sessionService.setUserLastSeenToNow()

            val isConnected = connectionStatus == ConnectionStatus.Online
            val userLoggedIn = sessionService.currentUserSession().sessionState == UserSessionState.User
            if (!isConnected || !userLoggedIn) {
                // go to list management screen with empty list
                val wrappedList = DishList(emptyList())
                return BFFResult.success(TransitionViewState.DishManagementScreen(wrappedList))
            }
            try {
                val searchParameters = sessionService.currentDishMemory().searchParameters
                val dishList = dishService.retrieveDishList(searchParameters)
                val wrappedLists = DishList(dishList)
                return BFFResult.success(TransitionViewState.DishManagementScreen(wrappedLists))
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
/*
        os_log("GoToDishManagementScreenUseCase, starting", log: Log.model, type: .debug)
        sessionService.setUserLastSeen()
        let session = sessionService.userSession
        var dishSearchParameters = DishSearchParameters()
        dishSearchParameters.currentFilterList = sessionService.userSession.sessionStateDish?.currentFilterList ?? []
        dishSearchParameters.sortDirection = sessionService.userSession.sessionStateDish?.sortDirection
        dishSearchParameters.sortKey = sessionService.userSession.sessionStateDish?.sortKey
        if session.sessionState == .User && connectionStatus == .connected {
            // retrieve list of dishes

            firstly {
                try dishService.searchDishes(searchParameters: dishSearchParameters)
            }
                    .done { [unowned self] listOfDishes in
                        onComplete(.success(TransitionViewState.dishManagementScreen(listOfDishes)))
                    }
                    .catch { error in
                        os_log("Error: %{public}@ - in GoToDishManagementScreenUseCase", log: Log.usecase, type: .error, error.localizedDescription)
                        let lse = ListShopError(type: .core, title: "CantRetrieveServerList", message: "Error while retrieving server list")
                        self.onComplete(.failure(lse))
                    }
        } else {
            // return empty list of lists (screen will be shown - but blocked)
            onComplete(.success(TransitionViewState.dishManagementScreen([])))

        }

 */
}
