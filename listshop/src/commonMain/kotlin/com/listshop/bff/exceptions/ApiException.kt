package com.listshop.bff.exceptions

open class ApiException(message: String? = null, cause: Throwable? = null) : Exception(message, cause)
class BadRequestException(message: String? = null, cause: Throwable? = null) : ApiException(message, cause)
class ServerErrorException(message: String? = null, cause: Throwable? = null) : ApiException(message, cause)
class UnknownApiException(message: String? = null, cause: Throwable? = null) : ApiException(message, cause)


class UnexpectedEmptyException(message: String? = null, cause: Throwable? = null) : Exception(message, cause)
class BadParameterException(message: String? = null, cause: Throwable? = null) : Exception(message, cause)
