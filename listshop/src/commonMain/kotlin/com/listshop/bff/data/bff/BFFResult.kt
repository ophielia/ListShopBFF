package com.listshop.bff.data.bff

sealed interface BFFResult<out T> {
    data class Success<T>(val value: T) : BFFResult<T>
    data class Failure(val error: BFFError) : BFFResult<Nothing>
}
