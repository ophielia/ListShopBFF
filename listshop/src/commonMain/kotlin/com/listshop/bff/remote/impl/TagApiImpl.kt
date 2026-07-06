package com.listshop.bff.remote.impl

import com.listshop.bff.data.model.Tag
import com.listshop.bff.data.remote.ApiTag
import com.listshop.bff.data.remote.ApiTagList
import com.listshop.bff.data.remote.PostApiTag
import com.listshop.bff.data.remote.PutApiTag
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
        val urlString = remoteApi.buildPath("/v2/tag")
        val response = remoteApi.client(token).get(urlString)

        remoteApi.mapNonSuccessToException(response.status.value,
            "get api tags call failed with status: " + response.status.value
        )

        val result: ApiTagList = response.body()
        return result.tagList
            .map { at -> Tag.create(at) }
    }

    override suspend fun retrieveApiTags(): List<ApiTag> {
        val token = remoteApi.token()
        val urlString = remoteApi.buildPath("/v2/tag")
        val response = remoteApi.client(token).get(urlString)

        remoteApi.mapNonSuccessToException(response.status.value,
            "get api tags call failed with status: " + response.status.value
        )

        val result: ApiTagList = response.body()
        return result.tagList
    }

    override suspend fun createTag(payload: PostApiTag) : String {
        val urlPath ="/v2/tag/${payload.parentId}/child"
        val urlString = remoteApi.buildPath(urlPath)
        val postPayload = Json.encodeToString(payload)
        val response = remoteApi.postRequest(urlString, postPayload)

        remoteApi.mapNonSuccessToException(response.status.value,"create tag call failed with status: " + response.status.value)

        val location = remoteApi.pullLocation(response)
        val elements = location.split("/")
        return elements.last()
    }

    override suspend fun updateTag(tagId: String,payload: PutApiTag) {
        val urlPath ="/v2/tag/${tagId}"
        val urlString = remoteApi.buildPath(urlPath)
        val putPayload = Json.encodeToString(payload)
        val response = remoteApi.putRequest(urlString, putPayload)

        remoteApi.mapNonSuccessToException(response.status.value,"update tag call failed with status: " + response.status.value)
    }

override suspend fun retrieveTag(newTagId: String) : ApiTag {
    val token = remoteApi.token()
    val urlString = remoteApi.buildPath("/v2/tag/${newTagId}")
    val response = remoteApi.client(token).get(urlString)

    remoteApi.mapNonSuccessToException(response.status.value,
        "get api tag call failed with status: " + response.status.value
    )

    val result: ApiTag = response.body()
    return result
}
}
