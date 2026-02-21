package com.listshop.bff.usecases.dashboard

import com.listshop.analytics.AnalyticsHandle
import com.listshop.analytics.debug
import com.listshop.analytics.error
import com.listshop.bff.data.bff.BFFError
import com.listshop.bff.data.bff.BFFResult
import com.listshop.bff.data.state.ConnectionStatus
import com.listshop.bff.data.state.OnboardingViewState
import com.listshop.bff.data.state.TransitionViewState
import com.listshop.bff.exceptions.OfflineException
import com.listshop.bff.services.SyncService
import com.listshop.bff.services.UserService

class Logout(
    private val connectionStatus: ConnectionStatus,
    private val userService: UserService,
    private val syncService: SyncService,
    private val analyticsHandle: AnalyticsHandle
) {
    suspend fun process(): BFFResult<TransitionViewState> {
        analyticsHandle.debug("Logout - begin use case")
        try {
            checkOnlineStatus(connectionStatus)

            // logout User
            userService.logoutUser()

            // sync lookup data
            syncService.syncLookupData(connectionStatus)
            // return TVS
            val goal = TransitionViewState.Onboarding(OnboardingViewState.Choose)
            analyticsHandle.debug("Logout - end use case")
            return BFFResult.Companion.success(goal)
        } catch (e: Exception) {
            analyticsHandle.error("Error in Logout process")
            return BFFError.errorFromException(e)
        }
    }

    private fun checkOnlineStatus(connectionStatus: ConnectionStatus) {
        if (connectionStatus != ConnectionStatus.Online) {
            throw OfflineException("User cannot delete account while offline")
        }
    }
}

