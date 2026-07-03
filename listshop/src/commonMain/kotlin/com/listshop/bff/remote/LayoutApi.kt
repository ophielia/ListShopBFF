package com.listshop.bff.remote

import com.listshop.bff.data.remote.ApiLayout
import com.listshop.bff.data.remote.ApiLayoutCategory

interface LayoutApi {
    suspend fun retrieveAllLayouts(): List<ApiLayout>?
    suspend fun retrieveLayoutForTag(tagId: String): ApiLayoutCategory
}
