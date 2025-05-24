package com.listshop.bff.db

import kotlin.Boolean
import kotlin.String

public data class TagLookupEntity(
  public val externalId: String?,
  public val isGroup: Boolean,
  public val name: String?,
  public val parentId: String?,
  public val power: String?,
  public val tagType: String?,
  public val userId: String?,
)
