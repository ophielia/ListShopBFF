package com.listshop.bff.repositories

import com.listshop.bff.data.model.ListInfo
import com.listshop.bff.data.model.UserInfo
import com.listshop.bff.db.ListInfoEntity
import com.listshop.bff.db.UserInfoEntity
import com.listshop.bff.db.UserPropertiesEntity


interface SessionInfoRepository {

    fun getUserInfo(): UserInfoEntity?

    fun createUserInfo(): UserInfoEntity?

    fun updateUserInfo(userInfo: UserInfo)

    fun getListInfo(): ListInfoEntity?

    fun createListInfo(): ListInfoEntity?

    fun updateListInfo(listInfo: ListInfo)

    fun getUserProperties(): List<UserPropertiesEntity>

    fun createOrReplaceUserProperty(key: String, value: String)

    fun replaceUserProperties(propertyMap: Map<String, String>)

    fun deleteUserInfo()
    fun deleteListInfo()
    fun deleteAllUserProperties()
}
