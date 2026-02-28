package com.listshop.bff.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PostShoppingList(
    @SerialName("name")
    val name: String?,
    @SerialName("is_starter_list")
    val isStarterList: Boolean? = false,
)

