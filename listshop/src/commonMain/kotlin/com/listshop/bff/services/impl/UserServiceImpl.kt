package com.listshop.bff.services.impl

import com.listshop.analytics.AnalyticsHandle
import com.listshop.bff.data.model.TokenType
import com.listshop.bff.data.remote.PostGenericPayload
import com.listshop.bff.data.remote.ApiDeviceInfo
import com.listshop.bff.data.remote.ApiProperty
import com.listshop.bff.data.remote.ApiUserProperties
import com.listshop.bff.data.remote.PostChangePassword
import com.listshop.bff.data.remote.PostTokenRequest
import com.listshop.bff.data.remote.PostUserLogin
import com.listshop.bff.exceptions.BadParameterException
import com.listshop.bff.exceptions.LoggedOutException
import com.listshop.bff.remote.UserApi
import com.listshop.bff.services.SessionService
import com.listshop.bff.services.UserService
import io.ktor.utils.io.core.toByteArray
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlin.math.min

class UserServiceImpl internal constructor(
    private val remoteApi: UserApi,
    private val sessionService: SessionService,
    private val analyticsHandle: AnalyticsHandle
) : UserService {
    override suspend fun authenticateUser() {
        if (sessionService.currentUserSession().userToken == null) {
            //MM errors! how to throw errors!!
        }
        val postDeviceInfo = buildDeviceInfo()
        remoteApi.authenticateUser(postDeviceInfo)
        //MM nfl - do properties here
    }

    override suspend fun logoutUser() {
        if (sessionService.currentUserSession().userToken == null) {
            throw LoggedOutException("User cannot logout when not logged in")
        }

        // logout on the server
        try {
            remoteApi.logoutUser()
        } catch (e: Exception) {
            analyticsHandle.listShopAnalytics.debug("remote call to logout failed. continuing logout on device. message: " + e.message)
        }

        // clear session
        sessionService.clearUserSession()
    }

    override suspend fun signInUser(userName: String, password: String) {
        val postLoginUser = prepareSignInOrUpObject(userName, password)

        val token = remoteApi.signInUser(postLoginUser)
        analyticsHandle.listShopAnalytics.debug("the token is : " + token)
        // save results
        sessionService.setUserLastSignedInToNow()
        sessionService.setUserToken(token)
        sessionService.setUserName(userName)
        // refresh user properties
        refreshUserProperties()
    }

    override suspend fun signUpUser(userName: String, password: String) {
        val postSignupUser = prepareSignInOrUpObject(userName, password)

        val token = remoteApi.signUpUser(postSignupUser)
        analyticsHandle.listShopAnalytics.debug("the token is : " + token)
        // save results
        sessionService.setUserLastSignedInToNow()
        sessionService.setUserToken(token)
        sessionService.setUserName(userName)
        sessionService.setUserCreatedOnServerToNow()
    }

    override suspend fun checkUserNameTaken(userName: String): Boolean {
        val safeUserName = userName.substring(0, min(255, userName.length))
        if (safeUserName.isEmpty()) {
            throw BadParameterException(message = "user name not given")
        }
        val payload = PostGenericPayload(parameters = arrayOf(safeUserName))
        return remoteApi.checkUserNameIsTaken(payload = payload);
    }

    override suspend fun updateUserProperty(property: String, value: String) {
        val saveValue = value.substring(0, min(255, value.length))
        if (saveValue.isEmpty() || property.isBlank()) {
            throw BadParameterException(message = "property or value are empty")
        }
        // update property in session
        sessionService.setUserProperty(property, saveValue)

        // update remotely and refresh
        val apiProperty =  ApiProperty(property, value)
        val userProperties = ApiUserProperties(listOf(apiProperty))
        remoteApi.updateUserProperty(userProperties)
        refreshUserProperties()
    }

    override suspend fun requestPasswordReset(userName: String) {
        val safeUserName = userName.substring(0, min(255, userName.length))
        if ( safeUserName.isBlank()) {
            throw BadParameterException(message = "user name is blank")
        }

        // update remotely and refresh
        val tokenRequest = PostTokenRequest( safeUserName, TokenType.PASSWORD_RESET.display)
        remoteApi.requestPasswordReset(tokenRequest)

    }

    override suspend fun deleteUser() {
        if (sessionService.currentUserSession().userToken == null) {
            throw BadParameterException("user is not logged in")
        }
        // remote call to delete user
        remoteApi.deleteUser()
        // clear user data from session
        sessionService.clearUserSession()
    }

    @OptIn(ExperimentalEncodingApi::class)
    override suspend fun changePassword(originalPassword: String, newPassword: String) {
        val safeOriginalPassword = originalPassword.substring(0, min(255, originalPassword.length))
        val safeNewPassword = newPassword.substring(0, min(255, newPassword.length))

        if (safeNewPassword.isEmpty() || safeOriginalPassword.isEmpty()) {
            throw BadParameterException("new or old password not entered")
        }
        val encodedOriginal: String = Base64.Default.encode(safeOriginalPassword.toByteArray())
        val encodedNew: String = Base64.Default.encode(safeNewPassword.toByteArray())
        val payload = PostChangePassword(encodedOriginal, encodedNew)
        return remoteApi.changePassword(payload)


 
    }


    private fun buildDeviceInfo(): ApiDeviceInfo {
        val appInfo = sessionService.currentAppInfo()
        return ApiDeviceInfo(
            appInfo.name,
            appInfo.model,
            appInfo.os,
            appInfo.osVersion,
            "Mobile",
            appInfo.clientVersion,
            appInfo.buildNumber,
            appInfo.deviceId
        )
    }

    private fun prepareSignInOrUpObject(userName: String, password: String): PostUserLogin {
        val cleanedName = cleanStringForServer(userName, RemoteConstants.NORMAL_STRING_LENGTH)
        val cleanedPassword = cleanStringForServer(password, RemoteConstants.NORMAL_STRING_LENGTH)
        val deviceInfo = buildDeviceInfo()
        return PostUserLogin(cleanedName, cleanedPassword, deviceInfo)
    }

    private fun cleanStringForServer(value: String, length: Int): String {
        val cutLength = min(length, value.length)
        return value.subSequence(0, cutLength).toString()
    }

    private suspend fun refreshUserProperties() {
        val serverProperties  = remoteApi.retrieveUserProperties()
        val propertyMap = serverProperties.userProperties
            .filter{it.key != null && it.value != null}
            .map{it.key!! to it.value!!}
            .toMap()
        sessionService.setUserProperties(propertyMap)
    }

}

class RemoteConstants {
    companion object {
        const val NORMAL_STRING_LENGTH = 300
    }
}
