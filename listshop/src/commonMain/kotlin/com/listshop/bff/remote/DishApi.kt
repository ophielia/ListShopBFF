package com.listshop.bff.remote

import com.listshop.bff.data.remote.ApiDish

public interface DishApi {
    suspend fun searchDishes(queryString: String? = null): List<ApiDish>


}
