package com.listshop.bff.usecases.system

import com.listshop.analytics.AnalyticsHandle
import com.listshop.analytics.debug
import com.listshop.analytics.error
import com.listshop.bff.data.bff.BFFError
import com.listshop.bff.data.bff.BFFErrorSubtype
import com.listshop.bff.data.bff.BFFErrorType
import com.listshop.bff.data.bff.BFFResult
import com.listshop.bff.data.session.UserSession
import com.listshop.bff.data.state.ConnectionStatus
import com.listshop.bff.data.state.UserSessionState
import com.listshop.bff.services.ListService
import com.listshop.bff.services.SessionService
import com.listshop.bff.services.SyncService
import com.listshop.bff.services.TagTree

class SystemInitializeClient(
    private val connectionStatus: ConnectionStatus,
    private val sessionService: SessionService,
    private val syncService: SyncService,
    private val analyticsHandle: AnalyticsHandle
) {

    suspend fun process(): BFFResult<TagTree> {
        analyticsHandle.debug("SystemInitializeClient - begin use case")
        val compatible = syncService.checkApiCompatibility(connectionStatus)

        if (compatible) {
            return loadForSession()
        }
        // construct result with failure
        val requiredVersion = syncService.getClientRequiredVersion(connectionStatus)
        val message =
            "Current version " + sessionService.currentAppInfo().clientVersion + ", Required Version " + requiredVersion
        analyticsHandle.error(message)
        val bfferror = BFFError(BFFErrorType.API, BFFErrorSubtype.UPGRADE_REQUIRED, message)
        return BFFResult.Companion.error(bfferror)
    }

    private suspend fun loadForSession(): BFFResult<TagTree> {
        analyticsHandle.debug("SystemInitializeClient - load for session")
        // determine logged in state of user
        val session = sessionService.currentUserSession()
        val isOnline = connectionStatus == ConnectionStatus.Online

        // I see you, user, even if you're not logged in
        sessionService.setUserLastSeenToNow()

        // always sync local data if possible
        var tagTree: TagTree?
        try {
            tagTree = syncLookupData(connectionStatus)
        } catch (e: Exception) {
            return BFFError.errorFromException(e)
        }

        // merge list if online user
        try {
            mergeShoppingListChanges(session, isOnline)
        } catch (e: Exception) {
            return BFFError.errorFromException(e)
        }

        return BFFResult.success(tagTree)
    }

    private suspend fun mergeShoppingListChanges(session: UserSession, isOnline: Boolean) {
        if (session.sessionState == UserSessionState.User && isOnline) {
            syncService.loadMergedShoppingList(connectionStatus)
        }
    }

    private suspend fun syncLookupData(connectionStatus: ConnectionStatus): TagTree {
        // sync lookup data
        val tagTree = syncService.syncLookupData(connectionStatus)
        return tagTree
    }
}
