package com.listshop.bff.usecases.dashboard

import com.listshop.analytics.AnalyticsHandle
import com.listshop.analytics.debug
import com.listshop.analytics.error
import com.listshop.bff.data.bff.BFFError.Companion.rawErrorFromException
import com.listshop.bff.data.bff.BFFResult
import com.listshop.bff.data.state.ConnectionStatus
import com.listshop.bff.data.state.OnboardingViewState
import com.listshop.bff.exceptions.OfflineException
import com.listshop.bff.services.SyncService
import com.listshop.bff.services.TagTree
import com.listshop.bff.services.UserService

class Logout(
    private val connectionStatus: ConnectionStatus,
    private val userService: UserService,
    private val syncService: SyncService,
    private val analyticsHandle: AnalyticsHandle
) {
    suspend fun process(): BFFResult<TagTree> {
        analyticsHandle.debug("Logout - begin use case")
        try {
            val isOffline = connectionStatus != ConnectionStatus.Online

            // logout User
            userService.logoutUser(isOffline)

            // sync lookup data
            val tagTree = syncService.syncLookupData(connectionStatus)

            analyticsHandle.debug("Logout - end use case")
            return BFFResult.Success(tagTree)
        } catch (e: Exception) {
            analyticsHandle.error("Error in Logout process")
            return BFFResult.Failure(rawErrorFromException(e))
        }
    }

    private fun checkOnlineStatus(connectionStatus: ConnectionStatus) {
        if (connectionStatus != ConnectionStatus.Online) {
            throw OfflineException("User cannot delete account while offline")
        }
    }
}

