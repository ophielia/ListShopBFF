package com.listshop.bff.usecases.dashboard

import com.listshop.analytics.AnalyticsHandle
import com.listshop.bff.data.bff.BFFError
import com.listshop.bff.data.bff.BFFErrorSubtype
import com.listshop.bff.data.bff.BFFErrorType
import com.listshop.bff.data.bff.BFFResult
import com.listshop.bff.data.state.OnboardingViewState
import com.listshop.bff.data.state.TransitionViewState
import com.listshop.bff.services.UserService

class ChangePassword(
    private val originalPassword: String,
    private val newPassword: String,
    private val userService: UserService,
    private val analyticsHandle: AnalyticsHandle
) {
    suspend fun process(): BFFResult<Unit> {
        try {
            val userNameTaken = userService.changePassword(originalPassword, newPassword)
            return BFFResult.success(value = userNameTaken)
        } catch (e: Exception) {
            analyticsHandle.listShopAnalytics.error("Error in CheckUserNameTaken call")
            val bfferror = BFFError(BFFErrorType.ONBOARDING, BFFErrorSubtype.CALL_FAILED, "unable to check user name")
            return BFFResult.Companion.error(bfferror)
        }
    }

}

