package com.listshop.bff.ucp

import com.listshop.analytics.AnalyticsHandle
import com.listshop.bff.data.bff.BFFResult
import com.listshop.bff.data.model.TagList
import com.listshop.bff.data.state.ConnectionStatus
import com.listshop.bff.data.state.TransitionViewState
import com.listshop.bff.services.DishService
import com.listshop.bff.services.ListService
import com.listshop.bff.services.SessionService
import com.listshop.bff.services.TagService
import com.listshop.bff.usecases.navigation.NavigateToDashboard
import com.listshop.bff.usecases.navigation.NavigateToDishManagement
import com.listshop.bff.usecases.navigation.NavigateToListManagement
import com.listshop.bff.usecases.navigation.NavigateToListScreen
import com.listshop.bff.usecases.system.SystemLookupTags

class SystemUCP internal constructor(
    private val sessionService: SessionService,
    private val dishService: DishService,
    private val listService: ListService,
    private val tagService: TagService,
    private val analyticsHandle: AnalyticsHandle
) {
    @Throws(Exception::class)
    suspend fun systemLookupTags(
        fragment: String,
        tagTypes: List<String>,
        excludeGroups: Boolean
    ): BFFResult<TagList> {
        val useCase = SystemLookupTags(fragment, tagTypes, excludeGroups, tagService, analyticsHandle)
        return useCase.process()
    }


    @Throws(Exception::class)
    suspend fun navigateToDashboard(): BFFResult<TransitionViewState> {
        val useCase = NavigateToDashboard(sessionService, analyticsHandle)
        return useCase.process()
    }

    @Throws(Exception::class)
    suspend fun navigateToListManagement(
        connectionStatus: ConnectionStatus
    ): BFFResult<TransitionViewState> {
        val useCase = NavigateToListManagement(
            connectionStatus, listService, sessionService,
            analyticsHandle
        )
        return useCase.process()
    }

    @Throws(Exception::class)
    suspend fun navigateToDishManagement(
        connectionStatus: ConnectionStatus
    ): BFFResult<TransitionViewState> {
        val useCase = NavigateToDishManagement(
            connectionStatus, sessionService, dishService,
            analyticsHandle
        )
        return useCase.process()
    }

    @Throws(Exception::class)
    suspend fun navigateToListScreen(
        connectionStatus: ConnectionStatus
    ): BFFResult<TransitionViewState> {
        val useCase = NavigateToListScreen(
            connectionStatus, sessionService, listService,
            analyticsHandle
        )
        return useCase.process()
    }
}


