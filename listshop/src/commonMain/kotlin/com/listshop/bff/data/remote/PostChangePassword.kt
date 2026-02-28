package com.listshop.bff.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PostChangePassword (
    @SerialName("original_password")
    val originalPassword: String,
    @SerialName("new_password")
    val newPassword: String?,

    ) {

}
