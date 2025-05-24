package com.listshop.bff

import com.listshop.bff.db.UserInfoEntity
import com.listshop.bff.repositories.ListShopDatabase
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime

class TestDatabaseHelper(
    private val listShopDatabase: ListShopDatabase
) {

    fun clearDatabase() {
        listShopDatabase.db.userSessionDefinitionQueries.removeAllUserInfo()
        listShopDatabase.db.tagDefinitionQueries.removeAllTagLookups()
    }

    fun standardUser():UserInfoEntity {
        val now = Clock.System.now()
        val created = LocalDateTime.parse("2017-02-15T18:00:00.000")
        val lastSeen = LocalDateTime.parse("2024-01-01T18:00:00.000")
        val lastSignedIn = LocalDateTime.parse("2024-07-01T18:00:00.000")

        return UserInfoEntity("user@the-list-shop.com",
            "a randomized token",
                    created.toString(),
            lastSeen.toString(),
            lastSignedIn.toString()
            )
    }

    fun setUser(user: UserInfoEntity?) {
        if (user == null) {
            return
        }
        listShopDatabase.db.userSessionDefinitionQueries.insertIntoUserInfo(
            user.userName,user.userToken, user.userCreated, user.userLastSeen,user.userLastSignedIn
        )
    }
}