package com.listshop.bff.usecases.dashboard

import com.listshop.analytics.AnalyticsHandle
import com.listshop.bff.data.bff.BFFError
import com.listshop.bff.data.bff.BFFErrorSubtype
import com.listshop.bff.data.bff.BFFErrorType
import com.listshop.bff.data.bff.BFFResult
import com.listshop.bff.data.state.OnboardingViewState
import com.listshop.bff.data.state.TransitionViewState
import com.listshop.bff.services.UserService

class UpdateUserProperty(
    private val property: String,
    private val value: String,
    private val userService: UserService,
    private val analyticsHandle: AnalyticsHandle,
) {
    suspend fun process(): BFFResult<Unit> {
        analyticsHandle.listShopAnalytics.debug("UpdateUserPropertiesUseCase - begin use case")

        try {
            userService.updateUserProperty(property, value)
            return BFFResult(value = null)
        } catch (e: Exception) {
            analyticsHandle.listShopAnalytics.error("Error while updating user property - ${e.message}")
            val bfferror = BFFError(BFFErrorType.DASHBOARD, BFFErrorSubtype.CALL_FAILED, "unable to update user property")
            return BFFResult.Companion.error(bfferror)
        }
    }
}
