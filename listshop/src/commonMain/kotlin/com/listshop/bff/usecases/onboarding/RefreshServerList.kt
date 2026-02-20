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

class RefreshServerList(
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
//MM usecasetodo - looks like not used
/*

//
//  RefreshServerListUseCase.swift
//  SeedBeta
//
//  Created by Margaret Martin on 07/07/2019.
//  Copyright © 2019 Margaret Martin. All rights reserved.
//

import Foundation
import PromiseKit

public class RefreshServerListUseCase: UseCase {

    typealias OnboardingUseCaseResult = Swift.Result<TransitionViewState, ListShopError>

    let onComplete: (OnboardingUseCaseResult) -> Void
    let onStart: () -> Void
    let listService: ListService
    let sessionService: UserSessionService
    let connectionStatus: ListShopConnectionStatus

    init(connectionStatus: ListShopConnectionStatus,
         listService: ListService,
         sessionService: UserSessionService,
         onStart: (() -> Void)?,
         onComplete: ((OnboardingUseCaseResult) -> Void)?) {
        self.listService = listService
        self.sessionService = sessionService
        self.connectionStatus = connectionStatus
        self.onStart = onStart ?? {
        }
        self.onComplete = onComplete ?? { result in
        }
    }

    public func start() {
        self.onStart()

        if connectionStatus != .connected {
            let lse = ListShopError(type: .network, title: "CantSyncServerList", message: "Can't sync server list while offline.")
            self.onComplete(.failure(lse))
        }
        // need to do refresh lookup data first
        // sync local list
        firstly {
            listService.refreshAndSaveServerList()
        }.done { [weak self] shoppingList in
            // set last local refresh in session
            self?.sessionService.setServerListId(list: shoppingList.externalId ?? 0)
            self?.goToListScreen(with: shoppingList)

        }.catch { error in
            print("Error: \(error) while refreshing server list")
            let lse = ListShopError(type: .core, title: "CantSyncServerList", message: "Error while syncing local list")
            self.onComplete(.failure(lse))
        }
    }

    private func goToListScreen(with shoppingList: ShoppingList) -> () {
        let bgq = DispatchQueue.global(qos: .userInitiated)
        firstly { () -> Promise<[ShoppingList]> in
            // make sure to finish syncing local data first before moving to local list
            try listService.retrieveListOfLists()
        }.done { listOfLists in
            self.onComplete(.success(TransitionViewState.listScreen(shoppingList, listOfLists)))
        }.catch { error in
            print("Error: \(error) while syncing local list")
            let lse = ListShopError(type: .core, title: "Can't get the lookup data", message: "Error while retrieving lookup data")
            self.onComplete(.failure(lse))
        }
    }


}


 */
