package com.listshop.bff.services

import com.listshop.analytics.AppInfo

interface UserSessionService {

    fun currentSession(): UserSession

    fun currentAppInfo(): AppInfo

    fun setUserToken(token: String?)

    fun setUserName(name: String)

    fun setUserLastSeenToNow()

    fun setUserLastSignedInToNow()

    fun setLookupDataLastSyncedToNow()

    fun setServerListId(listId: String)

    fun setLocalListUpdated(updateString: String)

    fun setLocalListUpdated()

    fun setLocalLastSynced()

    fun setServerListLastSynced()


}
