package com.listshop.bff.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class
ApiProperty(
    @SerialName("key")
    val key: String?,
    @SerialName("value")
    val value: String?
)  {
}

@Serializable
data class ApiUserProperties(
    @SerialName("user_properties")
    val userProperties: List<ApiProperty>,

)

