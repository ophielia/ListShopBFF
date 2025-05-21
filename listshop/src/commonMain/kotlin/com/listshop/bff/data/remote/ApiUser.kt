package com.listshop.bff.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ApiUser(
    @SerialName("email")
    val email: String?,
    @SerialName("password")
    val password: String?,
    @SerialName("roles")
    val roles: List<String>?,
    @SerialName("token")
    val token: String?,
    @SerialName("creation_date")
    val creationDate: String?,
    @SerialName("user_name")
    val userName: String?,
    )

@Serializable
data class ApiWrappedUser(
    @SerialName("user")
    val user: ApiUser
)
