package com.listshop.bff.data.state


sealed class NewResult<out T : Any> {

    class Success<T : Any>(val data: T) : NewResult<T>()
    class Failure<BFFError : Any>( val data: BFFError) : NewResult<BFFError>()

    public companion object {
        public  fun <T> success(value: T): NewResult<Any> {
            return NewResult.success(value)
        }

        public  fun <BFFError : Any> failure(value: BFFError): NewResult<Any> {
            return NewResult.failure(value)
        }



    }
}