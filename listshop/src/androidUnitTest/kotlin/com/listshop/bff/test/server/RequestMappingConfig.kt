package com.listshop.bff.test.server

data class RequestMappingConfig(
    val label: String,
    val requestMethod: String,
    val requestPath: String,
    val requestBodyFilename: String = "",
    val responseStatus: Int,
    val responseBodyFilename: String = ""
    )