package com.listshop.bff.usecases

import com.listshop.bff.data.bff.BFFResult
import com.listshop.bff.data.state.DashboardViewState
import com.listshop.bff.data.state.TransitionViewState
import com.listshop.bff.services.UserSessionService

class NavigateToDashboardUseCase(
    private val sessionService: UserSessionService,
) {
    fun process(): BFFResult<TransitionViewState> {
        val goal = TransitionViewState.Dashboard(DashboardViewState.mainDashboard)
        sessionService.setUserLastSeenToNow()

        return BFFResult.success(goal)
    }

}