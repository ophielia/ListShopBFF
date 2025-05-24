package com.listshop.bff.test.server

import okhttp3.mockwebserver.MockResponse

data class RequestMapping(
    val path: String,
    val method: String,
    val body: String,
    val response: MockResponse
) {
}