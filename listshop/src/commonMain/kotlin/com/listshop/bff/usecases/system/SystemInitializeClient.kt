package com.listshop.bff.usecases.system

import com.listshop.analytics.AnalyticsHandle
import com.listshop.analytics.debug
import com.listshop.analytics.error
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
import com.listshop.bff.services.ListService
import com.listshop.bff.services.SessionService
import com.listshop.bff.services.SyncService
import com.listshop.bff.services.TagTree

class SystemInitializeClient(
    private val connectionStatus: ConnectionStatus,
    private val sessionService: SessionService,
    private val listService: ListService,
    private val syncService: SyncService,
    private val analyticsHandle: AnalyticsHandle
) {

    suspend fun process(): BFFResult<Pair<TransitionViewState, TagTree>> {
        analyticsHandle.debug("SystemGetLaunchScreen - begin use case")
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

    private suspend fun loadForSession(): BFFResult<Pair<TransitionViewState, TagTree>> {
        analyticsHandle.debug("SystemGetLaunchScreen - load for session")
        // determine logged in state of user
        val session = sessionService.currentUserSession()
        val firstTimeUser = session.userLastSeen == null
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

                UserSessionState.AnonNoList -> {
                    // greeting if first time or no list
                    val listNotAvailable = sessionService.currentListSession().localListUpdated == null
                    if (firstTimeUser || listNotAvailable) {
                        destinationGreeting()
                    } else {
                        destinationLocalList()
                    }

                }
            }

            return BFFResult.success(Pair(viewState, tagTree))
        } catch (e: Exception) {
            return BFFError.errorFromException(e)
        }
    }

    private fun destinationGreeting(): TransitionViewState {
        analyticsHandle.debug("SystemGetLaunchScreen - destination onboarding")
        return TransitionViewState.Guides
    }

    private suspend fun destinationLocalList(): TransitionViewState {
        analyticsHandle.debug("SystemGetLaunchScreen - destination local list")

        val wrappedLists = ListShoppingList(emptyList())
        val shoppingList = listService.retrieveOrCreateLocalList()

        // error if shopping list is still null - shouldn't happen
        if (shoppingList == null) {
            throw UnexpectedEmptyException("No LocalList Found")
        }
        return TransitionViewState.ListScreen(shoppingList, wrappedLists)


    }

    private fun destinationOnboarding(): TransitionViewState {
        analyticsHandle.debug("SystemGetLaunchScreen - destination onboarding")
        return TransitionViewState.Onboarding(OnboardingViewState.Choose)
    }

    private suspend fun destinationServerList(): TransitionViewState {
        analyticsHandle.debug("SystemGetLaunchScreen - destination server list")
        val listOfLists = listService.retrieveListOfLists()
        val wrappedLists = ListShoppingList(listOfLists)

        var shoppingList = syncService.loadMergedShoppingList(connectionStatus)
        if (shoppingList == null) {
            shoppingList = listService.retrieveMostRecentList()
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
