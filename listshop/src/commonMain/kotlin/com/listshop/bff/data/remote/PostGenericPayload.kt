package com.listshop.bff.data.remote

import kotlinx.serialization.Serializable

@Serializable
data class PostGenericPayload (
    val parameters: Array<String>?
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as PostGenericPayload

        if (!parameters.contentEquals(other.parameters)) return false

        return true
    }

    override fun hashCode(): Int {
        return parameters?.contentHashCode() ?: 0
    }
}
