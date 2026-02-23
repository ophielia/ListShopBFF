package com.listshop.bff.repositories.impl

import com.listshop.bff.data.model.Tag
import com.listshop.bff.data.remote.ApiTag
import com.listshop.bff.db.ListshopDb
import com.listshop.bff.db.TagEntity
import com.listshop.bff.repositories.ListShopDatabase
import com.listshop.bff.repositories.TagRepository

class TagRepositoryImpl(
    private val listShopDatabase: ListShopDatabase
) : TagRepository {
    private val dbRef: ListshopDb = listShopDatabase.db

    fun selectAllTags(): List<Tag> {
        listShopDatabase.analytics.fetchingTagsFromNetwork()
        val result: List<TagEntity> = dbRef.tagDefinitionQueries
            .selectAllTags(::mapTagLookupSelecting).executeAsList()
        return result.map { tle -> Tag.Factory.create(tle) }
    }


    suspend fun insertTags(tags: List<Tag>) {
        listShopDatabase.analytics.insertingTagsToDatabase(tags.size)
        dbRef.tagDefinitionQueries.transaction {
            tags.forEach { tag ->
                dbRef.tagDefinitionQueries.insertIntoTag(
                    tag.externalId,
                    false, tag.name, tag.parentId, "0", tag.tagType, "0"
                )
            }
        }
    }

    override suspend fun insertApiTagsLocally(apiTags: List<ApiTag>) {
        listShopDatabase.analytics.insertingTagsToDatabase(apiTags.size)
        dbRef.tagDefinitionQueries.transaction {
            apiTags.forEach { tag ->
                dbRef.tagDefinitionQueries.insertIntoTag(
                    tag.externalId,
                    false, tag.name, tag.parentId, "0", tag.tagType, tag.userId
                )
            }
        }
    }


    override suspend fun findTagsByTypes(typesForTreeAsStrings: List<String>): List<TagEntity> {
        return dbRef.tagDefinitionQueries
            .selectTagsByTagTypes(typesForTreeAsStrings)
            .executeAsList()
    }

    override suspend fun retrieveTagLocally(tagId: String): TagEntity {
        return dbRef.tagDefinitionQueries.selectTagByTagId(tagId)
            .executeAsOne()
    }

    override suspend fun updateApiTagLocally(tagId: String, tagName: String) {
        dbRef.tagDefinitionQueries.updateTagName( tagName, tagId)
    }

    override suspend fun searchTags(
        fragment: String,
        tagTypes: List<String>,
        excludeGroups: Boolean
    ): List<TagEntity> {
        val wildcardFragment = "%$fragment%"
        return dbRef.tagDefinitionQueries.searchTags(wildcardFragment, tagTypes, excludeGroups)
            .executeAsList()
    }

    override suspend fun deleteAll() {
        listShopDatabase.analytics.databaseCleared()
        dbRef.tagDefinitionQueries.transaction {
            dbRef.tagDefinitionQueries.removeAllTags()
        }
    }

    private fun mapTagLookupSelecting(
        externalId: String?,
        isGroup: Boolean?,
        name: String?,
        parentId: String?,
        power: String?,
        tagType: String?,
        userId: String?,
    ): TagEntity {
        return TagEntity(
            externalId = externalId,
            isGroup = isGroup == true,
            name = name,
            parentId = parentId,
            power = power,
            tagType = tagType,
            userId = userId
        )
    }
}
