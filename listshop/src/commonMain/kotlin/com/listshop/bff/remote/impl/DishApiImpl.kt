package com.listshop.bff.remote.impl

import com.listshop.analytics.AnalyticsHandle
import com.listshop.bff.data.model.ShoppingList
import com.listshop.bff.data.remote.PostGenericPayload
import com.listshop.bff.data.remote.ApiDeviceInfo
import com.listshop.bff.data.remote.ApiDish
import com.listshop.bff.data.remote.ApiRequiredClientVersion
import com.listshop.bff.data.remote.ApiShoppingListEmbedded
import com.listshop.bff.data.remote.ApiUserProperties
import com.listshop.bff.data.remote.ApiWrappedUser
import com.listshop.bff.data.remote.EmbeddedDishResourceList
import com.listshop.bff.data.remote.PostChangePassword
import com.listshop.bff.data.remote.PostTokenRequest
import com.listshop.bff.data.remote.PostUserLogin
import com.listshop.bff.exceptions.ApiException
import com.listshop.bff.exceptions.AuthenticationException
import com.listshop.bff.remote.DishApi
import com.listshop.bff.remote.ListShopRemoteApi
import com.listshop.bff.remote.UserApi
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

internal class DishApiImpl(
    val remoteApi: ListShopRemoteApi,
    val analyticsHandle: AnalyticsHandle
) : DishApi {

     suspend fun deleteUser() {
        val urlString = remoteApi.buildPath("/user")

        val response = remoteApi.deleteRequest(urlString)


        remoteApi.mapNonSuccessToException(response.status.value,
            ApiException("password reset request failed with status: " + response.status.value)
        )
    }

    override suspend fun searchDishes(queryString: String? ): List<ApiDish> {
        val effectiveQueryString = queryString ?: ""
        val urlString = remoteApi.buildPath("/dish${effectiveQueryString}")
        analyticsHandle.listShopAnalytics.debug("retrieving dishes.")

        val response = remoteApi.getRequest(urlString)

        remoteApi.mapNonSuccessToException(
            response.status.value,
            ApiException("get shopping list call failed with status: " + response.status.value)
        )

        val result: EmbeddedDishResourceList =
            response.body()

        return result.embeddedList.dishResourceList.map { it.embeddedDish}
    }

    /*
            os_log("beginning searchDishes", log: Log.network, type: .info)
        let query = queryString ?? ""
        // put together url
        let escapedQuery = encodeQueryString(query)

        guard let url = URL(string: remoteSession.baseUrlString + "/dish" + escapedQuery) else {
            os_log("Cant convert string to url.", log: Log.network, type: .info)
            let lse = ListShopError(type: .network, title: "CANTMAKEURL", message: "Can't construct url")
            return Promise(error: lse)
        }
        // put together request
        var request = remoteSession.request(for: url)
        request.httpMethod = "GET"

        // make post
        return firstly {
            try performDataRequest(urlRequest: request)
        }
                .map { data -> [ApiDish] in
                    guard data.count > 2 else {
                        return []
                    }
                    let decoder = JSONDecoder()
                    let embeddedList = try decoder.decode(EmbeddedApiDishList.self, from: data) as EmbeddedApiDishList
                    os_log("ListShopRemoteApi - ending list of lists", log: Log.network, type: .info)
                    guard let resourceList = embeddedList.resourceList["dishResourceList"] else {
                        os_log("Fail in ListShopRemoteApi - can't decode received json", log: Log.network, type: .error)
                        let lse = ListShopError(type: .network, title: "CANTUNPACKRESPONSE", message: "Can't unpack response")
                        throw lse
                    }
                    return resourceList.map({ $0.dish })
                }

     */


}
