package com.listshop.bff.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PostListProperties(
    @SerialName("list_id")
    val listId: String?,

    @SerialName("name")
    val name: String?,

    @SerialName("is_starter_list")
    val isStarterList: Boolean?
)



