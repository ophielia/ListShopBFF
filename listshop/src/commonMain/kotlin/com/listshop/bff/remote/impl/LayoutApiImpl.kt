package com.listshop.bff.remote.impl

import com.listshop.bff.data.remote.ApiLayout
import com.listshop.bff.data.remote.ApiLayoutCategory
import com.listshop.bff.remote.LayoutApi
import com.listshop.bff.remote.ListShopRemoteApi
import io.ktor.client.call.*
import io.ktor.client.request.*

internal class LayoutApiImpl(
    val remoteApi: ListShopRemoteApi
) : LayoutApi {


    override suspend fun retrieveAllLayouts(): List<ApiLayout>? {
        val token = remoteApi.token()

        // currently default layout is the only layout available
        val urlString = remoteApi.buildPath("/v2/layout/default")
        val result: ApiLayout =
            remoteApi.client(token).get(urlString).body()

        return listOf(result)
    }


    override suspend fun retrieveLayoutForTag(tagId: String): ApiLayoutCategory {
        val token = remoteApi.token()

        // currently default layout is the only layout available
        val urlString = remoteApi.buildPath("/v2/layout/tag/${tagId}")
        val result: ApiLayoutCategory =
            remoteApi.client(token).get(urlString).body()

        return result
    }


}
