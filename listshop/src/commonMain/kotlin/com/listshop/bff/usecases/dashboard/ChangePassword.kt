package com.listshop.bff.usecases.dashboard

import com.listshop.analytics.AnalyticsHandle
import com.listshop.analytics.debug
import com.listshop.analytics.error
import com.listshop.bff.data.bff.BFFError.Companion.rawErrorFromException
import com.listshop.bff.data.bff.NewBFFResult
import com.listshop.bff.services.UserService

class ChangePassword(
    private val originalPassword: String,
    private val newPassword: String,
    private val userService: UserService,
    private val analyticsHandle: AnalyticsHandle
) {
    suspend fun process(): NewBFFResult<Unit> {
        analyticsHandle.debug("ChangePassword - begin use case")
        try {
            val userNameTaken = userService.changePassword(originalPassword, newPassword)
            analyticsHandle.debug("ChangePassword - end use case")
            return NewBFFResult.Success(value = userNameTaken)
        } catch (e: Exception) {
            analyticsHandle.error("Error in CheckUserNameTaken call")
            return NewBFFResult.Failure(rawErrorFromException(e))
        }
    }

}

