package com.listshop.bff.remote.impl

import com.listshop.analytics.AnalyticsHandle
import com.listshop.bff.data.remote.ApiDish
import com.listshop.bff.data.remote.ApiDishList
import com.listshop.bff.remote.DishApi
import com.listshop.bff.remote.ListShopRemoteApi
import io.ktor.client.call.body

internal class DishApiImpl(
    val remoteApi: ListShopRemoteApi,
    val analyticsHandle: AnalyticsHandle
) : DishApi {



    override suspend fun searchDishes(queryString: String? ): List<ApiDish> {
        val effectiveQueryString = queryString ?: ""
        val urlString = remoteApi.buildPath("/v2/dish${effectiveQueryString}")
        analyticsHandle.listShopAnalytics.debug("retrieving dishes.")

        val response = remoteApi.getRequest(urlString)

        remoteApi.mapNonSuccessToException(
            response.status.value,
            "get shopping list call failed with status: " + response.status.value
        )

        val result: ApiDishList =
            response.body()

        return result.dishes
    }

}
