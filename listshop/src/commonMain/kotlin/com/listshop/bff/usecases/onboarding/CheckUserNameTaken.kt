package com.listshop.bff.usecases.onboarding

import com.listshop.analytics.AnalyticsHandle
import com.listshop.analytics.debug
import com.listshop.analytics.error
import com.listshop.bff.data.bff.BFFError
import com.listshop.bff.data.bff.BFFResult
import com.listshop.bff.services.UserService

class CheckUserNameTaken(
    private val userName: String,
    private val userService: UserService,
    private val analyticsHandle: AnalyticsHandle
) {

    suspend fun process(): BFFResult<Boolean> {
        analyticsHandle.debug("CheckUserNameTaken - begin use case")
        try {
            val userNameTaken = userService.checkUserNameTaken(userName = userName)
            analyticsHandle.debug("CheckUserNameTaken - end use case")
            return BFFResult.success(value = userNameTaken)
        } catch (e: Exception) {
            analyticsHandle.error("Error in CheckUserNameTaken call")
            return BFFError.errorFromException(e)
        }

    }

}

