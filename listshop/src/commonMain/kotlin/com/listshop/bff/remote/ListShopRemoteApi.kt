package com.listshop.bff.remote

import io.ktor.client.HttpClient
import io.ktor.client.statement.HttpResponse

internal interface ListShopRemoteApi {
    fun client( token: String?): HttpClient
    fun token(): String?
    fun buildPath(path: String): String
    suspend fun postRequest(path: String, body: String?): HttpResponse
    suspend fun getRequest(path: String): HttpResponse
    fun mapNonSuccessToException(statusValue: Int, exception: Exception)
}
