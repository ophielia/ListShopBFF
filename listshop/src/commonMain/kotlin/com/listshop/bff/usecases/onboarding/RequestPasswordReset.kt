package com.listshop.bff.usecases.onboarding

import com.listshop.analytics.AnalyticsHandle
import com.listshop.analytics.debug
import com.listshop.analytics.error
import com.listshop.bff.data.bff.BFFError
import com.listshop.bff.data.bff.BFFResult
import com.listshop.bff.services.UserService

class RequestPasswordReset(
    private val userName: String,
    private val userService: UserService,
    private val analyticsHandle: AnalyticsHandle
) {

    suspend fun process(): BFFResult<Unit> {
        // send password reset request to server
        analyticsHandle.debug("RequestPasswordReset - begin use case")
        try {
            userService.requestPasswordReset(userName)
            analyticsHandle.debug("RequestPasswordReset - end use case")
            return BFFResult(value = null)
        } catch (e: Exception) {
            analyticsHandle.error("Error while requesting password rest - ${e.message}")
            return BFFError.errorFromException(e)
        }
    }
}
