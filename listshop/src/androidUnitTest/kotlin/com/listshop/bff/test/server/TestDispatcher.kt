package com.listshop.bff.test.server

import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest

class TestDispatcher constructor(val mappings: List<RequestMapping>): Dispatcher() {

    @Throws(InterruptedException::class)
    override fun dispatch(request: RecordedRequest): MockResponse {
        val method = request.method ?: ""
        val path = request.path ?: ""
        val body = request.body.readUtf8()

        try {
            val response  = mappings
                .filter{ m -> m.method.equals(method) }
                .filter{ m -> m.path.equals(path) }
                .filter{ m -> m.body.equals(body.trim()) }
                .map { m -> m.response}
                .first()

            if (response != null) {
                return response
            }

        } catch (e: NoSuchElementException) {
            println("COULDN'T MAP REQUEST: " + request.path)
            return MockResponse().setResponseCode(404)
        }

        return MockResponse().setResponseCode(404)
    }
}