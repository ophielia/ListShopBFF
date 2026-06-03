package com.listshop.bff.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class
ApiLayout(
    @SerialName("name")
    val name: String?,
    @SerialName("layout_id")
    val externalId: Int?,
    @SerialName("is_default")
    val isDefault: Boolean = false,
    @SerialName("user_id")
    val userId: String?,
    @SerialName("categories")
    val categories: List<ApiLayoutCategory> = listOf()
)  {
    companion object {
        fun empty(): ApiLayout = ApiLayout(
            name = null,
            externalId = null,
            isDefault = false,
            userId = null,
            categories = listOf()
        )
    }
}

@Serializable
data class ApiLayoutCategory(
    @SerialName("name")
    val name: String?,
    @SerialName("category_id")
    val externalId: Int,
    @SerialName("display_order")
    val displayOrder: Long = 0,
    @SerialName("is_default")
    val isDefault: Boolean = false,
    @SerialName("tags")
    val tags: List<ApiLayoutTag> = listOf()

)

@Serializable
data class ApiLayoutTag(
    @SerialName("tag_id")
    val externalId: String?,
    @SerialName("name")
    val name: String?
)

@Serializable
data class EmbeddedApiLayoutList(
    @SerialName("_embedded")
    val embedded: ApiLayoutList?
)

@Serializable
data class ApiLayoutList(
    @SerialName("list_layouts")
    val layoutList: List<ApiLayout>?
)

@Serializable
data class EmbeddedApiLayout(
    @SerialName("list_layout")
    val embeddedLayout: ApiLayout?
)



