package com.listshop.bff.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ApiTagLookup(
    @SerialName("tag_id")
    val externalId: String?,
    @SerialName("name")
    val name: String?,
    @SerialName("tag_type")
    val tagType: String?,
    @SerialName("parent_id")
    val parentId: String?,
    @SerialName("user_id")
    val userId: String?,
    @SerialName("is_group")
    val isgroup: Boolean?,
    @SerialName("power")
    val power: Double?
)

@Serializable
data class ApiTagLookupEmbeddedTag(
    @SerialName("tag")
    val embeddedTag: ApiTagLookup
)

@Serializable
data class ApiTagLookupResourceList(
    @SerialName("tagResourceList")
    val tagLookupResourceList: List<ApiTagLookupEmbeddedTag>
)

@Serializable
data class ApiTagLookupEmbedded(
    @SerialName("_embedded")
    val embeddedList: ApiTagLookupResourceList
)
