package com.listshop.bff.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PostTokenRequest(
    @SerialName("token_parameter")
    val tokenParameter: String?,
    @SerialName("token_type")
    val tokenType: String? = null
)
