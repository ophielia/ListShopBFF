package com.listshop.bff.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ApiDish(
    @SerialName("dish_id")
    val externalId: Long?,
    @SerialName("name")
    val name: String?
)

@Serializable
data class ApiDishEmbedded(
    @SerialName("dish")
    val embeddedDish: ApiDish
)

@Serializable
data class ApiDishResourceList(
    @SerialName("dishResourceList")
    val dishResourceList: List<ApiDishEmbedded>
)

@Serializable
data class EmbeddedDishResourceList(
    @SerialName("_embedded")
    val embeddedList: ApiDishResourceList
)
