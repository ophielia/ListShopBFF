package com.listshop.bff.remote

import com.listshop.bff.data.model.Tag
import com.listshop.bff.data.remote.ApiTag

interface TagApi {

    suspend fun getAllTags(): List<Tag>

    suspend fun retrieveApiTags(): List<ApiTag>
}
