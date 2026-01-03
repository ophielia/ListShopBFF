package com.listshop.bff.repositories

import com.listshop.bff.data.remote.ApiTag
import com.listshop.bff.db.TagEntity

interface TagRepository {
    suspend fun deleteAll()
    suspend fun insertApiTagsLocally(apiTags: List<ApiTag>)
    suspend fun findTagsByTypes(typesForTreeAsStrings: List<String>) : List<TagEntity>
}
