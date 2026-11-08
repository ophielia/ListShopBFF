package com.listshop.bff.usecases.navigation

import com.listshop.analytics.AnalyticsHandle
import com.listshop.analytics.debug
import com.listshop.analytics.error
import com.listshop.bff.data.bff.BFFError
import com.listshop.bff.data.bff.BFFError.Companion.rawErrorFromException
import com.listshop.bff.data.bff.NewBFFResult
import com.listshop.bff.data.state.DashboardViewState
import com.listshop.bff.data.state.TransitionViewState
import com.listshop.bff.services.SessionService

class NavigateToDashboard(
    private val sessionService: SessionService,
    private val analyticsHandle: AnalyticsHandle
) {
    fun process(): NewBFFResult<TransitionViewState> {
        analyticsHandle.debug("NavigateToDashboard - begin use case")
        try {
            val goal = TransitionViewState.Dashboard(DashboardViewState.mainDashboard)
            sessionService.setUserLastSeenToNow()
            analyticsHandle.debug("NavigateToDashboard - end use case")
            return NewBFFResult.Success(goal)
        } catch (e: Exception) {
            analyticsHandle.error("NavigateToDashboard - error while navigating to dashboard - ${e.message}")
            return NewBFFResult.Failure(rawErrorFromException(e))
        }
    }

}
