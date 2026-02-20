package com.listshop.bff.remote

import com.listshop.bff.data.remote.PostGenericPayload
import com.listshop.bff.data.remote.ApiDeviceInfo
import com.listshop.bff.data.remote.ApiDish
import com.listshop.bff.data.remote.ApiRequiredClientVersion
import com.listshop.bff.data.remote.ApiUserProperties
import com.listshop.bff.data.remote.PostChangePassword
import com.listshop.bff.data.remote.PostTokenRequest
import com.listshop.bff.data.remote.PostUserLogin

public interface DishApi {
    suspend fun searchDishes(queryString: String? = null): List<ApiDish>


}
