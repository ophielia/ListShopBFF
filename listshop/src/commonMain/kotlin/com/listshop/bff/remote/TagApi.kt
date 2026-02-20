package com.listshop.bff.remote

import com.listshop.bff.data.model.Tag
import com.listshop.bff.data.remote.ApiTag
import com.listshop.bff.data.remote.PostApiTag
import com.listshop.bff.data.remote.PostGenericPayload
import com.listshop.bff.data.remote.PutApiTag

interface TagApi {

    suspend fun getAllTags(): List<Tag>

    suspend fun retrieveApiTags(): List<ApiTag>
    suspend fun createTag(payload: PostApiTag) : String
    suspend fun retrieveTag(newTagId: String) : ApiTag
    suspend fun updateTag(tagId: String,payload: PutApiTag)
}
