package com.listshop.bff.usecases.dashboard

import com.listshop.analytics.AnalyticsHandle
import com.listshop.bff.data.bff.BFFError
import com.listshop.bff.data.bff.BFFErrorSubtype
import com.listshop.bff.data.bff.BFFErrorType
import com.listshop.bff.data.bff.BFFResult
import com.listshop.bff.data.state.ConnectionStatus
import com.listshop.bff.data.state.OnboardingViewState
import com.listshop.bff.data.state.TransitionViewState
import com.listshop.bff.services.SyncService
import com.listshop.bff.services.UserService

class Logout(
    private val connectionStatus: ConnectionStatus,
    private val userService: UserService,
    private val syncService: SyncService,
    private val analyticsHandle: AnalyticsHandle
) {
    suspend fun process(): BFFResult<TransitionViewState> {

        try {
            checkOnlineStatus(connectionStatus)

            // logout User
            userService.logoutUser()

            // sync lookup data
            syncService.syncLookupData(connectionStatus)
            // return TVS
            val goal = TransitionViewState.Onboarding(OnboardingViewState.Choose)
            return BFFResult.Companion.success(goal)
        } catch (e: Exception) {
            analyticsHandle.listShopAnalytics.error("Error in Logout process")
            val bfferror = BFFError(BFFErrorType.ONBOARDING, BFFErrorSubtype.CALL_FAILED,
                "unable to logout user")
            return BFFResult.Companion.error(bfferror)
        }
    }

    private fun checkOnlineStatus(connectionStatus: ConnectionStatus) {
        if (connectionStatus != ConnectionStatus.Online) {
            throw IllegalStateException("User cannot delete account while offline")
        }
    }
}

