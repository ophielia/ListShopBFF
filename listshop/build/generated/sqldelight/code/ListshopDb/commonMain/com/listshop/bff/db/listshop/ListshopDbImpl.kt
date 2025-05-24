package com.listshop.bff.db.listshop

import app.cash.sqldelight.TransacterImpl
import app.cash.sqldelight.db.AfterVersion
import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlSchema
import com.listshop.bff.db.ListshopDb
import com.listshop.bff.db.TagDefinitionQueries
import com.listshop.bff.db.UserSessionDefinitionQueries
import kotlin.Long
import kotlin.Unit
import kotlin.reflect.KClass

internal val KClass<ListshopDb>.schema: SqlSchema<QueryResult.Value<Unit>>
  get() = ListshopDbImpl.Schema

internal fun KClass<ListshopDb>.newInstance(driver: SqlDriver): ListshopDb = ListshopDbImpl(driver)

private class ListshopDbImpl(
  driver: SqlDriver,
) : TransacterImpl(driver), ListshopDb {
  override val tagDefinitionQueries: TagDefinitionQueries = TagDefinitionQueries(driver)

  override val userSessionDefinitionQueries: UserSessionDefinitionQueries =
      UserSessionDefinitionQueries(driver)

  public object Schema : SqlSchema<QueryResult.Value<Unit>> {
    override val version: Long
      get() = 1

    override fun create(driver: SqlDriver): QueryResult.Value<Unit> {
      driver.execute(null, """
          |CREATE TABLE TagLookupEntity (
          |    externalId TEXT,
          |    isGroup INTEGER NOT NULL,
          |    name TEXT,
          |    parentId TEXT,
          |    power TEXT,
          |    tagType TEXT,
          |    userId TEXT
          |
          |)
          """.trimMargin(), 0)
      driver.execute(null, """
          |CREATE TABLE UserInfoEntity (
          |    userName TEXT,
          |    userToken TEXT,
          |    userCreated TEXT,
          |    userLastSeen TEXT,
          |    userLastSignedIn TEXT
          |)
          """.trimMargin(), 0)
      return QueryResult.Unit
    }

    override fun migrate(
      driver: SqlDriver,
      oldVersion: Long,
      newVersion: Long,
      vararg callbacks: AfterVersion,
    ): QueryResult.Value<Unit> = QueryResult.Unit
  }
}
