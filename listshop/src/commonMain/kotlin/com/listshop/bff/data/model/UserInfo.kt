package com.listshop.bff.data.model

import com.listshop.bff.db.UserInfoEntity

data class UserInfo(
    var userName: String?,
    var userToken: String?,
    var userCreated: String?,
    var userLastSeen: String?,
    var userLastSignedIn: String?

) {
    companion object Factory {
        fun create(dbValue: UserInfoEntity): UserInfo {
            return UserInfo(
                dbValue.userName,
                dbValue.userToken,
                dbValue.userCreated,
                dbValue.userLastSeen,
                dbValue.userLastSignedIn
            )
        }

    }

}

