package com.listshop.bff.services.impl

import com.listshop.analytics.AppInfo
import com.listshop.bff.data.model.ListInfo
import com.listshop.bff.data.model.UserInfo
import com.listshop.bff.data.session.DishSessionMemory
import com.listshop.bff.data.state.UserSessionState
import com.listshop.bff.db.ListInfoEntity
import com.listshop.bff.db.UserInfoEntity
import com.listshop.bff.db.UserPropertiesEntity
import com.listshop.bff.repositories.SessionInfoRepository
import com.listshop.bff.services.ListSession
import com.listshop.bff.data.session.UserSession
import com.listshop.bff.services.SessionService
import kotlinx.datetime.Clock

class SessionServiceImpl internal constructor(
    private val sessionRepo: SessionInfoRepository,
    val appInfo: AppInfo
) : SessionService {
    private var _userSession: UserSession? = null
    private var _listSession: ListSession? = null
    private var _dishSessionMemory: DishSessionMemory? = null

    override fun currentUserSession(): UserSession {
        if (_userSession != null) {
            return _userSession!!
        }
        refreshOrInitializeUserSession()
        return _userSession!!
    }

    override fun currentListSession(): ListSession {
        if (_listSession != null) {
            return _listSession!!
        }
        refreshOrInitializeListSession()
        return _listSession!!
    }

    override fun currentDishMemory(): DishSessionMemory {
        if (_dishSessionMemory != null) {
            return _dishSessionMemory!!
        }
        _dishSessionMemory = DishSessionMemory()
        return _dishSessionMemory!!
    }


    override fun currentAppInfo(): AppInfo {
        return appInfo
    }

    override fun setUserToken(token: String?) {
        val userInfo = getUserInfo()
        userInfo.userToken = token
        updateUserInfo(userInfo)
        refreshOrInitializeUserSession()

    }

    override fun setUserName(name: String) {
        val userInfo = getUserInfo()
        userInfo.userName = name
        updateUserInfo(userInfo)
        refreshOrInitializeUserSession()
    }

    override fun setUserLastSeenToNow() {
        val userInfo = getUserInfo()
        val now = Clock.System.now()
        userInfo.userLastSeen = now.toString()
        updateUserInfo(userInfo)
        refreshOrInitializeUserSession()
    }

    override fun setUserLastSignedInToNow() {
        val userInfo = getUserInfo()
        val now = Clock.System.now()
        userInfo.userLastSignedIn = now.toString()
        updateUserInfo(userInfo)
        refreshOrInitializeUserSession()
    }

    override fun setUserCreatedOnServerToNow() {
        val userInfo = getUserInfo()
        val now = Clock.System.now()
        userInfo.userCreatedOnServer = now.toString()
        updateUserInfo(userInfo)
        refreshOrInitializeUserSession()
    }

    override fun setLookupDataLastSyncedToNow() {
        val listInfo = getListInfo()
        val now = Clock.System.now()
        listInfo.lookupDataLastSynced = now.toString()
        updateListInfo(listInfo)
        refreshOrInitializeUserSession()
    }

    override fun setServerListId(listId: String) {
        val listInfo = getListInfo()
        listInfo.serverListId = listId
        updateListInfo(listInfo)
        refreshOrInitializeUserSession()
    }

    override fun setLocalListUpdated(updateString: String) {
        val listInfo = getListInfo()
        listInfo.localListUpdated = updateString
        updateListInfo(listInfo)
        refreshOrInitializeUserSession()
    }

    override fun setUserProperty(property: String, saveValue: String) {
        sessionRepo.createOrReplaceUserProperty(property, saveValue)
        refreshOrInitializeUserSession()
    }

    override fun setUserProperties(propertyMap: Map<String, String>) {
        sessionRepo.replaceUserProperties(propertyMap)
        refreshOrInitializeUserSession()
    }


    override fun setLocalListUpdated() {
        val listInfo = getListInfo()
        listInfo.localListUpdated = Clock.System.now().toString()
        updateListInfo(listInfo)
        refreshOrInitializeUserSession()
    }

    override fun setLocalLastSynced() {
        val listInfo = getListInfo()
        listInfo.localLastSynced = Clock.System.now().toString()
        updateListInfo(listInfo)
        refreshOrInitializeUserSession()
    }

    override fun setServerListLastSynced() {
        val listInfo = getListInfo()
        listInfo.serverListLastSynced = Clock.System.now().toString()
        updateListInfo(listInfo)
        refreshOrInitializeUserSession()
    }

    override fun clearUserSession() {
        clearUserInfo()
        clearListInfo()
        clearUserProperties()
        refreshOrInitializeUserSession()
    }

    private fun clearUserProperties() {
        sessionRepo.deleteAllUserProperties()
    }

    private fun clearListInfo() {
        sessionRepo.deleteListInfo()
    }


    private fun getUserInfo(): UserInfo {
        val userInfoEntity = getOrCreateUserInfoEntity()
        return UserInfo.create(userInfoEntity)
    }

    private fun getListInfo(): ListInfo {
        val listInfoEntity = getOrCreateListInfoEntity()
        return ListInfo.create(listInfoEntity)
    }

    private fun updateUserInfo(userInfo: UserInfo): UserInfo {
        sessionRepo.updateUserInfo(userInfo)
        return getUserInfo()
    }

    private fun updateListInfo(listInfo: ListInfo): ListInfo {
        sessionRepo.updateListInfo(listInfo)
        return getListInfo()
    }

    private fun getOrCreateUserInfoEntity(): UserInfoEntity {
        var userInfo = sessionRepo.getUserInfo()
        if (userInfo != null) {
            return userInfo
        }
        userInfo = sessionRepo.createUserInfo()
        return userInfo!!
    }
    private fun clearUserInfo() {
        sessionRepo.deleteUserInfo()
    }

    private fun getOrCreateListInfoEntity(): ListInfoEntity {
        var listInfo = sessionRepo.getListInfo()
        if (listInfo != null) {
            return listInfo
        }
        listInfo = sessionRepo.createListInfo()
        return listInfo!!
    }


    private fun getUserPropertiesAsMap() : Map<String, String> {
        val properties : List<UserPropertiesEntity> = sessionRepo.getUserProperties()
        if (properties.isNullOrEmpty()) {
            return emptyMap()
        }

        return properties
            .filter{it.key != null}
            .filter{it.property_value != null}
            .map { it.key!! to it.property_value!! }.toMap()
    }

    private fun refreshOrInitializeUserSession() {
        val userInfo = getOrCreateUserInfoEntity()
        val listInfo = getOrCreateListInfoEntity()
        val dishMemory = currentDishMemory()
        val userProperties = getUserPropertiesAsMap()
        val sessionState = determineUserSessionState(userInfo, listInfo)
        _userSession = UserSession(
            userInfo.userName,
            userInfo.userToken,
            userInfo.userLastSeen,
            userInfo.userLastSignedIn,
            sessionState,
            userProperties,
            dishMemory,
            appInfo.clientVersion ?: "unknown",
            appInfo.buildNumber ?: "unknown",
            appInfo.baseUrl
        )

    }


    private fun refreshOrInitializeListSession() {
        val listInfo = getOrCreateListInfoEntity()
        _listSession = ListSession(
            lastInternalUpdate = listInfo.lastInternalUpdate,
            localListUpdated = listInfo.localListUpdated,
            serverListId = listInfo.serverListId,
            lookupDataLastSynced = listInfo.lookupDataLastSynced,
            localLastSynced = listInfo.localLastSynced,
            serverListLastSynced = listInfo.serverListLastSynced
        )

    }

    private fun determineUserSessionState(userInfo: UserInfoEntity, listInfo: ListInfoEntity): UserSessionState {
        if (userInfo.userName != null && userInfo.userToken != null) {
            return UserSessionState.User
        }
        if (userInfo.userName != null) {
            return UserSessionState.UserLoggedOut
        }
        if (listInfo.localListUpdated != null) {
            return UserSessionState.Anon
        }
        return UserSessionState.AnonNoList
    }
}
