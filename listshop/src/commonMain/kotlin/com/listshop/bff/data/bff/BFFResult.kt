package com.listshop.bff.data.bff

class BFFResult<T> internal constructor(
    val value: T?
) {

    constructor(error: BFFError?) : this(value = null) {
        _error = error
    }

    var _error: BFFError? = null

    val isSuccess: Boolean get() = _error == null

    val isFailure: Boolean get() = _error != null

    companion object {
        fun <T> success(value: T): BFFResult<T> {
            return BFFResult(value)
        }

        fun <T> error(error: BFFError): BFFResult<T> {
            return BFFResult(error = error)
        }
    }

}