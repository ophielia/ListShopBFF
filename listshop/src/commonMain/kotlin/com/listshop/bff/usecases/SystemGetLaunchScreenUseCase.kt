package com.listshop.bff.usecases

import com.listshop.analytics.ListShopAnalytics
import com.listshop.bff.data.bff.BFFError
import com.listshop.bff.data.bff.BFFErrorSubtype
import com.listshop.bff.data.bff.BFFErrorType
import com.listshop.bff.data.bff.BFFResult
import com.listshop.bff.data.model.ListShoppingList
import com.listshop.bff.data.model.ShoppingList
import com.listshop.bff.data.state.ConnectionStatus
import com.listshop.bff.data.state.TransitionViewState
import com.listshop.bff.data.state.UserSessionState
import com.listshop.bff.services.*

class SystemGetLaunchScreenUseCase(
    private val connectionStatus: ConnectionStatus,
    private val sessionService: UserSessionService,
    private val userService: UserService,
    private val listService: ListService,
    private val syncService: SyncService,
    private val listShopAnalytics: ListShopAnalytics
) {

    suspend fun process(): BFFResult<Pair<TransitionViewState, TagTree>> {
        val compatible = syncService.checkApiCompatibility(connectionStatus)

        if (compatible) {
            listShopAnalytics.loadingSession()
            return loadForSession()
        }
        // construct result with failure
        val message = "Current version " + sessionService.currentAppInfo().clientVersion + " is not compatible"
        listShopAnalytics.error(message)
        val bfferror = BFFError(BFFErrorType.LOADING, BFFErrorSubtype.UPGRADE_REQUIRED, message)
        return BFFResult.error(bfferror)
    }

    private suspend fun loadForSession(): BFFResult<Pair<TransitionViewState, TagTree>> {
        // determine logged in state of user
        val session = sessionService.currentSession()
        val firstTimeUser = session.userLastSeen != null
        val standardDataOnly = session.sessionState != UserSessionState.User
        val isOnline = connectionStatus == ConnectionStatus.Online
        /*
                // this will be enow that we'e
                when (session.sessionState) {
                    UserSessionState.Anon, UserSessionState.AnonNoList, UserSessionState.UserLoggedOut ->
                        //
                        syncLookupData(connectionStatus)

                    else -> {
                        syncDataAndList(connectionStatus)
                    }
                }
        */

        // I see you, user, even if you're not logged in
        sessionService.setUserLastSeenToNow()

        //MM IMPORTANT - needs error handling here - catch Exceptions on this level
        //MM eeor handling for syncLocalData - don't want to fail if offline, or failure syncing local data
        // always sync local data if possible
        val tagTree = syncLookupData(connectionStatus, standardDataOnly)

        //MM try around this whole thing to catch and handle exceptions
        val viewState: TransitionViewState = when (session.sessionState) {
            UserSessionState.User ->
                // server list
                if (isOnline) {
                    destinationServerList()
                } else {
                    destinationLocalList()
                }

            UserSessionState.UserLoggedOut ->
                // login
                destinationOnboarding()

            UserSessionState.Anon ->
                // lovsl lidz
                destinationLocalList()

            UserSessionState.AnonNoList ->
                // greeting
                if (firstTimeUser) {
                    destinationGreeting()
                } else {
                    destinationLocalList()
                }

        }

        return BFFResult.success(Pair(viewState, tagTree))
        // exception list
        // throw OfflineException(message = "Can't reach the server - syncing lookup data")
    }

    private fun destinationGreeting(): TransitionViewState {
        TODO("Not yet implemented")
    }

    private fun destinationLocalList(): TransitionViewState {
        TODO("Not yet implemented")
    }

    private fun destinationOnboarding(): TransitionViewState {
        TODO("Not yet implemented")
    }

    private suspend fun destinationServerList(): TransitionViewState {
        val listOfLists = listService.retrieveListOfLists()
        val wrappedLists = ListShoppingList(listOfLists)

        var shoppingList = syncService.loadMergedShoppingList(connectionStatus)
        if (shoppingList == null) {
            shoppingList = listService.getMostRecentList(connectionStatus)
        }

        // error if shopping list is still null - shouldn't happen
        if (shoppingList == null) {
            //MM THROW EXCEPTION HERE!!!
        }

        val finalShoppingList = shoppingList!!
        return TransitionViewState.ListScreen(finalShoppingList, wrappedLists)
    }


    private suspend fun syncDataAndList(connectionStatus: ConnectionStatus) {
        userService.authenticateUser()
        val tagTree = syncService.syncLookupData(connectionStatus)
        val listOfLists = listService.retrieveListOfLists()
        val wrappedLists = ListShoppingList(listOfLists)
        retrieveSyncedList(tagTree, wrappedLists)
    }

    private suspend fun retrieveSyncedList(
        tagTree: TagTree,
        listOfLists: ListShoppingList
    ): BFFResult<Pair<TransitionViewState, String>> {
        var shoppingList: ShoppingList? = null
        try {
            shoppingList = syncService.loadMergedShoppingList(connectionStatus)
            if (shoppingList == null) {
                shoppingList = syncService.getMostRecentList(connectionStatus)
            }
            val finalShoppingList = shoppingList!!
            return BFFResult.success(Pair(TransitionViewState.ListScreen(finalShoppingList, listOfLists), "string"))
        } catch (e: Exception) {
            // construct result with failure
            val bfferror = BFFError(BFFErrorType.UNKNOWN, BFFErrorSubtype.CANT_GET_LIST, "cant retrieve shopping list")
            return BFFResult.error<Pair<TransitionViewState, String>>(bfferror)

        }


    }


    private suspend fun syncLookupData(connectionStatus: ConnectionStatus, standardDataOnly: Boolean): TagTree {
        // sync lookup data
        val tagTree = syncService.syncLookupData(connectionStatus)
        return tagTree
    }

    private suspend fun goToListOfLists(): BFFResult<Pair<TransitionViewState, String>> {
        // authenticate user
        userService.authenticateUser()
        val listOfLists = listService.retrieveListOfLists()
        val wrappedLists = ListShoppingList(listOfLists)
        val singleList = listOfLists.get(0)
        return BFFResult.success(Pair(TransitionViewState.ListScreen(singleList, wrappedLists), "beep")) //MM go away
    }
    /*
        private func goToListScreen(with shoppingList: ShoppingList, and tagTree: TagTree) {
            let cStat = connectionStatus
            let bgq = DispatchQueue.global(qos: .userInitiated)
            firstly { () -> Promise<[ShoppingList]> in
                // make sure to finish syncing local data first before moving to local list
                try listService.retrieveListOfLists()
            }
                    .done { listOfLists in
                        self.onComplete(.success((TransitionViewState.listScreen(shoppingList, listOfLists), tagTree)))
                    }
                    .done(on: bgq) { _ in
                        // fire and forget statistics
                        _ = try? self.syncService.syncStatistics(connectionStatus: cStat)
                    }
                    .catch { error in
                        print("Error: \(error) while syncing local list")
                        let lse = ListShopError(type: .core, title: "Can't get the lookup data", message: "Error while retrieving lookup data")
                        self.onComplete(.failure(lse))
                    }
        }
     */

}
