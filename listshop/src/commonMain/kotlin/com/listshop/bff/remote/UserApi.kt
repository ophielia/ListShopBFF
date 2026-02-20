package com.listshop.bff.remote

import com.listshop.bff.data.remote.PostGenericPayload
import com.listshop.bff.data.remote.ApiDeviceInfo
import com.listshop.bff.data.remote.ApiRequiredClientVersion
import com.listshop.bff.data.remote.ApiUserProperties
import com.listshop.bff.data.remote.PostChangePassword
import com.listshop.bff.data.remote.PostTokenRequest
import com.listshop.bff.data.remote.PostUserLogin

public interface UserApi {

    suspend fun authenticateUser(postDeviceInfo: ApiDeviceInfo)
    suspend fun signInUser(postLoginUser: PostUserLogin): String
    suspend fun logoutUser()
    suspend fun retrieveRequiredClientVersion(): ApiRequiredClientVersion
    suspend fun checkUserNameIsTaken(payload: PostGenericPayload): Boolean
    suspend fun changePassword(payload: PostChangePassword)
    suspend fun updateUserProperty(payload: ApiUserProperties)
    suspend fun retrieveUserProperties(): ApiUserProperties
    suspend fun requestPasswordReset(payload: PostTokenRequest)
    suspend fun deleteUser()
    suspend fun signUpUser(postSignupUser: PostUserLogin): String

}
