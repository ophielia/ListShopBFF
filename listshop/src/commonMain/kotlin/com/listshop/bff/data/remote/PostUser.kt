package com.listshop.bff.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PostUser(
    @SerialName("user_name")
    val username: String?,
    val email: String?,
    val password: String?,

)




