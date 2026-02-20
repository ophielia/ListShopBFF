package com.listshop.bff.usecases.navigation

import com.listshop.bff.data.bff.BFFResult
import com.listshop.bff.data.state.DashboardViewState
import com.listshop.bff.data.state.TransitionViewState
import com.listshop.bff.services.SessionService

class NavigateToDashboard(
    private val sessionService: SessionService,
) {
    fun process(): BFFResult<TransitionViewState> {
        val goal = TransitionViewState.Dashboard(DashboardViewState.mainDashboard)
        sessionService.setUserLastSeenToNow()

        return BFFResult.Companion.success(goal)
    }

}
