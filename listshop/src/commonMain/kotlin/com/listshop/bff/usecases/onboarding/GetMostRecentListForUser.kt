package com.listshop.bff.usecases.onboarding

import com.listshop.bff.data.bff.BFFResult
import com.listshop.bff.data.model.ListShoppingList
import com.listshop.bff.data.state.ConnectionStatus
import com.listshop.bff.data.state.OnboardingViewState
import com.listshop.bff.data.state.TransitionViewState
import com.listshop.bff.data.state.UserSessionState
import com.listshop.bff.services.ListService
import com.listshop.bff.services.UserService
import com.listshop.bff.services.SessionService

class GetMostRecentListForUser(
    private val connectionStatus: ConnectionStatus,
    private val sessionService: SessionService,
    private val userService: UserService,
    private val listService: ListService
) {

    suspend fun process(): BFFResult<TransitionViewState> {
         //MM nfl - skipping checking api compatibility for now
        //        - skipping first seen (will need userLastSeen)
        val session = sessionService.currentUserSession()

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
//MM usecasetodo NOT SURE THIS IS USED
/*

//
//  GoToCurrentShopUseCase.swift
//  SeedBeta
//
//  Created by Margaret Martin on 6/12/19.
//  Copyright © 2019 Margaret Martin. All rights reserved.
//

import Foundation
import PromiseKit
import os

public class GetMostRecentListForUserUseCase: UseCase {

    typealias GetMostRecentListForUserResult = Swift.Result<TransitionViewState, ListShopError>

    let onComplete: (GetMostRecentListForUserResult) -> Void
    let listService: ListService
    let sessionService: UserSessionService

    init(listService: ListService,
         sessionService: UserSessionService,
         onComplete: ((GetMostRecentListForUserResult) -> Void)?) {
        self.listService = listService
        self.sessionService = sessionService
        self.onComplete = onComplete ?? { _ in
        }
    }

    public func start() {
        os_log("GetMostRecentListForUser - starting use case.", log: Log.usecase, type: .info)

        sessionService.setUserLastSeen()
        // retrieve server list
        firstly {
            listService.retrieveMostRecentList()
        }.done { [unowned self] shoppingList in
            os_log("GetMostRecentListForUser - retrieved list - success", log: Log.usecase, type: .info)
            goToListScreen(with: shoppingList)
        }.catch { error in
            os_log("Error: %{public}@ - in GetMostRecentListForUserUseCase", log: Log.usecase, type: .error, error.localizedDescription)
            let lse = ListShopError(type: .core, title: "CantRetrieveServerList", message: "Error while retrieving server list")
            self.onComplete(.failure(lse))
        }

    }

    private func goToListScreen(with shoppingList: ShoppingList) {
        firstly { () -> Promise<[ShoppingList]> in
            // make sure to finish syncing local data first before moving to local list
            try listService.retrieveListOfLists()
        }.done { listOfLists in
            self.onComplete(.success(TransitionViewState.listScreen(shoppingList, listOfLists)))
        }.catch { error in
            os_log("Error: %{public}@ - in GetMostRecentListForUserUseCase", log: Log.usecase, type: .error, error.localizedDescription)
            let lse = ListShopError(type: .core, title: "Can't get the lookup data", message: "Error while retrieving lookup data")
            self.onComplete(.failure(lse))
        }
    }
}

 */
