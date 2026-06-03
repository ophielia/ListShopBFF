package com.listshop.bff.remote

import com.listshop.bff.data.remote.ApiLayout

interface LayoutApi {
    suspend fun retrieveDefaultLayout() : ApiLayout?

    suspend fun retrieveAllLayouts(): List<ApiLayout>?
}
