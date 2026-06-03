package com.listshop.bff.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ApiTag(
    @SerialName("tag_id")
    val externalId: String?,
    @SerialName("name")
    val name: String?,
    @SerialName("tag_type")
    val tagType: String? = null,
    @SerialName("parent_id")
    var parentId: String? = null,
    @SerialName("user_id")
    val userId: String? = null,
    @SerialName("is_group")
    val isgroup: Boolean? = null,
    @SerialName("power")
    val power: Double? = null
)



@Serializable
data class ApiTagList(
    @SerialName("tag_list")
    val tagList: List<ApiTag>
)
