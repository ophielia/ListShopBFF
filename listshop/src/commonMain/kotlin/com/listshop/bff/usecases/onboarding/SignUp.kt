package com.listshop.bff.usecases.onboarding

import com.listshop.analytics.AnalyticsHandle
import com.listshop.bff.data.bff.BFFError
import com.listshop.bff.data.bff.BFFErrorSubtype
import com.listshop.bff.data.bff.BFFErrorType
import com.listshop.bff.data.bff.BFFResult
import com.listshop.bff.data.model.ListShoppingList
import com.listshop.bff.data.model.ShoppingList
import com.listshop.bff.data.state.ConnectionStatus
import com.listshop.bff.data.state.TransitionViewState
import com.listshop.bff.exceptions.BadParameterException
import com.listshop.bff.exceptions.OfflineException
import com.listshop.bff.services.ListService
import com.listshop.bff.services.SyncService
import com.listshop.bff.services.TagTree
import com.listshop.bff.services.UserService

class SignUp(
    private val userName: String,
    private val password: String,
    private val connectionStatus: ConnectionStatus,
    private val userService: UserService,
    private val syncService: SyncService,
    private val listService: ListService,
    private val analyticsHandle: AnalyticsHandle
) {

    suspend fun process(): BFFResult<Pair<TransitionViewState, TagTree>> {
        // validate input
        try {
            checkOnlineStatus()
            checkParameters()
        } catch (e: Exception) {
            analyticsHandle.listShopAnalytics.error("parameters invalid or offline")
            val bfferror = BFFError(BFFErrorType.AUTHENTICATION, BFFErrorSubtype.CANT_SIGNUP, "error while signing up")
            return BFFResult.Companion.error(bfferror)

        }

        // signup user - (setting all session stuff in method)
        try {
            userService.signUpUser(userName, password)
        } catch (exception: Exception) {
            analyticsHandle.listShopAnalytics.error("call to signup failed")
            val bfferror = BFFError(BFFErrorType.AUTHENTICATION, BFFErrorSubtype.CANT_SIGNUP, "error while signing up")
            return BFFResult.Companion.error(bfferror)
        }

        // (skipping statistics)
        var shoppingList: ShoppingList?
        try {
            // merge local list with server
            // the server list was created with the user - now we'll "upload" the
            // local list (if there is one)
            shoppingList = listService.mergeLocalWithServerList()
        } catch (e: Exception) {
            // if we fail here, it's because the merge with the server failed, but
            // we know that the list was created on the server, so we can continue with the local list
            analyticsHandle.listShopAnalytics.error("merging the list failed")
            shoppingList = listService.retrieveLocalList()
            if (shoppingList == null || shoppingList.externalId == null) {
                // this is an error - the user has to have a list
                val bfferror = BFFError(
                    BFFErrorType.ONBOARDING,
                    BFFErrorSubtype.CALL_FAILED,
                    "error after signing up, list retrieval failed"
                )
                return BFFResult.Companion.error(bfferror)
            }
        }
        var tagTree: TagTree = TagTree()
        var listOfLists = listOf<ShoppingList>()
        try {
            // get list of lists
            listOfLists = listService.retrieveListOfLists()


            // sync local data
            val tagTree = syncService.syncLookupData(connectionStatus)

            // return tag tree, list, and list of lists
            val wrappedLists = ListShoppingList(listOfLists)
            val viewState = TransitionViewState.ListScreen(shoppingList ?: ShoppingList.empty(), wrappedLists)
            return BFFResult.Companion.success(Pair(viewState, tagTree))
        } catch (e: Exception) {
            // we're swallowing errors here - the main thing is that the
            // user was created
            analyticsHandle.listShopAnalytics.error("processing post sign up failed, returning empty lists")
            val wrappedLists = ListShoppingList(listOfLists)
            val viewState = TransitionViewState.ListScreen(shoppingList ?: ShoppingList.empty(), wrappedLists)
            return BFFResult.Companion.success(Pair(viewState, tagTree))

        }
    }

    private fun checkParameters() {
        if (userName.isBlank() || password.isBlank()) {
            throw BadParameterException("userName or password is blank")
        }
        if (userName.length > 255) {
            throw BadParameterException("userName too long")
        }
        if (password.length > 255) {
            throw BadParameterException("password too long")
        }
    }

    private fun checkOnlineStatus() {
        if (connectionStatus != ConnectionStatus.Online) {
            throw OfflineException("User cannot signup while offline")
        }
    }
}
