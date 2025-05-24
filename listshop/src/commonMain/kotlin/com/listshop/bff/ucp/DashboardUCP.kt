package com.listshop.bff.ucp

import com.listshop.analytics.ListShopAnalytics
import com.listshop.bff.data.bff.BFFResult
import com.listshop.bff.data.state.TransitionViewState
import com.listshop.bff.services.UserService
import com.listshop.bff.services.UserSessionService
import com.listshop.bff.usecases.LogoutUseCase
import com.listshop.bff.usecases.NavigateToDashboardUseCase

class DashboardUCP internal constructor(
    private val sessionService: UserSessionService,
    private val userService: UserService,
    private val listShopAnalytics: ListShopAnalytics
) {


    @Throws(Exception::class)
    suspend fun logout(): BFFResult<TransitionViewState> {
        val useCase = LogoutUseCase(userService)
        return useCase.process()
    }

    @Throws(Exception::class)
    suspend fun navigateToDashboard(): BFFResult<TransitionViewState> {
        val useCase = NavigateToDashboardUseCase(sessionService)
        return useCase.process()
    }

}


