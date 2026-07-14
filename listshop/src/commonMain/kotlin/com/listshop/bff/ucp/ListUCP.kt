package com.listshop.bff.ucp

import com.listshop.analytics.AnalyticsHandle
import com.listshop.bff.data.bff.BFFResult
import com.listshop.bff.data.model.ListShoppingList
import com.listshop.bff.services.ListService
import com.listshop.bff.usecases.listmanagement.AddList

class ListUCP internal constructor(
    private val listService: ListService,
    private val analyticsHandle: AnalyticsHandle
) {

    @Throws(Exception::class)
    suspend fun addList(): BFFResult<ListShoppingList> {
        val useCase = AddList(
            listService = listService,
            analyticsHandle = analyticsHandle
        )
        return useCase.process()
    }
}


