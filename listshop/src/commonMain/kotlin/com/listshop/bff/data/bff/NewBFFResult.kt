package com.listshop.bff.data.bff

sealed interface NewBFFResult<out T> {
    data class Success<T>(val value: T) : NewBFFResult<T>
    data class Failure(val error: BFFError) : NewBFFResult<Nothing>
}
