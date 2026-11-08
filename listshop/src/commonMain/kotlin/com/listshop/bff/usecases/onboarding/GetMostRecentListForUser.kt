package com.listshop.bff.usecases.onboarding

import com.listshop.bff.data.bff.BFFResult
import com.listshop.bff.data.state.ConnectionStatus
import com.listshop.bff.data.state.TransitionViewState
import com.listshop.bff.services.ListService
import com.listshop.bff.services.SessionService
import com.listshop.bff.services.UserService

class GetMostRecentListForUser(
    private val connectionStatus: ConnectionStatus,
    private val sessionService: SessionService,
    private val userService: UserService,
    private val listService: ListService
) {

    suspend fun process(): BFFResult<TransitionViewState> {
        TODO("not implemented")
    }

}

