package com.listshop.bff.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PostApiTag(
    @SerialName("name")
    val name: String?,
    @SerialName("tag_type")
    val tagType: String? = null,
    @SerialName("parent_id")
    val parentId: String? = null,
)

