package com.listshop.bff.usecases

import com.listshop.analytics.ListShopAnalytics
import com.listshop.bff.data.bff.BFFError
import com.listshop.bff.data.bff.BFFErrorSubtype
import com.listshop.bff.data.bff.BFFErrorType
import com.listshop.bff.data.bff.BFFResult
import com.listshop.bff.data.model.ListShoppingList
import com.listshop.bff.data.state.ConnectionStatus
import com.listshop.bff.data.state.OnboardingViewState
import com.listshop.bff.data.state.TransitionViewState
import com.listshop.bff.data.state.UserSessionState
import com.listshop.bff.exceptions.UnexpectedEmptyException
import com.listshop.bff.services.*

class SystemGetLaunchScreenUseCase(
    private val connectionStatus: ConnectionStatus,
    private val sessionService: SessionService,
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
        val requiredVersion = syncService.getClientRequiredVersion(connectionStatus)
        val message = "Current version " + sessionService.currentAppInfo().clientVersion + ", Required Version " + requiredVersion
        listShopAnalytics.error(message)
        val bfferror = BFFError(BFFErrorType.LOADING, BFFErrorSubtype.UPGRADE_REQUIRED, message)
        return BFFResult.error(bfferror)
    }

    private suspend fun loadForSession(): BFFResult<Pair<TransitionViewState, TagTree>> {
        // determine logged in state of user
        val session = sessionService.currentUserSession()
        val firstTimeUser = session.userLastSeen != null
        val isOnline = connectionStatus == ConnectionStatus.Online

        // I see you, user, even if you're not logged in
        sessionService.setUserLastSeenToNow()

        // always sync local data if possible
        var tagTree : TagTree?
        try {
         tagTree = syncLookupData(connectionStatus)
        } catch ( e : Exception ) {
            val bfferror = BFFError(BFFErrorType.API, BFFErrorSubtype.CANT_RETRIEVE_DATA, e.message.toString())
            return BFFResult.error(bfferror)
        }

        try {
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
                {
                    // greeting if first time or no list
                    val listNotAvailable = sessionService.currentListSession().localListUpdated == null
                    if (firstTimeUser || listNotAvailable) {
                        destinationGreeting()
                    } else {
                        destinationLocalList()
                    }

                }
            }

            return BFFResult.success(Pair(viewState, tagTree ?: TagTree()))
        } catch (e: Exception) {
            val bfferror = BFFError(BFFErrorType.API, BFFErrorSubtype.CANT_LAUNCH, e.message.toString())
            return BFFResult.error(bfferror)
        }

        // exception list
        // throw OfflineException(message = "Can't reach the server - syncing lookup data")
    }

    private fun destinationGreeting(): TransitionViewState {
        return TransitionViewState.Guides
    }

    private suspend fun destinationLocalList(): TransitionViewState {
        val wrappedLists = ListShoppingList(emptyList())
        val shoppingList = listService.retrieveOrCreateLocalList()

        // error if shopping list is still null - shouldn't happen
        if (shoppingList == null) {
            throw UnexpectedEmptyException("No LocalList Found")
        }
        return TransitionViewState.ListScreen(shoppingList, wrappedLists)
    }

    private fun destinationOnboarding(): TransitionViewState {
        return TransitionViewState.Onboarding(OnboardingViewState.Choose)
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
            throw UnexpectedEmptyException("No Server List Found")
        }

        return TransitionViewState.ListScreen(shoppingList, wrappedLists)
    }

    private suspend fun syncLookupData(connectionStatus: ConnectionStatus): TagTree {
        // sync lookup data
        val tagTree = syncService.syncLookupData(connectionStatus)
        return tagTree
    }
}
