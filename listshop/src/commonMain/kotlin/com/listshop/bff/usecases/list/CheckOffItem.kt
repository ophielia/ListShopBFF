package com.listshop.bff.usecases.list

import com.listshop.analytics.AnalyticsHandle
import com.listshop.analytics.debug
import com.listshop.analytics.error
import com.listshop.bff.data.bff.BFFError.Companion.rawErrorFromException
import com.listshop.bff.data.bff.BFFResult
import com.listshop.bff.data.model.ShoppingList
import com.listshop.bff.data.state.ConnectionStatus
import com.listshop.bff.data.state.UserSessionState
import com.listshop.bff.services.ListService
import com.listshop.bff.services.ListSession
import com.listshop.bff.services.SessionService
import com.listshop.bff.usecases.validators.ConnectionStatusValidator

class CheckOffItem(
    private val listId: String?,
    private val itemId: String?,
    private val tagId: String,
    private val checkedOff: Boolean,
    private val connectionStatus: ConnectionStatus,
    private val sessionService: SessionService,
    private val listService: ListService,
    private val analyticsHandle: AnalyticsHandle
) : ConnectionStatusValidator {
    suspend fun process(): BFFResult<ShoppingList> {
        analyticsHandle.debug("Check Off Item - begin use case")
        try {

            val session = sessionService.currentUserSession()
            val isOnline = connectionStatus == ConnectionStatus.Online
            val isLoggedIn = session.sessionState == UserSessionState.User

            validateListIdMatchesCurrent(listId, sessionService.currentListSession())
            val validatedItemId: String = validateItemId(itemId, isOnline, isLoggedIn)
            // _always_ check off item locally
            listService.checkOffItemLocally(listId, itemId, tagId, checkedOff)

            // if online, check off item on server and refresh list
            if (isOnline && isLoggedIn) {
                val validatedListId = validateListIdExists(listId)
                listService.checkOffItemOnServer(validatedListId, validatedItemId, tagId, checkedOff)
            }

            val shoppingList: ShoppingList? = when (session.sessionState) {
                UserSessionState.User ->
                    // server list
                    if (isOnline) {
                        listService.retrieveServerList()
                    } else {
                        listService.retrieveOrCreateLocalList()
                    }

                else ->
                    // login
                    listService.retrieveOrCreateLocalList()
            }

            analyticsHandle.debug("Check Off Item - end use case")
            return BFFResult.Success(value = shoppingList ?: ShoppingList.empty())
        } catch (e: Exception) {
            analyticsHandle.error("Error in Check Off Item call")
            return BFFResult.Failure(rawErrorFromException(e))
        }
    }

    private fun validateListIdExists(listId: String?): String {
        return listId ?: throw IllegalArgumentException("List id cannot be null")
    }

    private fun validateItemId(itemId: String?, isOnline: Boolean, isLoggedIn: Boolean): String {
        // check that item id is not null - only when online with user
        return when (isOnline && isLoggedIn) {
         true -> itemId ?: throw IllegalArgumentException("Item id cannot be null for server call")
         else -> itemId ?: ""
        }
    }

    private fun validateListIdMatchesCurrent(listId: String?, session: ListSession) {
        // check that list id matches current list id in session (non-logged in users may have empty list ids)
        val bothNotNull = session.serverListId != null && listId != null
        if (bothNotNull && listId != session.serverListId) {
            throw IllegalArgumentException("List id does not match current list id in session")
        }
    }
}
