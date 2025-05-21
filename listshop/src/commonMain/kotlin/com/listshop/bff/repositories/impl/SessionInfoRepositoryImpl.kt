package com.listshop.bff.repositories.impl

import com.listshop.bff.data.model.UserInfo
import com.listshop.bff.db.UserInfoEntity
import com.listshop.bff.repositories.ListShopDatabase
import com.listshop.bff.repositories.SessionInfoRepository
import kotlinx.datetime.Clock


internal class SessionInfoRepositoryImpl(
    private val listShopDatabase: ListShopDatabase
) : SessionInfoRepository {

    override fun getUserInfo(): UserInfoEntity? {
        val userInfoList = listShopDatabase.db.userSessionDefinitionQueries.selectAllUserInfos()
            .executeAsList()
        if (userInfoList.size > 0 ) {
            return userInfoList.get(0)
        }
        return null
    }

    override fun createUserInfo(): UserInfoEntity? {
        val now = Clock.System.now()

        listShopDatabase.db.userSessionDefinitionQueries
            .insertIntoUserInfo(null,
                null,
                now.toString(),
                now.toString(),
                null
            )
        return getUserInfo()
    }

    override fun updateUserInfo(userInfo: UserInfo) {
        listShopDatabase.db.userSessionDefinitionQueries
            .updateUserInfo(
                userInfo.userName,
                userInfo.userToken,
                userInfo.userCreated,
                userInfo.userLastSeen,
                userInfo.userLastSignedIn
            )
    }

}
