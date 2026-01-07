package com.listshop.bff.ucp

import com.listshop.analytics.ListShopAnalytics
import com.listshop.bff.data.bff.BFFResult
import com.listshop.bff.data.state.ConnectionStatus
import com.listshop.bff.data.state.TransitionViewState
import com.listshop.bff.services.*
import com.listshop.bff.usecases.LegacySystemGetLaunchScreenUseCase
import com.listshop.bff.usecases.LoginUseCase
import com.listshop.bff.usecases.SystemGetLaunchScreenUseCase

class OnboardingUCP internal constructor(
    private val sessionService: UserSessionService,
    private val listService: ListService,
    private val userService: UserService,
    private val syncService: SyncService,
    private val listShopAnalytics: ListShopAnalytics
) {

    @Throws(Exception::class)
    suspend fun legacySystemGetLaunchScreen(connectionStatus: ConnectionStatus): BFFResult<TransitionViewState> {
        val useCase = LegacySystemGetLaunchScreenUseCase(connectionStatus, sessionService, userService, listService)
        return useCase.process()
    }

    @Throws(Exception::class)
    suspend fun systemGetLaunchScreen(connectionStatus: ConnectionStatus): BFFResult<Pair<TransitionViewState, TagTree>> {
        val useCase = SystemGetLaunchScreenUseCase(
            connectionStatus = connectionStatus,
            sessionService = sessionService,
            listService = listService,
            syncService = syncService,
            listShopAnalytics = listShopAnalytics
        )
        return useCase.process()
    }

    @Throws(Exception::class)
    suspend fun signIn(userName: String, password: String): BFFResult<TransitionViewState> {
        val useCase = LoginUseCase(userName, password, userService, sessionService, listService)
        return useCase.process()
    }

}


