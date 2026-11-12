package com.listshop.bff.data.bff

import com.listshop.bff.exceptions.*
import io.ktor.serialization.*
import kotlinx.serialization.SerializationException

data class BFFError(
    var type: BFFErrorType,
    var subType: BFFErrorSubtype,
    var message: String
) {

    companion object {


        fun rawErrorFromException(exception: Exception): BFFError {
            return when (exception) {
                is IllegalArgumentException -> handleIllegalArgumentException(exception)
                is HttpClientException -> handleHttpClientException(exception)
                is BadParameterException -> handleBadParameterException(exception)
                is InternalDataException -> handleInternalDataException(exception)
                is OfflineException -> handleOfflineException(exception)
                is AuthenticationException -> handleAuthenticationException(exception)
                is AuthorizationException -> handleAuthorizationException(exception)
                is ApiException -> handleApiException(exception)
                is LoggedOutException -> handleLoggedOutException(exception)
                is SerializationException -> handleSerializationException(exception)
                is ContentConvertException -> handleSerializationException(exception)
                else -> handleUnknownException(exception)
            }
        }

        private fun handleUnknownException(exception: Exception): BFFError {
            return BFFError(BFFErrorType.UNKNOWN, BFFErrorSubtype.UNKNOWN, exception.message ?: "")
        }

        private fun handleLoggedOutException(exception: LoggedOutException): BFFError {
            return BFFError(BFFErrorType.AUTHENTICATION, BFFErrorSubtype.NOT_LOGGGED_IN, exception.message ?: "")
        }

        private fun handleSerializationException(exception: Exception): BFFError {
            return BFFError(BFFErrorType.API, BFFErrorSubtype.BAD_REQUEST, exception.message ?: "")
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
            val subtype = when (exception) {
                is LoginException -> BFFErrorSubtype.CANT_LOGIN
                is LogoutException -> BFFErrorSubtype.CANT_LOGOUT
                is SignUpException -> BFFErrorSubtype.CANT_SIGNUP
                else -> BFFErrorSubtype.LOGIN_REQUIRED
            }
            return BFFError(BFFErrorType.AUTHENTICATION, subtype, exception.message ?: "")
        }

        private fun handleAuthorizationException(exception: AuthorizationException): BFFError {

            return BFFError(BFFErrorType.LOGIN_REQUIRED, BFFErrorSubtype.LOGIN_REQUIRED, exception.message ?: "")
        }
    }


}

