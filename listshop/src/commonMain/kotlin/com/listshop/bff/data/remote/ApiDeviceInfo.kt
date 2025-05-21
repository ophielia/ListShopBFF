package com.listshop.bff.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ApiDeviceInfo(
    val name: String?,
    val model: String?,
    val os: String?,
    @SerialName("os_version")
    val osVersion: String?,
    @SerialName("client_type")
    val clientType: String?,
    @SerialName("client_version")
    val clientVersion: String?,
    @SerialName("build_number")
    val buildNumber: String?,
    @SerialName("device_id")
    val deviceId: String?
)




