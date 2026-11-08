package com.listshop.bff.ucp

import com.listshop.analytics.AnalyticsHandle
import com.listshop.bff.data.bff.NewBFFResult
import com.listshop.bff.data.bff.NewBFFResult
import com.listshop.bff.data.model.ListShoppingList
import com.listshop.bff.data.model.ShoppingList
import com.listshop.bff.data.state.ConnectionStatus
import com.listshop.bff.services.ListService
import com.listshop.bff.services.SessionService
import com.listshop.bff.usecases.listmanagement.AddList
import com.listshop.bff.usecases.listmanagement.DeleteList
import com.listshop.bff.usecases.listmanagement.GetAllLists
import com.listshop.bff.usecases.listmanagement.SelectListForEdit
import com.listshop.bff.usecases.listmanagement.UpdateList

class ListManagementUCP internal constructor(
    private val listService: ListService,
    private val sessionService: SessionService,
    private val analyticsHandle: AnalyticsHandle
) {

    @Throws(Exception::class)
    suspend fun addList(connectionStatus: ConnectionStatus): NewBFFResult<ListShoppingList> {
        val useCase = AddList(
            connectionStatus = connectionStatus,
            listService = listService,
            analyticsHandle = analyticsHandle
        )
        return useCase.process()
    }

    @Throws(Exception::class)
    suspend fun deleteList(connectionStatus: ConnectionStatus,listId:String): NewBFFResult<ListShoppingList> {
        val useCase = DeleteList(
            connectionStatus = connectionStatus,
            listIdToDelete = listId,
            listService = listService,
            analyticsHandle = analyticsHandle
        )
        return useCase.process()
    }


    @Throws(Exception::class)
    suspend fun selectListForEdit(connectionStatus: ConnectionStatus,listId:String): NewBFFResult<ShoppingList> {
        val useCase = SelectListForEdit(
            connectionStatus = connectionStatus,
            listId = listId,
            listService = listService,
            analyticsHandle = analyticsHandle
        )
        return useCase.process()
    }


    @Throws(Exception::class)
    suspend fun updateList(connectionStatus: ConnectionStatus,listId:String, listName: String, isStarterList:Boolean? = null): NewBFFResult<ListShoppingList> {
        val useCase = UpdateList(
            connectionStatus = connectionStatus,
            listId = listId,
            listName = listName,
            isStarterList = isStarterList,
            listService = listService,
            analyticsHandle = analyticsHandle
        )
        return useCase.process()
    }

    @Throws(Exception::class)
    suspend fun getAllLists(connectionStatus: ConnectionStatus): NewBFFResult<ListShoppingList> {
        val useCase = GetAllLists(
            connectionStatus = connectionStatus,
            listService = listService,
            analyticsHandle = analyticsHandle
        )
        return useCase.process()
    }
}


