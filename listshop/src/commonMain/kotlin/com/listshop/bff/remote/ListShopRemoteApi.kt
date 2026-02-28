package com.listshop.bff.remote

import io.ktor.client.*
import io.ktor.client.statement.*

internal interface ListShopRemoteApi {
    fun client(token: String?): HttpClient
    fun token(): String?
    fun buildPath(path: String): String
    suspend fun postRequest(path: String, body: String?): HttpResponse
    suspend fun deleteRequest(path: String): HttpResponse
    suspend fun putRequest(path: String, body: String?): HttpResponse
    suspend fun getRequest(path: String): HttpResponse
    fun mapNonSuccessToException(statusValue: Int, exception: Exception)
    fun pullLocation(response:HttpResponse): String
    fun mapNonSuccessToException(statusValue: Int, message: String)
}
