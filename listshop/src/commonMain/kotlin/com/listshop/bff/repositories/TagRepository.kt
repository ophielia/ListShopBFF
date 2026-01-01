package com.listshop.bff.repositories

import com.listshop.bff.data.remote.ApiTag

interface TagRepository {
    suspend fun deleteAll()
    suspend fun insertApiTagsLocally(apiTags: List<ApiTag>)
}
