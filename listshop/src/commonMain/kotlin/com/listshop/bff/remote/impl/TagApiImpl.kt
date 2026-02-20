package com.listshop.bff.remote.impl

import com.listshop.bff.data.model.Tag
import com.listshop.bff.data.remote.ApiTag
import com.listshop.bff.data.remote.ApiTagLookupEmbedded
import com.listshop.bff.data.remote.ApiTagLookupEmbeddedTag
import com.listshop.bff.data.remote.ApiWrappedUser
import com.listshop.bff.data.remote.PostApiTag
import com.listshop.bff.data.remote.PutApiTag
import com.listshop.bff.exceptions.ApiException
import com.listshop.bff.remote.ListShopRemoteApi
import com.listshop.bff.remote.TagApi
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

internal class TagApiImpl(
    val remoteApi: ListShopRemoteApi
) : TagApi {

    override suspend fun getAllTags(): List<Tag> {
        val token = remoteApi.token()
        val urlString = remoteApi.buildPath("/tag/user")
        val response = remoteApi.client(token).get(urlString)

        remoteApi.mapNonSuccessToException(response.status.value,
            ApiException("get api tags call failed with status: " + response.status.value)
        )

        val result: ApiTagLookupEmbedded = response.body()
        return result.embeddedList.tagLookupResourceList
            .map { et -> et.embeddedTag }
            .map { at -> Tag.create(at) }
    }

    override suspend fun retrieveApiTags(): List<ApiTag> {
        val token = remoteApi.token()
        val urlString = remoteApi.buildPath("/tag/user")
        val response = remoteApi.client(token).get(urlString)

        remoteApi.mapNonSuccessToException(response.status.value,
            ApiException("get api tags call failed with status: " + response.status.value)
        )

        val result: ApiTagLookupEmbedded = response.body()
        return result.embeddedList.tagLookupResourceList
            .map { et -> et.embeddedTag }
    }

    override suspend fun createTag(payload: PostApiTag) : String {
        val urlPath ="/tag/${payload.parentId}/child"
        val urlString = remoteApi.buildPath(urlPath)
        val postPayload = Json.encodeToString(payload)
        val response = remoteApi.postRequest(urlString, postPayload)

        remoteApi.mapNonSuccessToException(response.status.value,ApiException("create tag call failed with status: " + response.status.value))

        val location = remoteApi.pullLocation(response)
        val elements = location.split("/")
        return elements.last()
    }

    override suspend fun updateTag(tagId: String,payload: PutApiTag) {
        val urlPath ="/tag/${tagId}"
        val urlString = remoteApi.buildPath(urlPath)
        val putPayload = Json.encodeToString(payload)
        val response = remoteApi.putRequest(urlString, putPayload)

        remoteApi.mapNonSuccessToException(response.status.value,ApiException("update tag call failed with status: " + response.status.value))
    }

override suspend fun retrieveTag(newTagId: String) : ApiTag {
    val token = remoteApi.token()
    val urlString = remoteApi.buildPath("/tag/${newTagId}")
    val response = remoteApi.client(token).get(urlString)

    remoteApi.mapNonSuccessToException(response.status.value,
        ApiException("get api tag call failed with status: " + response.status.value)
    )

    val result: ApiTagLookupEmbeddedTag = response.body()
    return result.embeddedTag
}
}
