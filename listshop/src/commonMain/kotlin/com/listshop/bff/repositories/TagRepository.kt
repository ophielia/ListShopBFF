package com.listshop.bff.repositories

import com.listshop.bff.data.model.ShoppingListTag
import com.listshop.bff.data.remote.ApiTag
import com.listshop.bff.db.TagEntity

interface TagRepository {
    suspend fun deleteAll()
    suspend fun insertApiTagsLocally(apiTags: List<ApiTag>)
    suspend fun findTagsByTypes(typesForTreeAsStrings: List<String>) : List<TagEntity>
    suspend fun retrieveTagLocally(tagId: String) : TagEntity
    suspend fun updateApiTagLocally(tagId: String, tagName: String)
    suspend fun searchTags(fragment: String, tagTypes: List<String>, excludeGroups: Boolean) : List<TagEntity>
}
