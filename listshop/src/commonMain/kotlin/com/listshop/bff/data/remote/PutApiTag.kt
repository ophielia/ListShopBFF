package com.listshop.bff.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PutApiTag(
    @SerialName("name")
    val name: String?,
)

