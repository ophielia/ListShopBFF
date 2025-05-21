package com.listshop.bff.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PostUserLogin(
    val username: String?,
    val password: String?,
    @SerialName("device_info")
    val deviceInfo: ApiDeviceInfo
)




