package com.listshop.bff.remote

import com.listshop.bff.data.remote.ApiDeviceInfo
import com.listshop.bff.data.remote.PostUserLogin

internal interface UserApi {

    suspend fun authenticateUser(postDeviceInfo: ApiDeviceInfo)
    suspend fun signInUser(postLoginUser: PostUserLogin): String
    suspend fun logoutUser()

}
