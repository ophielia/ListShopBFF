package com.listshop.bff.db

import app.cash.sqldelight.Query
import app.cash.sqldelight.TransacterImpl
import app.cash.sqldelight.db.SqlDriver
import kotlin.Any
import kotlin.String

public class UserSessionDefinitionQueries(
  driver: SqlDriver,
) : TransacterImpl(driver) {
  public fun <T : Any> selectAllUserInfos(mapper: (
    userName: String?,
    userToken: String?,
    userCreated: String?,
    userLastSeen: String?,
    userLastSignedIn: String?,
  ) -> T): Query<T> = Query(903_784_958, arrayOf("UserInfoEntity"), driver,
      "UserSessionDefinition.sq", "selectAllUserInfos",
      "SELECT UserInfoEntity.userName, UserInfoEntity.userToken, UserInfoEntity.userCreated, UserInfoEntity.userLastSeen, UserInfoEntity.userLastSignedIn FROM UserInfoEntity") {
      cursor ->
    mapper(
      cursor.getString(0),
      cursor.getString(1),
      cursor.getString(2),
      cursor.getString(3),
      cursor.getString(4)
    )
  }

  public fun selectAllUserInfos(): Query<UserInfoEntity> = selectAllUserInfos { userName, userToken,
      userCreated, userLastSeen, userLastSignedIn ->
    UserInfoEntity(
      userName,
      userToken,
      userCreated,
      userLastSeen,
      userLastSignedIn
    )
  }

  public fun insertIntoUserInfo(
    userName: String?,
    userToken: String?,
    userCreated: String?,
    userLastSeen: String?,
    userLastSignedIn: String?,
  ) {
    driver.execute(-2_115_244_549, """
        |INSERT INTO UserInfoEntity(userName, userToken, userCreated, userLastSeen, userLastSignedIn )
        |VALUES (?, ?,?,?,?)
        """.trimMargin(), 5) {
          bindString(0, userName)
          bindString(1, userToken)
          bindString(2, userCreated)
          bindString(3, userLastSeen)
          bindString(4, userLastSignedIn)
        }
    notifyQueries(-2_115_244_549) { emit ->
      emit("UserInfoEntity")
    }
  }

  public fun updateUserInfo(
    userName: String?,
    userToken: String?,
    userCreated: String?,
    userLastSeen: String?,
    userLastSignedIn: String?,
  ) {
    driver.execute(1_517_185_675, """
        |UPDATE UserInfoEntity
        |  SET userName = ?,
        |      userToken = ?,
        |      userCreated = ?,
        |      userLastSeen = ?,
        |      userLastSignedIn = ?
        """.trimMargin(), 5) {
          bindString(0, userName)
          bindString(1, userToken)
          bindString(2, userCreated)
          bindString(3, userLastSeen)
          bindString(4, userLastSignedIn)
        }
    notifyQueries(1_517_185_675) { emit ->
      emit("UserInfoEntity")
    }
  }

  public fun removeAllUserInfo() {
    driver.execute(-329_469_811, """DELETE FROM UserInfoEntity""", 0)
    notifyQueries(-329_469_811) { emit ->
      emit("UserInfoEntity")
    }
  }
}
