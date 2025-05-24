package com.listshop.bff.ucp

import com.listshop.analytics.ListShopAnalytics
import com.listshop.bff.data.bff.BFFResult
import com.listshop.bff.data.state.ConnectionStatus
import com.listshop.bff.data.state.TransitionViewState
import com.listshop.bff.services.ListService
import com.listshop.bff.services.UserService
import com.listshop.bff.services.UserSessionService
import com.listshop.bff.usecases.LoginUseCase
import com.listshop.bff.usecases.SystemGetLaunchScreenUseCase

class OnboardingUCP internal constructor(
    private val sessionService: UserSessionService,
    private val listService: ListService,
    private val userService : UserService,
    private val listShopAnalytics: ListShopAnalytics
) {

    @Throws(Exception::class)
    suspend fun systemGetLaunchScreen(connectionStatus: ConnectionStatus): BFFResult<TransitionViewState> {
        val useCase = SystemGetLaunchScreenUseCase(connectionStatus,sessionService,userService, listService)
        return useCase.process()
    }

    @Throws(Exception::class)
    suspend fun signIn(userName: String, password: String): BFFResult<TransitionViewState> {
        val useCase = LoginUseCase(userName, password, userService, sessionService, listService)
        return useCase.process()
    }

}


