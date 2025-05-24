package com.listshop.bff.db

import app.cash.sqldelight.Transacter
import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlSchema
import com.listshop.bff.db.listshop.newInstance
import com.listshop.bff.db.listshop.schema
import kotlin.Unit

public interface ListshopDb : Transacter {
  public val tagDefinitionQueries: TagDefinitionQueries

  public val userSessionDefinitionQueries: UserSessionDefinitionQueries

  public companion object {
    public val Schema: SqlSchema<QueryResult.Value<Unit>>
      get() = ListshopDb::class.schema

    public operator fun invoke(driver: SqlDriver): ListshopDb =
        ListshopDb::class.newInstance(driver)
  }
}
