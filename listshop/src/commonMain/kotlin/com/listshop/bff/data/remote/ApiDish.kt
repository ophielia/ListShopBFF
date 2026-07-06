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
data class ApiDishList(
    @SerialName("dish_list")
    val dishes: List<ApiDish>
)
