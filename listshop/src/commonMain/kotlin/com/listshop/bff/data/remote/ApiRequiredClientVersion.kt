package com.listshop.bff.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ApiRequiredClientVersion(
    @SerialName("ios_min_version")
    val iosMinVersion: String?,
    @SerialName("android_min_version")
    val androidMinVersion: String?,
)
