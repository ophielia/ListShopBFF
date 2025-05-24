package com.listshop.bff.usecases

import com.listshop.bff.data.bff.BFFResult
import com.listshop.bff.data.model.ListShoppingList
import com.listshop.bff.data.state.ConnectionStatus
import com.listshop.bff.data.state.OnboardingViewState
import com.listshop.bff.data.state.TransitionViewState
import com.listshop.bff.data.state.UserSessionState
import com.listshop.bff.services.ListService
import com.listshop.bff.services.UserService
import com.listshop.bff.services.UserSessionService

class SystemGetLaunchScreenUseCase(
    private val connectionStatus: ConnectionStatus,
    private val sessionService: UserSessionService,
    private val userService: UserService,
    private val listService: ListService
) {

    suspend fun process(): BFFResult<TransitionViewState> {
         //MM nfl - skipping checking api compatibility for now
        //        - skipping first seen (will need userLastSeen)
        val session = sessionService.currentSession()

        return when (session.sessionState) {
            UserSessionState.Anon, UserSessionState.UserLoggedOut, UserSessionState.AnonNoList -> {
                val goal = TransitionViewState.Onboarding(OnboardingViewState.Choose)
                BFFResult.success(goal)
            }

            UserSessionState.User -> goToListOfLists()

        }
    }

    private suspend fun goToListOfLists() : BFFResult<TransitionViewState> {
        // authenticate user
        userService.authenticateUser()
        val listOfLists = listService.retrieveListOfLists()
        val wrappedLists = ListShoppingList(listOfLists)
        return BFFResult.success(TransitionViewState.ListManagementScreen(wrappedLists))
    }


}