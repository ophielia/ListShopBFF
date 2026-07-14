package com.listshop.bff.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PostShoppingList(
    @SerialName("list_name")
    val name: String?
)

