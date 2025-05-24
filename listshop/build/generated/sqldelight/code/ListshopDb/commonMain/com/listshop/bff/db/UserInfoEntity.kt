package com.listshop.bff.db

import kotlin.String

public data class UserInfoEntity(
  public val userName: String?,
  public val userToken: String?,
  public val userCreated: String?,
  public val userLastSeen: String?,
  public val userLastSignedIn: String?,
)
