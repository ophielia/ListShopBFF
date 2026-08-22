package com.listshop.bff.ucp

import com.listshop.analytics.AnalyticsHandle
import com.listshop.analytics.ListShopAnalytics
import com.listshop.bff.data.bff.BFFResult
import com.listshop.bff.data.state.ConnectionStatus
import com.listshop.bff.data.state.TransitionViewState
import com.listshop.bff.services.*
import com.listshop.bff.usecases.dashboard.ChangePassword
import com.listshop.bff.usecases.onboarding.CheckUserNameTaken
import com.listshop.bff.usecases.onboarding.RequestPasswordReset
import com.listshop.bff.usecases.system.LegacySystemGetLaunchScreen
import com.listshop.bff.usecases.onboarding.SignIn
import com.listshop.bff.usecases.onboarding.SignUp
import com.listshop.bff.usecases.system.SystemGetLaunchScreen
import com.listshop.bff.usecases.system.SystemInitializeClient

class OnboardingUCP internal constructor(
    private val sessionService: SessionService,
    private val listService: ListService,
    private val userService: UserService,
    private val syncService: SyncService,
    private val listShopAnalytics: ListShopAnalytics,
    private val analyticsHandle: AnalyticsHandle
) {

    @Throws(Exception::class)
    suspend fun legacySystemGetLaunchScreen(connectionStatus: ConnectionStatus): BFFResult<TransitionViewState> {
        val useCase = LegacySystemGetLaunchScreen(connectionStatus, sessionService, userService, listService)
        return useCase.process()
    }

    @Throws(Exception::class)
    suspend fun systemGetLaunchScreen(connectionStatus: ConnectionStatus): BFFResult<Pair<TransitionViewState, TagTree>> {
        val useCase = SystemGetLaunchScreen(
            connectionStatus = connectionStatus,
            sessionService = sessionService,
            listService = listService,
            syncService = syncService,
            analyticsHandle = analyticsHandle
        )
        return useCase.process()
    }

    @Throws(Exception::class)
    suspend fun systemInitializeClient(connectionStatus: ConnectionStatus): BFFResult<TagTree> {
        val useCase = SystemInitializeClient(
            connectionStatus = connectionStatus,
            sessionService = sessionService,
            syncService = syncService,
            analyticsHandle = analyticsHandle
        )
        return useCase.process()
    }

    @Throws(Exception::class)
    suspend fun signIn(userName: String, password: String, connectionStatus: ConnectionStatus): BFFResult<Pair<TransitionViewState, TagTree>> {
        val useCase = SignIn(userName, password, connectionStatus,userService, syncService, listService, analyticsHandle)
        return useCase.process()
    }

    @Throws(Exception::class)
    suspend fun signUp(userName: String, password: String, connectionStatus: ConnectionStatus): BFFResult<Pair<TransitionViewState, TagTree>> {
        val useCase =
            SignUp(userName, password, connectionStatus, userService,syncService, listService, analyticsHandle)
        return useCase.process()
    }

    @Throws(Exception::class)
    suspend fun checkUserNameTaken(userName: String): BFFResult<Boolean> {
        val useCase = CheckUserNameTaken(userName,  userService, analyticsHandle = analyticsHandle)
        return useCase.process()
    }


    @Throws(Exception::class)
    suspend fun requestPasswordReset(userName: String): BFFResult<Unit> {
        val useCase = RequestPasswordReset(userName, userService, analyticsHandle = analyticsHandle)
        return useCase.process()
    }
}


