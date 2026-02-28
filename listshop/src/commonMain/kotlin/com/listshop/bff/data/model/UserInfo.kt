package com.listshop.bff.data.model

import com.listshop.bff.db.UserInfoEntity

data class UserInfo(
    var userName: String?,
    var userToken: String?,
    var userInfoCreated: String?,
    var userLastSeen: String?,
    var userLastSignedIn: String?,
    var userCreatedOnServer: String?

) {
    companion object Factory {
        fun create(dbValue: UserInfoEntity): UserInfo {
            return UserInfo(
                dbValue.userName,
                dbValue.userToken,
                dbValue.userInfoCreated,
                dbValue.userLastSeen,
                dbValue.userLastSignedIn,
                dbValue.userCreatedOnServer
            )
        }

    }

}

