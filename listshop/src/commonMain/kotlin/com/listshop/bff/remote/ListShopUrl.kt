package com.listshop.bff.remote

import io.ktor.http.URLProtocol
import io.ktor.util.toLowerCasePreservingASCIIRules

data class ListShopUrl(
    val scheme: String = "",
    val host: String = "",
    val pathSegments: String = ""
) {
    fun schemeToProtocol() : URLProtocol {
        if (scheme.toLowerCasePreservingASCIIRules().equals("http")) {
            return URLProtocol.HTTP
        }
        return URLProtocol.HTTPS
    }
}