package com.listshop.bff.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PostUserSignup(
    @SerialName("user")
    val user: PostUser,
    @SerialName("device_info")
    val deviceInfo: ApiDeviceInfo,
    @SerialName("create_list")
    val createList: Boolean = true
)




