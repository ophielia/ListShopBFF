package com.listshop.bff.services.impl

import com.listshop.analytics.AnalyticsHandle
import com.listshop.analytics.ListShopAnalytics
import com.listshop.bff.data.model.Dish
import com.listshop.bff.data.model.DishSearchParameters
import com.listshop.bff.remote.DishApi
import com.listshop.bff.repositories.ListRepository
import com.listshop.bff.services.DishService
import com.listshop.bff.services.SessionService

class DishServiceImpl internal constructor(
    private val remoteApi: DishApi,
    private val sessionService: SessionService,
    private val analyticsHandle: AnalyticsHandle
) : DishService {


    override suspend fun retrieveDishList(searchParameters: DishSearchParameters): List<Dish> {
        val queryStringParts = ArrayList<String>()
        if (!searchParameters.currentFilterList.isEmpty()) {
            val includedTags = searchParameters.currentFilterList.joinToString(separator = ",")
            queryStringParts.add("includedTags=${includedTags}")
        }
        if (searchParameters.searchFragment != null && !searchParameters.searchFragment?.isEmpty()!!) {
            val searchFragment = "searchFragment=${searchParameters.searchFragment!!}"
            queryStringParts.add(searchFragment)
        }
        if (searchParameters.sortKey != null) {
            val sortKey = searchParameters?.sortKey
            val sortKeyString = "sortKey=${sortKey}"
            queryStringParts.add(sortKeyString)
        }
        if (searchParameters.sortDirection != null) {
            val sortDirection = searchParameters?.sortDirection
            val sortDirectionString = "sortDirection=${sortDirection}"
            queryStringParts.add(sortDirectionString)
        }

        var queryString = ""
        if (!queryStringParts.isEmpty()) {
            queryString = "?" + queryStringParts.joinToString(separator = "&")
        }
        return doRetrieveDishList(queryString)
    }


    private suspend fun allDishes(): List<Dish> {
        return doRetrieveDishList(null)
    }

    private suspend fun doRetrieveDishList(queryString: String?): List<Dish> {
        val apiDishes = remoteApi.searchDishes(queryString = queryString)
        return apiDishes.map { apiDish -> Dish.create(apiDish) }
    }
}
