package com.listshop.bff.services.impl

import com.listshop.analytics.AppInfo
import com.listshop.bff.data.model.UserInfo
import com.listshop.bff.data.state.UserSessionState
import com.listshop.bff.db.UserInfoEntity
import com.listshop.bff.repositories.SessionInfoRepository
import com.listshop.bff.services.UserSession
import com.listshop.bff.services.UserSessionService
import kotlinx.datetime.Clock

class UserSessionServiceImpl internal constructor(
    private val sessionRepo: SessionInfoRepository,
    val appInfo: AppInfo
) : UserSessionService {
    private var _userSession: UserSession? = null

    override fun currentSession(): UserSession {
        if (_userSession != null) {
            return _userSession!!
        }
        initializeUserSession()
        return _userSession!!
    }

    override fun currentAppInfo(): AppInfo {
        return appInfo
    }

    override fun setUserToken(token: String?) {
        val userInfo = getUserInfo()
        userInfo.userToken = token
        updateUserInfo(userInfo)
        refreshUserSession()

    }

    override fun setUserName(name: String) {
        val userInfo = getUserInfo()
        userInfo.userName = name
        updateUserInfo(userInfo)
        refreshUserSession()
    }

    override fun setUserLastSeenToNow() {
        val userInfo = getUserInfo()
        val now = Clock.System.now()
        userInfo.userLastSeen = now.toString()
        updateUserInfo(userInfo)
        refreshUserSession()
    }

    override fun setUserLastSignedInToNow() {
        val userInfo = getUserInfo()
        val now = Clock.System.now()
        userInfo.userLastSignedIn = now.toString()
        updateUserInfo(userInfo)
        refreshUserSession()
    }

    private fun updateUserInfo(userInfo: UserInfo) : UserInfo{
        sessionRepo.updateUserInfo(userInfo)
        return getUserInfo()
    }

    private fun getUserInfo(): UserInfo {
        val userInfoEntity = getOrCreateUserInfoEntity()
        return UserInfo.create(userInfoEntity)
    }


    private fun getOrCreateUserInfoEntity(): UserInfoEntity {
        var userInfo = sessionRepo.getUserInfo()
        if (userInfo != null) {
            return userInfo
        }
        userInfo = sessionRepo.createUserInfo()
        return userInfo!!
    }

    private fun refreshUserSession() {
        val userInfo = getOrCreateUserInfoEntity()
        //MM nfl - list info here
        val sessionState = determineUserSessionState(userInfo)
        _userSession = UserSession(
            userInfo.userName,
            userInfo.userToken,
            userInfo.userLastSeen,
            userInfo.userLastSignedIn,
            sessionState,
            appInfo.clientVersion ?: "unknown",
            appInfo.buildNumber ?: "unknown",
            appInfo.baseUrl
        )

    }

    private fun determineUserSessionState(userInfo: UserInfoEntity): UserSessionState {
        if (userInfo.userName != null && userInfo.userToken != null) {
            return UserSessionState.User
        }
        if (userInfo.userName != null) {
            return UserSessionState.UserLoggedOut
        }
        //MM nfl (note for later) skipping local list right now
        return UserSessionState.Anon
    }

    private fun initializeUserSession() {
        val userInfo = getOrCreateUserInfoEntity()
        val sessionState = determineUserSessionState(userInfo)
        _userSession = UserSession(
            userInfo.userName,
            userInfo.userToken,
            userInfo.userLastSeen,
            userInfo.userLastSignedIn,
            sessionState,
            appInfo.clientVersion ?: "unknown",
            appInfo.buildNumber ?: "unknown",
            appInfo.baseUrl
        )
    }


}