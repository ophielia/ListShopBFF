package com.listshop.bff.exceptions

open class AuthenticationException(message: String? = null, cause: Throwable? = null) : Exception(message, cause)

class LoginException(message: String? = null, cause: Throwable? = null) : AuthenticationException(message, cause)

class SignUpException(message: String? = null, cause: Throwable? = null) : AuthenticationException(message, cause)

class LogoutException(message: String? = null, cause: Throwable? = null) : AuthenticationException(message, cause)
