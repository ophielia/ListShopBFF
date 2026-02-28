package com.listshop.bff.ucp

import com.listshop.analytics.AnalyticsHandle
import com.listshop.bff.data.bff.BFFResult
import com.listshop.bff.data.model.ShoppingListTag
import com.listshop.bff.data.state.ConnectionStatus
import com.listshop.bff.data.state.TransitionViewState
import com.listshop.bff.services.*
import com.listshop.bff.usecases.dashboard.*

class DashboardUCP internal constructor(
    private val userService: UserService,
    private val tagService: TagService,
    private val syncService: SyncService,
    private val listService: ListService,
    private val analyticsHandle: AnalyticsHandle
) {


    @Throws(Exception::class)
    suspend fun logout(
        connectionStatus: ConnectionStatus
    ): BFFResult<TransitionViewState> {
        val useCase = Logout(connectionStatus, userService, syncService, analyticsHandle)
        return useCase.process()
    }

    @Throws(Exception::class)
    suspend fun createTag(
        tagName: String,
        tagType: String,
        groupId: String
    ): BFFResult<ShoppingListTag> {
        val useCase = CreateTag(
            tagName,
            tagType,
            groupId,
            tagService,
            analyticsHandle
        )
        return useCase.process()
    }

    @Throws(Exception::class)
    suspend fun updateTag(
        tagId: String,
        tagName: String
    ): BFFResult<ShoppingListTag> {
        val useCase = UpdateTag(
            tagId,
            tagName,
            tagService,
            analyticsHandle
        )
        return useCase.process()
    }

    @Throws(Exception::class)
    suspend fun updateUserProperty(
        key: String,
        value: String,
    ): BFFResult<Unit> {
        val useCase = UpdateUserProperty(
            key,
            value,
            userService,
            analyticsHandle
        )
        return useCase.process()
    }


    @Throws(Exception::class)
    suspend fun deleteUser(connectionStatus: ConnectionStatus): BFFResult<Unit> {
        val useCase = DeleteUser(userService, listService, connectionStatus, syncService, analyticsHandle)
        return useCase.process()
    }


    @Throws(Exception::class)
    suspend fun changePassword(oldPassword: String, newPassword: String): BFFResult<Unit> {
        val useCase = ChangePassword(oldPassword, newPassword, userService, analyticsHandle = analyticsHandle)
        return useCase.process()
    }

}


