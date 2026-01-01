package com.listshop.bff.remote.impl

import com.listshop.bff.data.model.Tag
import com.listshop.bff.data.remote.ApiTag
import com.listshop.bff.data.remote.ApiTagLookupEmbedded
import com.listshop.bff.remote.LayoutApi
import com.listshop.bff.remote.ListShopRemoteApi
import com.listshop.bff.remote.TagApi
import io.ktor.client.call.body
import io.ktor.client.request.get

internal class LayoutApiImpl(
    val remoteApi: ListShopRemoteApi
) : LayoutApi {



}
