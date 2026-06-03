package com.listshop.bff.remote.impl

import com.listshop.bff.data.remote.ApiLayout
import com.listshop.bff.data.remote.ApiLayoutList
import com.listshop.bff.data.remote.EmbeddedApiLayout
import com.listshop.bff.remote.LayoutApi
import com.listshop.bff.remote.ListShopRemoteApi
import io.ktor.client.call.*
import io.ktor.client.request.*

internal class LayoutApiImpl(
    val remoteApi: ListShopRemoteApi
) : LayoutApi {

    override suspend fun retrieveDefaultLayout(): ApiLayout? {
        val urlString = remoteApi.buildPath("/v2/layout/default")
        val result: EmbeddedApiLayout =
            remoteApi.getRequest(urlString).body()

        return result.embeddedLayout
    }

    override suspend fun retrieveAllLayouts(): List<ApiLayout>? {
        val token = remoteApi.token()

        val urlString = remoteApi.buildPath("/v2/layout")
        val result: ApiLayoutList =
            remoteApi.client(token).get(urlString).body()

        return result.layoutList
    }


}
