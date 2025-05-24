package com.listshop.bff.data.bff

import com.listshop.bff.exceptions.HttpClientException

data class BFFError(
    var type: BFFErrorType,
    var subType: BFFErrorSubtype,
    var message: String
) {


    companion object {
        fun <T> errorFromException(exception: Exception): BFFResult<T> {
            if (exception is HttpClientException) {
                // client / server error
                val bfferror = BFFError(
                    BFFErrorType.API,
                    BFFErrorSubtype.CANT_CONNECT,
                    exception.message ?: "no info"
                )
                return BFFResult.error<T>(bfferror)
            }
            val bfferror = BFFError(
                BFFErrorType.UNKNOWN,
                BFFErrorSubtype.UNKNOWN,
                exception.message ?: "no info"
            )
            return BFFResult.error<T>(bfferror)
        }
    }

}

