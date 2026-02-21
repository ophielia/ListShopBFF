package com.listshop.bff.usecases.onboarding

import com.listshop.analytics.AnalyticsHandle
import com.listshop.analytics.debug
import com.listshop.analytics.error
import com.listshop.bff.data.bff.BFFError
import com.listshop.bff.data.bff.BFFResult
import com.listshop.bff.data.model.ListShoppingList
import com.listshop.bff.data.model.ShoppingList
import com.listshop.bff.data.state.ConnectionStatus
import com.listshop.bff.data.state.TransitionViewState
import com.listshop.bff.exceptions.AuthenticationException
import com.listshop.bff.services.ListService
import com.listshop.bff.services.SyncService
import com.listshop.bff.services.TagTree
import com.listshop.bff.services.UserService

class SignIn(
    private val userName: String,
    private val password: String,
    private val connectionStatus: ConnectionStatus,
    private val userService: UserService,
    private val syncService: SyncService,
    private val listService: ListService,
    private val analyticsHandle: AnalyticsHandle
) {

    suspend fun process(): BFFResult<Pair<TransitionViewState, TagTree>> {
        // sign in user
        analyticsHandle.debug("SignIn - begin use case")
        try {
            userService.signInUser(userName, password)
        } catch (exception: Exception) {
            analyticsHandle.error("Error while signing in - ${exception.message}")
            if (exception is AuthenticationException) {
                // user couldn't sign in - return error
                return BFFError.errorFromException(exception)
            }
            // otherwise, log the exception and continue
            // we don't want a user property exception to break the login
            analyticsHandle.listShopAnalytics.error("Non-Authentication error while signing in, continuing - ${exception.message}")
        }

        // user successfully signed in

        try {
            // sync lookup data
            val tagTree = syncService.syncLookupData(connectionStatus)

            // merge local list if pertinent
            syncService.mergeLocalListWithServer()

            // get most recent list
            val shoppingList = listService.retrieveMostRecentList()

            val listOfLists = listService.retrieveListOfLists()
            val wrappedLists = ListShoppingList(listOfLists)

            // return tag tree, list, and list of lists
            val viewState = TransitionViewState.ListScreen(shoppingList ?: ShoppingList.empty(), wrappedLists)
            analyticsHandle.debug("SignIn - end use case")
            return BFFResult.Companion.success(Pair(viewState, tagTree))

        } catch (exception: Exception) {
            analyticsHandle.error("Error processing data after login - ${exception.message.toString()}")
            // going to the list screen, with empties
            val wrappedLists = ListShoppingList(emptyList())
            val tagTree = TagTree()
            // return tag tree, list, and list of lists
            val viewState = TransitionViewState.ListScreen(ShoppingList.empty(), wrappedLists)
            return BFFResult.Companion.success(Pair(viewState, tagTree))
        }
    }
}
