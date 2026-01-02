package com.listshop.bff.repositories.impl

import com.listshop.bff.data.model.Tag
import com.listshop.bff.data.remote.ApiTag
import com.listshop.bff.db.ListshopDb
import com.listshop.bff.db.TagEntity
import com.listshop.bff.repositories.LayoutRepository
import com.listshop.bff.repositories.ListShopDatabase

class LayoutRepositoryImpl(
    private val listShopDatabase: ListShopDatabase
) : LayoutRepository {
    private val dbRef: ListshopDb = listShopDatabase.db

    fun selectAllTags(): List<Tag> {
        listShopDatabase.analytics.fetchingTagsFromNetwork()
        val result: List<TagEntity> = dbRef.tagDefinitionQueries
            .selectAllTagLookups(::mapTagLookupSelecting).executeAsList()
        return result.map { tle -> Tag.Factory.create(tle) }
    }


    suspend fun insertTags(tags: List<Tag>) {
        listShopDatabase.analytics.insertingTagsToDatabase(tags.size)
        dbRef.tagDefinitionQueries.transaction {
            tags.forEach { tag ->
                dbRef.tagDefinitionQueries.insertIntoTagLookup(
                    tag.externalId,
                    false, tag.name, tag.parentId, "0", tag.tagType, "0"
                )
            }
        }
    }

    suspend fun insertApiTagsLocally(tags: List<ApiTag>) {
        listShopDatabase.analytics.insertingTagsToDatabase(tags.size)
        dbRef.tagDefinitionQueries.transaction {
            tags.forEach { tag ->
                dbRef.tagDefinitionQueries.insertIntoTagLookup(
                    tag.externalId,
                    false, tag.name, tag.parentId, "0", tag.tagType, tag.userId
                )
            }
        }
    }


    suspend fun deleteAll() {
        listShopDatabase.analytics.databaseCleared()
        dbRef.tagDefinitionQueries.transaction {
            dbRef.tagDefinitionQueries.removeAllTagLookups()
        }
    }

    private fun mapTagLookupSelecting(
        externalId: String?,
        isGroup: Boolean?,
        name: String?,
        parentId: String?,
        power: String?,
        tagType: String?,
        user_id: String?,
    ): TagEntity {
        return TagEntity(
            externalId = externalId,
            isGroup = isGroup == true,
            name = name,
            parentId = parentId,
            power = power,
            tagType = tagType,
            userId = user_id
        )
    }
}
