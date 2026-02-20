package com.listshop.bff.services

import com.listshop.analytics.AppInfo
import com.listshop.bff.data.session.DishSessionMemory
import com.listshop.bff.data.session.UserSession

interface SessionService {

    fun currentUserSession(): UserSession

    fun currentAppInfo(): AppInfo

    fun setUserToken(token: String?)

    fun setUserName(name: String)

    fun setUserLastSeenToNow()

    fun setUserLastSignedInToNow()

    fun setUserCreatedOnServerToNow()

    fun setLookupDataLastSyncedToNow()

    fun setServerListId(listId: String)

    fun setLocalListUpdated(updateString: String)

    fun setLocalListUpdated()

    fun setLocalLastSynced()

    fun setServerListLastSynced()


    fun currentListSession(): ListSession

    fun setUserProperty(property: String, saveValue: String)

    fun setUserProperties(propertyMap: Map<String, String>)

    fun clearUserSession()
    fun currentDishMemory(): DishSessionMemory
}
