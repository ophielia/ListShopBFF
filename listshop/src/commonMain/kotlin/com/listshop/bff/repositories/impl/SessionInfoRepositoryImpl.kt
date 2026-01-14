package com.listshop.bff.repositories.impl

import com.listshop.bff.data.model.ListInfo
import com.listshop.bff.data.model.UserInfo
import com.listshop.bff.db.ListInfoEntity
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
                null,
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

    override fun updateListInfo(listInfo: ListInfo) {
        listShopDatabase.db.userSessionDefinitionQueries
            .updateListInfo(
                lastInternalUpdate = listInfo.lastInternalUpdate ,
                lastUpdate = listInfo.lastUpdate ,
                localListUpdated = listInfo.localListUpdated ,
                serverListId = listInfo.serverListId ,
                lookupDataLastSynced = listInfo.lookupDataLastSynced ,
                statisticsLastSynced = listInfo.statisticsLastSynced ,
                localLastSynced = listInfo.localLastSynced ,
                serverListLastSynced = listInfo.serverListLastSynced
            )
    }

    override fun getListInfo(): ListInfoEntity? {
        val listInfooList = listShopDatabase.db.userSessionDefinitionQueries.selectAllListInfos()
            .executeAsList()
        if (listInfooList.size > 0 ) {
            return listInfooList.get(0)
        }
        return null
    }

    override fun createListInfo(): ListInfoEntity? {
        val now = Clock.System.now().toString()

        listShopDatabase.db.userSessionDefinitionQueries
            .insertIntoListInfo(
                lastInternalUpdate = now,
                lastUpdate = now,
                localListUpdated = null,
                serverListId = null,
                lookupDataLastSynced = null,
                statisticsLastSynced = null,
                localLastSynced = null,
                serverListLastSynced = null
            )
        return getListInfo()
    }

}
