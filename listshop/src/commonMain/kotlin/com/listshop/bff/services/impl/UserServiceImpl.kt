package com.listshop.bff.services.impl

import com.listshop.analytics.ListShopAnalytics
import com.listshop.bff.data.remote.ApiDeviceInfo
import com.listshop.bff.data.remote.PostUserLogin
import com.listshop.bff.remote.UserApi
import com.listshop.bff.services.UserService
import com.listshop.bff.services.UserSessionService
import kotlin.math.min

class UserServiceImpl internal constructor(
    private val remoteApi: UserApi,
    private val sessionService: UserSessionService,
    private val listShopAnalytics : ListShopAnalytics
) : UserService {
    override suspend fun authenticateUser() {
        if (sessionService.currentSession().userToken == null) {
            //MM errors! how to throw errors!!
        }
        val postDeviceInfo = buildDeviceInfo()
        remoteApi.authenticateUser(postDeviceInfo)
        //MM nfl - do properties here
    }

    override suspend fun logoutUser() {
        if (sessionService.currentSession().userToken == null) {
            return
        }

        // logout on the server
        try {
            remoteApi.logoutUser()
        } catch (e: Exception) {
            listShopAnalytics.debug("remote call to logout failed. continuing logout on device. message: " + e.message)
        }

        // clear session
        sessionService.setUserToken(null)
        //MM nfl also clear list (server list) and user properties
    }

    override suspend fun signInUser(userName: String, password: String) {
        val postLoginUser = prepareSignInObject(userName, password)

        val token = remoteApi.signInUser(postLoginUser)
        listShopAnalytics.debug("the token is : " + token)
        // save results
        sessionService.setUserLastSignedInToNow()
        sessionService.setUserToken(token)
        sessionService.setUserName(userName)
    }

    private fun buildDeviceInfo(): ApiDeviceInfo {
        val appInfo = sessionService.currentAppInfo()
        return ApiDeviceInfo(
            appInfo.name,
            appInfo.model,
            appInfo.os,
            appInfo.osVersion,
            appInfo.clientType,
            appInfo.clientVersion,
            appInfo.buildNumber,
            appInfo.deviceId
        )
    }

    private fun prepareSignInObject(userName: String, password: String): PostUserLogin {
        val cleanedName = cleanStringForServer(userName, RemoteConstants.NORMAL_STRING_LENGTH)
        val cleanedPassword = cleanStringForServer(password, RemoteConstants.NORMAL_STRING_LENGTH)
        val deviceInfo = buildDeviceInfo()
        return PostUserLogin(cleanedName, cleanedPassword, deviceInfo)
    }

    private fun cleanStringForServer(value: String, length: Int): String {
        val cutLength = min(length, value.length)
        return value.subSequence(0, cutLength).toString()
    }


}

class RemoteConstants {
    companion object {
        const val NORMAL_STRING_LENGTH = 300
    }
}