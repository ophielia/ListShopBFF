package com.listshop.bff.remote.impl

import com.listshop.analytics.ListShopAnalytics
import com.listshop.bff.data.model.ShoppingList
import com.listshop.bff.data.remote.ApiShoppingListEmbedded
import com.listshop.bff.exceptions.ApiException
import com.listshop.bff.remote.ListShopRemoteApi
import com.listshop.bff.remote.ShoppingListApi
import io.ktor.client.call.body

internal class ShoppingListApiImpl(
    val remoteApi: ListShopRemoteApi,
    val listShopAnalytics: ListShopAnalytics
) : ShoppingListApi {

    override suspend fun getAllShoppingLists(): List<ShoppingList> {
        val token = remoteApi.token()
        val urlString = remoteApi.buildPath("/shoppinglist")
        listShopAnalytics.debug("getting lists, the token is: " + token)

        val response = remoteApi.getRequest(urlString)

        remoteApi.mapNonSuccessToException(response.status.value,
            ApiException("get shopping list call failed with status: " + response.status.value)
        )

        val result: ApiShoppingListEmbedded =
            response.body()

        return result.embeddedList.shoppingListResourceList
            .map { el -> el.embeddedList}
            .map { el -> ShoppingList.create(apiValue = el) }
    }
}
