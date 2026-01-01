package com.listshop.bff.usecases

import com.listshop.analytics.ListShopAnalytics
import com.listshop.bff.data.bff.BFFError
import com.listshop.bff.data.bff.BFFErrorSubtype
import com.listshop.bff.data.bff.BFFErrorType
import com.listshop.bff.data.bff.BFFResult
import com.listshop.bff.data.model.ListShoppingList
import com.listshop.bff.data.model.ShoppingList
import com.listshop.bff.data.state.ConnectionStatus
import com.listshop.bff.data.state.OnboardingViewState
import com.listshop.bff.data.state.TransitionViewState
import com.listshop.bff.data.state.UserSessionState
import com.listshop.bff.services.ListService
import com.listshop.bff.services.SyncService
import com.listshop.bff.services.TagTree
import com.listshop.bff.services.UserService
import com.listshop.bff.services.UserSessionService

class SystemGetLaunchScreenUseCase(
    private val connectionStatus: ConnectionStatus,
    private val sessionService: UserSessionService,
    private val userService: UserService,
    private val listService: ListService,
    private val syncService: SyncService,
    private val listShopAnalytics: ListShopAnalytics
) {

    suspend fun process(): BFFResult<Pair<TransitionViewState,String>> {
        val compatible = syncService.checkApiCompatibility(connectionStatus)

        if (compatible) {
            listShopAnalytics.loadingSession()
            loadForSession()
        } else {
            // construct result with failure
            val message = "Current version " + sessionService.currentAppInfo().clientVersion + " is not compatible"
            listShopAnalytics.error(message)
            val bfferror = BFFError(BFFErrorType.LOADING, BFFErrorSubtype.UPGRADE_REQUIRED, message)
            return BFFResult.error(  bfferror)
        }
        // dummy return for compile
     val singleList = ShoppingList.empty()
        return BFFResult.success(Pair(TransitionViewState.ListScreen(singleList, ListShoppingList(emptyList())), "beep")) //MM go away
    }

    private suspend fun loadForSession() {
        // determine logged in state of user
        val session = sessionService.currentSession()

        when (session.sessionState) {
            UserSessionState.Anon, UserSessionState.AnonNoList, UserSessionState.UserLoggedOut ->
                syncLookupData(connectionStatus)

            else -> {
                syncDataAndList(connectionStatus)
            }
        }




    }

    private suspend fun syncDataAndList(connectionStatus: ConnectionStatus) {
        userService.authenticateUser()
        val tagTree = syncService.syncLookupData(connectionStatus)
        val listOfLists = listService.retrieveListOfLists()
        val wrappedLists = ListShoppingList(listOfLists)
        retrieveSyncedList(tagTree, wrappedLists)
    }

    private suspend fun retrieveSyncedList(tagTree: TagTree, listOfLists: ListShoppingList)  : BFFResult<Pair<TransitionViewState,String>> {
        var shoppingList: ShoppingList? = null
        try {
            shoppingList = syncService.syncWithServerList(connectionStatus)
            if (shoppingList == null) {
                shoppingList = syncService.getMostRecentList(connectionStatus)
            }
            val finalShoppingList = shoppingList!!
            return BFFResult.success(Pair(TransitionViewState.ListScreen(finalShoppingList, listOfLists), "string"))
        } catch (e : Exception) {
            // construct result with failure
            val bfferror = BFFError(BFFErrorType.UNKNOWN, BFFErrorSubtype.CANT_GET_LIST, "cant retrieve shopping list")
            return BFFResult.error<Pair<TransitionViewState,String>>(bfferror)

        }


    }



    private suspend fun syncLookupData(connectionStatus: ConnectionStatus) : BFFResult<Pair<TransitionViewState,String>> {
        // sync lookup data
        try {
            val tagTree = syncService.syncLookupData(connectionStatus)
            val goal = TransitionViewState.Onboarding(OnboardingViewState.Choose)
            return BFFResult.success(Pair(goal,"string"))
        } catch (e: Exception) {
            return  BFFError.errorFromException<Pair<TransitionViewState,String>>( e)
        }

    }

    private suspend fun goToListOfLists() : BFFResult<Pair<TransitionViewState,String>> {
        // authenticate user
        userService.authenticateUser()
        val listOfLists = listService.retrieveListOfLists()
        val wrappedLists = ListShoppingList(listOfLists)
        val singleList = listOfLists.get(0)
        return BFFResult.success(Pair(TransitionViewState.ListScreen(singleList,wrappedLists), "beep")) //MM go away
    }


}
