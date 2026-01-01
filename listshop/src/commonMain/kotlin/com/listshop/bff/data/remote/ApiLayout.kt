package com.listshop.bff.data.remote

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EmbeddedApiLayout(
    @SerialName("list_layout")
    val embeddedLayout: ApiLayout?
)
@Serializable
data class ApiLayout(
    @SerialName("name")
    val name: String?,
    @SerialName("layout_id")
    val externalId: Int?,
    @SerialName("is_default")
    val isDefault: Boolean = false,
    @SerialName("user_id")
    val userId: Int?,
    @SerialName("categories")
    val categories: List<ApiLayoutCategory>?,
)

@Serializable
data class ApiLayoutCategory(
    @SerialName("name")
    val name: String?,
    @SerialName("category_id")
    val externalId: Int?,
    @SerialName("display_order")
    val displayOrder: Int = 0,
    @SerialName("is_default")
    val isDefault: Boolean = false,
    @SerialName("tags")
    val tags: List<ApiLayoutTag>?

)



@Serializable
data class ApiLayoutTag(
    @SerialName("tag_id")
    val externalId: String?,
    @SerialName("name")
    val name: String?
)
