package com.listshop.bff.db

import app.cash.sqldelight.Query
import app.cash.sqldelight.TransacterImpl
import app.cash.sqldelight.db.SqlDriver
import kotlin.Any
import kotlin.Boolean
import kotlin.String

public class TagDefinitionQueries(
  driver: SqlDriver,
) : TransacterImpl(driver) {
  public fun <T : Any> selectAllTagLookups(mapper: (
    externalId: String?,
    isGroup: Boolean,
    name: String?,
    parentId: String?,
    power: String?,
    tagType: String?,
    userId: String?,
  ) -> T): Query<T> = Query(353_107_404, arrayOf("TagLookupEntity"), driver, "TagDefinition.sq",
      "selectAllTagLookups", """
  |SELECT TagLookupEntity.externalId, TagLookupEntity.isGroup, TagLookupEntity.name, TagLookupEntity.parentId, TagLookupEntity.power, TagLookupEntity.tagType, TagLookupEntity.userId
  |FROM TagLookupEntity
  """.trimMargin()) { cursor ->
    mapper(
      cursor.getString(0),
      cursor.getBoolean(1)!!,
      cursor.getString(2),
      cursor.getString(3),
      cursor.getString(4),
      cursor.getString(5),
      cursor.getString(6)
    )
  }

  public fun selectAllTagLookups(): Query<TagLookupEntity> = selectAllTagLookups { externalId,
      isGroup, name, parentId, power, tagType, userId ->
    TagLookupEntity(
      externalId,
      isGroup,
      name,
      parentId,
      power,
      tagType,
      userId
    )
  }

  public fun insertIntoTagLookup(
    externalId: String?,
    isGroup: Boolean,
    name: String?,
    parentId: String?,
    power: String?,
    tagType: String?,
    userId: String?,
  ) {
    driver.execute(-1_103_819_613, """
        |INSERT INTO TagLookupEntity(externalId, isGroup, name, parentId, power, tagType, userId)
        |VALUES (?,?,?,?,?,?,?)
        """.trimMargin(), 7) {
          bindString(0, externalId)
          bindBoolean(1, isGroup)
          bindString(2, name)
          bindString(3, parentId)
          bindString(4, power)
          bindString(5, tagType)
          bindString(6, userId)
        }
    notifyQueries(-1_103_819_613) { emit ->
      emit("TagLookupEntity")
    }
  }

  public fun removeAllTagLookups() {
    driver.execute(-687_327_420, """DELETE FROM TagLookupEntity""", 0)
    notifyQueries(-687_327_420) { emit ->
      emit("TagLookupEntity")
    }
  }
}
