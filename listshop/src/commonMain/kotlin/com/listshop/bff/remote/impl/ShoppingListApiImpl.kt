package com.listshop.bff.remote.impl

import com.listshop.analytics.ListShopAnalytics
import com.listshop.bff.data.model.ShoppingList
import com.listshop.bff.data.remote.ApiShoppingListEmbedded
import com.listshop.bff.data.remote.ApiShoppingListEmbeddedList
import com.listshop.bff.data.remote.MergeResult
import com.listshop.bff.data.remote.PostShoppingList
import com.listshop.bff.data.remote.PutMergeRequest
import com.listshop.bff.exceptions.ApiException
import com.listshop.bff.remote.ListShopRemoteApi
import com.listshop.bff.remote.ShoppingListApi
import io.ktor.client.call.body
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

internal class ShoppingListApiImpl(
    val remoteApi: ListShopRemoteApi,
    val listShopAnalytics: ListShopAnalytics
) : ShoppingListApi {

    override suspend fun getAllShoppingLists(): List<ShoppingList> {
        val token = remoteApi.token()
        val urlString = remoteApi.buildPath("/shoppinglist")
        listShopAnalytics.debug("getting lists, the token is: " + token)

        val response = remoteApi.getRequest(urlString)

        remoteApi.mapNonSuccessToException(
            response.status.value,
            ApiException("get shopping list call failed with status: " + response.status.value)
        )

        val result: ApiShoppingListEmbedded =
            response.body()

        return result.embeddedList.shoppingListResourceList
            .map { el -> el.embeddedList}
            .map { el -> ShoppingList.create(apiValue = el) }
    }

    override suspend fun retrieveMostRecentList(): ShoppingList {
        val token = remoteApi.token()
        val urlString = remoteApi.buildPath("/shoppinglist/mostrecent")
        listShopAnalytics.debug("getting most recent list, the token is: " + token)

        val response = remoteApi.getRequest(urlString)

        remoteApi.mapNonSuccessToException(
            response.status.value,
            ApiException("get shopping list call failed with status: " + response.status.value)
        )

        val result : ApiShoppingListEmbeddedList = response.body()

        return ShoppingList.create(apiValue = result.embeddedList)
    }

    override suspend fun retrieveListById(serverId: String): ShoppingList {
        val urlString = remoteApi.buildPath("/shoppinglist/${serverId}")
        listShopAnalytics.debug("getting most recent list, the id is: " + serverId)

        val response = remoteApi.getRequest(urlString)

        remoteApi.mapNonSuccessToException(
            response.status.value,
            ApiException("get shopping list call failed with status: " + response.status.value)
        )

        val result : ApiShoppingListEmbeddedList = response.body()

        return ShoppingList.create(apiValue = result.embeddedList)
    }

    override suspend fun createList(payload: PostShoppingList): String {
        val payload = Json.encodeToString(payload)
        val urlString = remoteApi.buildPath("/shoppinglist")
        val response = remoteApi.postRequest(urlString, payload)
        remoteApi.mapNonSuccessToException(
            response.status.value,
            ApiException("merge shopping list call failed with status: " + response.status.value)
        )
        val location = remoteApi.pullLocation(response)
        val elements = location.split("/")
        return elements.last()
    }

    override suspend fun mergeLocalListWithServer(listMergeRequest: PutMergeRequest) : ShoppingList {
        val token = remoteApi.token()
        val urlString = remoteApi.buildPath("/shoppinglist/shared")
        listShopAnalytics.debug("merging the local list with the server list, the token is: " + token)

        // convert object to json payload
        val payload = Json.encodeToString(listMergeRequest)

        val response = remoteApi.putRequest(urlString, payload)

        remoteApi.mapNonSuccessToException(
            response.status.value,
            ApiException("merge shopping list call failed with status: " + response.status.value)
        )

        val result : MergeResult = response.body()

        return ShoppingList.create(apiValue = result.mergeResult.shoppingList)
    }

}
