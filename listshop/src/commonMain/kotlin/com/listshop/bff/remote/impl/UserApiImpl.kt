package com.listshop.bff.remote.impl

import com.listshop.bff.data.remote.PostGenericPayload
import com.listshop.bff.data.remote.ApiDeviceInfo
import com.listshop.bff.data.remote.ApiRequiredClientVersion
import com.listshop.bff.data.remote.ApiUserProperties
import com.listshop.bff.data.remote.ApiWrappedUser
import com.listshop.bff.data.remote.PostChangePassword
import com.listshop.bff.data.remote.PostTokenRequest
import com.listshop.bff.data.remote.PostUser
import com.listshop.bff.data.remote.PostUserSignin
import com.listshop.bff.data.remote.PostUserSignup
import com.listshop.bff.exceptions.LoginException
import com.listshop.bff.exceptions.LogoutException
import com.listshop.bff.exceptions.SignUpException
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
    }

    override suspend fun signInUser(postLoginUser: PostUserSignin): String {

        val urlString = remoteApi.buildPath("/auth")
        val userLoginPayload = Json.encodeToString(postLoginUser)
        val response = remoteApi.postRequest(urlString, userLoginPayload)

        remoteApi.mapNonSuccessToException(response.status.value,
            LoginException("login call failed with status: " + response.status.value)
        )

        val wrappedUser: ApiWrappedUser = response.body()
        return wrappedUser.user.token ?: "token"
    }

    override suspend fun signUpUser(postSignupUser: PostUserSignup): String {

        val urlString = remoteApi.buildPath("/user")
        val userPayload = Json.encodeToString(postSignupUser)
        val response = remoteApi.postRequest(urlString, userPayload)

        remoteApi.mapNonSuccessToException(response.status.value,
            SignUpException("login call failed with status: " + response.status.value)
        )

        val wrappedUser: ApiWrappedUser = response.body()
        return wrappedUser.user.token ?: "token"
    }

    override suspend fun logoutUser() {

        val urlString = remoteApi.buildPath("/auth/logout")
        val response = remoteApi.getRequest(urlString)

        remoteApi.mapNonSuccessToException(response.status.value, LogoutException("logout call failed with status: " + response.status.value))
    }

    override suspend fun retrieveRequiredClientVersion(): ApiRequiredClientVersion {
        val urlString = remoteApi.buildPath("/user/client/version")
        val response = remoteApi.getRequest(urlString)

        remoteApi.mapNonSuccessToException(response.status.value,
            "get required version call failed with status: " + response.status.value
        )

        return response.body()
    }

    override suspend fun checkUserNameIsTaken(payload: PostGenericPayload): Boolean {
        val urlString = remoteApi.buildPath("/user/name")
        val jsonPayload = Json.encodeToString(payload)

        val response = remoteApi.postRequest(urlString, jsonPayload)

        remoteApi.mapNonSuccessToException(response.status.value,
            "check user name call failed with status: " + response.status.value
        )

        return response.body<Boolean>() ?: false
    }

    override suspend fun changePassword(payload: PostChangePassword) {
        val urlString = remoteApi.buildPath("/user/password")
        val jsonPayload = Json.encodeToString(payload)

        val response = remoteApi.postRequest(urlString, jsonPayload)

        remoteApi.mapNonSuccessToException(response.status.value,
            "change password call failed with status: " + response.status.value
        )
    }

    override suspend fun updateUserProperty(payload: ApiUserProperties) {
        val urlString = remoteApi.buildPath("/user/properties")
        val jsonPayload = Json.encodeToString(payload)

        val response = remoteApi.postRequest(urlString, jsonPayload)

        remoteApi.mapNonSuccessToException(response.status.value,
            "change password call failed with status: " + response.status.value
        )
    }

    override suspend fun retrieveUserProperties(): ApiUserProperties {
        val urlString = remoteApi.buildPath("/user/properties")

        val response = remoteApi.getRequest(urlString)

        remoteApi.mapNonSuccessToException(response.status.value,
            "retrieve properties failed with status: " + response.status.value
        )
        return response.body()
    }

    override suspend fun requestPasswordReset(payload: PostTokenRequest) {
        val urlString = remoteApi.buildPath("/user/token/tokenrequest")
        val jsonPayload = Json.encodeToString(payload)

        val response = remoteApi.postRequest(urlString, jsonPayload)


        remoteApi.mapNonSuccessToException(response.status.value,
            "password reset request failed with status: " + response.status.value
        )
    }

    override suspend fun deleteUser() {
        val urlString = remoteApi.buildPath("/user")

        val response = remoteApi.deleteRequest(urlString)


        remoteApi.mapNonSuccessToException(response.status.value,
            "password reset request failed with status: " + response.status.value
        )
    }


}
