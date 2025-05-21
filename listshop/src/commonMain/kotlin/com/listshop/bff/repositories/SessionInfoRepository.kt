package com.listshop.bff.repositories

import com.listshop.bff.data.model.UserInfo
import com.listshop.bff.db.UserInfoEntity


interface SessionInfoRepository {

    fun getUserInfo(): UserInfoEntity?

    fun createUserInfo(): UserInfoEntity?

    fun updateUserInfo(userInfo: UserInfo)

}
