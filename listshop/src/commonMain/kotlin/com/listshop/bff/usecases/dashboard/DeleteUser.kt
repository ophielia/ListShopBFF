package com.listshop.bff.usecases.dashboard

import com.listshop.analytics.AnalyticsHandle
import com.listshop.analytics.debug
import com.listshop.bff.data.bff.BFFError
import com.listshop.bff.data.bff.BFFResult
import com.listshop.bff.data.state.ConnectionStatus
import com.listshop.bff.exceptions.OfflineException
import com.listshop.bff.services.ListService
import com.listshop.bff.services.SyncService
import com.listshop.bff.services.UserService

class DeleteUser(
    private val userService: UserService,
    private val listService: ListService,
    private val connectionStatus: ConnectionStatus,
    private val syncService: SyncService,
    private val analyticsHandle: AnalyticsHandle
) {
    suspend fun process(): BFFResult<Unit> {
        analyticsHandle.debug("DeleteUser - begin use case")
        try {
            checkOnlineStatus(connectionStatus)
            userService.deleteUser()
            listService.clearLocalList()
            syncService.syncLookupData(connectionStatus)
            analyticsHandle.debug("DeleteUser - end use case")
            return BFFResult(value = null)
        } catch (e: Exception) {
            analyticsHandle.listShopAnalytics.error("Error in Delete User call")
            return BFFError.errorFromException(e)
        }
    }

    private fun checkOnlineStatus(connectionStatus: ConnectionStatus) {
        if (connectionStatus != ConnectionStatus.Online) {
            throw OfflineException("User cannot delete account while offline")
        }
    }

}
