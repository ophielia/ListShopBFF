package com.listshop.bff.usecases.dashboard

import com.listshop.analytics.AnalyticsHandle
import com.listshop.bff.data.bff.BFFError
import com.listshop.bff.data.bff.BFFErrorSubtype
import com.listshop.bff.data.bff.BFFErrorType
import com.listshop.bff.data.bff.BFFResult
import com.listshop.bff.data.state.ConnectionStatus
import com.listshop.bff.services.LayoutService
import com.listshop.bff.services.ListService
import com.listshop.bff.services.SyncService
import com.listshop.bff.services.TagService
import com.listshop.bff.services.UserService

class DeleteUser(
    private val userService: UserService,
    private val listService: ListService,
    private val connectionStatus: ConnectionStatus,
    private val syncService: SyncService,
    private val analyticsHandle: AnalyticsHandle
) {
    suspend fun process(): BFFResult<Unit> {

        try {
            checkOnlineStatus(connectionStatus)
            userService.deleteUser()
            listService.clearLocalList()
            syncService.syncLookupData(connectionStatus)
            return BFFResult(value = null)
        } catch (e: Exception) {
            analyticsHandle.listShopAnalytics.error("Error in CheckUserNameTaken call")
            val bfferror = BFFError(BFFErrorType.DASHBOARD, BFFErrorSubtype.CALL_FAILED,
                "unable to delete user")
            return BFFResult.Companion.error(bfferror)
        }
    }

    private fun checkOnlineStatus(connectionStatus: ConnectionStatus) {
        if (connectionStatus != ConnectionStatus.Online) {
            throw IllegalStateException("User cannot delete account while offline")
        }
    }

}
