package com.listshop.bff.remote.impl

import com.listshop.bff.data.remote.ApiDeviceInfo
import com.listshop.bff.data.remote.ApiWrappedUser
import com.listshop.bff.data.remote.PostUserLogin
import com.listshop.bff.exceptions.AuthenticationException
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

internal class UserApiImpl(
    val remoteApi: ListShopRemoteApi
) : UserApi {



    override suspend fun authenticateUser(postDeviceInfo: ApiDeviceInfo)  {

        val response: HttpResponse = remoteApi.client(remoteApi.token())
            .post("/auth/authenticate") {
                contentType(ContentType.Application.Json)
                setBody(postDeviceInfo)
        }


        //MM nfl check response
    }
    override suspend fun signInUser(postLoginUser: PostUserLogin): String {

        val urlString = remoteApi.buildPath("/auth")
        val userLoginPayload = Json.encodeToString(postLoginUser)
        val response = remoteApi.postRequest(urlString, userLoginPayload)

        remoteApi.mapNonSuccessToException(response.status.value,AuthenticationException("login call failed with status: " + response.status.value))

        val wrappedUser: ApiWrappedUser = response.body()
        return wrappedUser.user.token ?: "token"
    }

    override suspend fun logoutUser() {

        val urlString = remoteApi.buildPath("/auth/logout")
        val response = remoteApi.getRequest(urlString)

        remoteApi.mapNonSuccessToException(response.status.value,AuthenticationException("logout call failed with status: " + response.status.value))
    }


}
