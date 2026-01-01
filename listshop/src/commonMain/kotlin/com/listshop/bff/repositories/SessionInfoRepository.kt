package com.listshop.bff.repositories

import com.listshop.bff.data.model.ListInfo
import com.listshop.bff.data.model.UserInfo
import com.listshop.bff.db.ListInfoEntity
import com.listshop.bff.db.UserInfoEntity


interface SessionInfoRepository {

    fun getUserInfo(): UserInfoEntity?

    fun createUserInfo(): UserInfoEntity?

    fun updateUserInfo(userInfo: UserInfo)

    fun getListInfo(): ListInfoEntity?

    fun createListInfo(): ListInfoEntity?

    fun updateListInfo(listInfo: ListInfo)
}
