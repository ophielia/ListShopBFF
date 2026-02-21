package com.listshop.bff.data.bff

import com.listshop.bff.exceptions.ApiException
import com.listshop.bff.exceptions.AuthenticationException
import com.listshop.bff.exceptions.BadParameterException
import com.listshop.bff.exceptions.BadRequestException
import com.listshop.bff.exceptions.HttpClientException
import com.listshop.bff.exceptions.InternalDataException
import com.listshop.bff.exceptions.LoginException
import com.listshop.bff.exceptions.LogoutException
import com.listshop.bff.exceptions.OfflineException
import com.listshop.bff.exceptions.ServerErrorException
import com.listshop.bff.exceptions.SignUpException
import com.listshop.bff.exceptions.UnknownApiException

data class BFFError(
    var type: BFFErrorType,
    var subType: BFFErrorSubtype,
    var message: String
) {

    companion object {

        fun <T> errorFromException(exception: Exception): BFFResult<T> {
            val bfferror =  when (exception) {
                is IllegalArgumentException ->  handleIllegalArgumentException(exception)
                is HttpClientException ->  handleHttpClientException(exception)
                is BadParameterException ->  handleBadParameterException(exception)
                is InternalDataException ->  handleInternalDataException(exception)
                is OfflineException ->  handleOfflineException(exception)
                is AuthenticationException ->  handleAuthenticationException(exception)
                is ApiException ->  handleApiException(exception)
                else ->  handleUnknownException(exception)
            }
            return BFFResult.error<T>(bfferror)
        }

        private fun handleUnknownException(exception: Exception): BFFError {
            return BFFError(BFFErrorType.UNKNOWN, BFFErrorSubtype.UNKNOWN, exception.message ?: "")
        }

        private fun handleApiException(exception: ApiException): BFFError {
            val subtype = when (exception) {
                is BadRequestException -> BFFErrorSubtype.BAD_REQUEST
                is ServerErrorException -> BFFErrorSubtype.SERVER_ERROR
                is UnknownApiException -> BFFErrorSubtype.UNKNOWN
                else -> BFFErrorSubtype.UNKNOWN
            }
            return BFFError(BFFErrorType.API, subtype, exception.message ?: "")
        }

        private fun handleIllegalArgumentException(exception: IllegalArgumentException): BFFError {
            return BFFError(BFFErrorType.VALIDATION, BFFErrorSubtype.INVALID_INPUT, exception.message ?: "")
        }

        private fun handleBadParameterException(exception: BadParameterException): BFFError {
            return BFFError(BFFErrorType.VALIDATION, BFFErrorSubtype.INVALID_INPUT, exception.message ?: "")
        }

        private fun handleHttpClientException(exception: HttpClientException): BFFError {
            return BFFError(BFFErrorType.NETWORK, BFFErrorSubtype.HTTP_CALL_ERROR, exception.message ?: "")
        }

        private fun handleInternalDataException(exception: InternalDataException): BFFError {
            return BFFError(BFFErrorType.DATABASE, BFFErrorSubtype.DATA_NOT_FOUND, exception.message ?: "")
        }

        private fun handleOfflineException(exception: OfflineException): BFFError {
            return BFFError(BFFErrorType.OFFLINE, BFFErrorSubtype.OFFLINE, exception.message ?: "")
        }

        private fun handleAuthenticationException(exception: AuthenticationException): BFFError {
            val subtype =  when (exception) {
                is LoginException -> BFFErrorSubtype.CANT_LOGIN
                is LogoutException -> BFFErrorSubtype.CANT_LOGOUT
                is SignUpException -> BFFErrorSubtype.CANT_SIGNUP
                else -> BFFErrorSubtype.UNKNOWN
            }
            return BFFError(BFFErrorType.AUTHENTICATION, subtype, exception.message ?: "")
        }
    }


}

