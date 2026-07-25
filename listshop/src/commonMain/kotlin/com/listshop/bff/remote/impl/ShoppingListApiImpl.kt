package com.listshop.bff.remote.impl

import com.listshop.analytics.ListShopAnalytics
import com.listshop.bff.data.model.ShoppingList
import com.listshop.bff.data.remote.*
import com.listshop.bff.remote.ListShopRemoteApi
import com.listshop.bff.remote.ShoppingListApi
import io.ktor.client.call.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

internal class ShoppingListApiImpl(
    val remoteApi: ListShopRemoteApi,
    val listShopAnalytics: ListShopAnalytics
) : ShoppingListApi {

    private val shoppingListPath = "/v2/shoppinglist"

    override suspend fun getAllShoppingLists(): List<ShoppingList> {
        val token = remoteApi.token()
        val urlString = remoteApi.buildPath(shoppingListPath)
        listShopAnalytics.debug("getting lists, the token is: $token")

        val response = remoteApi.getRequest(urlString)

        remoteApi.mapNonSuccessToException(
            response.status.value,
            "get shopping list call failed with status: " + response.status.value
        )

        val result: ApiShoppingListList =
            response.body()

        return result.lists
            .map { el -> ShoppingList.create(apiValue = el) }
    }

    override suspend fun retrieveMostRecentList(): ShoppingList {
        val token = remoteApi.token()
        val urlString = remoteApi.buildPath("$shoppingListPath/mostrecent")
        listShopAnalytics.debug("getting most recent list, the token is: $token")

        val response = remoteApi.getRequest(urlString)

        remoteApi.mapNonSuccessToException(
            response.status.value,
            "get shopping list call failed with status: " + response.status.value
        )

        val result: ApiShoppingList = response.body()

        return ShoppingList.create(apiValue = result)
    }

    override suspend fun retrieveListById(serverId: String): ShoppingList {
        val urlString = remoteApi.buildPath("$shoppingListPath/${serverId}")
        listShopAnalytics.debug("getting most recent list, the id is: $serverId")

        val response = remoteApi.getRequest(urlString)

        remoteApi.mapNonSuccessToException(
            response.status.value,
            "get shopping list call failed with status: " + response.status.value
        )

        val result: ApiShoppingList = response.body()

        return ShoppingList.create(apiValue = result)
    }

    override suspend fun deleteList(listIdToDelete: String) {
        val urlString = remoteApi.buildPath("$shoppingListPath/${listIdToDelete}")
        listShopAnalytics.debug("deleting list, id: $listIdToDelete")

        val response = remoteApi.deleteRequest(urlString)

        remoteApi.mapNonSuccessToException(
            response.status.value,
            "get shopping list call failed with status: " + response.status.value
        )

    }
    override suspend fun createList(payload: PostShoppingList): String {
        val payload = Json.encodeToString(payload)
        val urlString = remoteApi.buildPath(shoppingListPath)
        val response = remoteApi.postRequest(urlString, payload)
        remoteApi.mapNonSuccessToException(
            response.status.value,
            "create list call failed with status: " + response.status.value
        )
        val location = remoteApi.pullLocation(response)
        val elements = location.split("/")
        return elements.last()
    }

    override suspend fun mergeLocalListWithServer(listMergeRequest: PutMergeRequest): ShoppingList {
        val token = remoteApi.token()
        val urlString = remoteApi.buildPath("$shoppingListPath/shared")
        listShopAnalytics.debug("merging the local list with the server list, the token is: $token")

        // convert object to json payload
        val payload = Json.encodeToString(listMergeRequest)

        val response = remoteApi.putRequest(urlString, payload)

        remoteApi.mapNonSuccessToException(
            response.status.value,
            "merge shopping list call failed with status: " + response.status.value
        )

        val result: MergeResult = response.body()

        return ShoppingList.create(apiValue = result.mergeResult.shoppingList)
    }

}
