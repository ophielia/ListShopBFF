package com.listshop.bff.usecases.onboarding

import com.listshop.analytics.AnalyticsHandle
import com.listshop.bff.data.bff.BFFError
import com.listshop.bff.data.bff.BFFErrorSubtype
import com.listshop.bff.data.bff.BFFErrorType
import com.listshop.bff.data.bff.BFFResult
import com.listshop.bff.services.UserService

class RequestPasswordReset(
    private val userName: String,
    private val userService: UserService,
    private val analyticsHandle: AnalyticsHandle
) {

    suspend fun process(): BFFResult<Unit> {
        // send password reset request to server
        try {
            userService.requestPasswordReset(userName)
            return BFFResult(value = null)
        } catch (e: Exception) {
            analyticsHandle.listShopAnalytics.error("Error while requesting password rest - ${e.message}")
            val bfferror =
                BFFError(BFFErrorType.ONBOARDING, BFFErrorSubtype.CALL_FAILED, "unable to request password reset")
            return BFFResult.Companion.error(bfferror)
        }
    }
}
