package com.listshop.bff.services

import com.listshop.bff.data.remote.ApiTag

interface TagService {
    suspend fun retrieveTagsAndSaveLocally(): List<ApiTag>


}
