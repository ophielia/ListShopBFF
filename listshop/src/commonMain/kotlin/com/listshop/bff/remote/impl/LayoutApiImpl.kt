package com.listshop.bff.remote.impl

import com.listshop.bff.data.remote.ApiLayout
import com.listshop.bff.data.remote.EmbeddedApiLayout
import com.listshop.bff.data.remote.EmbeddedApiLayoutList
import com.listshop.bff.remote.LayoutApi
import com.listshop.bff.remote.ListShopRemoteApi
import io.ktor.client.call.*
import io.ktor.client.request.*

internal class LayoutApiImpl(
    val remoteApi: ListShopRemoteApi
) : LayoutApi {

    override suspend fun retrieveDefaultLayout(): ApiLayout? {
        val urlString = remoteApi.buildPath("/layout/default")
        val result: EmbeddedApiLayout =
            remoteApi.getRequest(urlString).body()

        return result.embeddedLayout
    }

    override suspend fun retrieveUserLayouts(): List<ApiLayout>? {
        val token = remoteApi.token()
        if (token != null) {
            val urlString = remoteApi.buildPath("/layout/user")
            val result: EmbeddedApiLayoutList =
                remoteApi.client(token).get(urlString).body()

            return result.embedded?.layoutList?.map { emb -> emb.embeddedLayout as ApiLayout }

        }
        return emptyList()
    }


}
