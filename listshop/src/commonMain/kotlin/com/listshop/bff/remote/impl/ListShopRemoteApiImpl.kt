package com.listshop.bff.remote.impl

import com.listshop.analytics.AppInfo
import com.listshop.analytics.HttpClientAnalytics
import com.listshop.analytics.ListShopAnalytics
import com.listshop.bff.exceptions.HttpClientException
import com.listshop.bff.remote.ListShopRemoteApi
import com.listshop.bff.remote.ListShopUrl
import com.listshop.bff.services.SessionService
import com.listshop.bff.tools.StringUtils
import io.ktor.client.*
import io.ktor.client.engine.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

internal class ListShopRemoteApiImpl(
    private val engine: HttpClientEngine,
    private val sessionService: SessionService,
    private val appInfo: AppInfo,
    private val httpClientAnalytics: HttpClientAnalytics,
    private val listShopAnalytics: ListShopAnalytics
) : ListShopRemoteApi {

    private var _currentClientToken: String? = "init"
    private val REQUEST_FAILURE_MESSAGE = "request failed with message: "
    private var _listshopUrl: ListShopUrl = StringUtils.buildUrl(appInfo.baseUrl)

    private var _client = HttpClient(engine) {
        expectSuccess = true
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                useAlternativeNames = false
                isLenient = true
            })
        }
        install(Logging) {
            logger = object : Logger {
                override fun log(message: String) {
                    httpClientAnalytics.logMessage(message)
                }
            }

            level = LogLevel.INFO
        }
        install(HttpTimeout) {
            val timeout = 30000L
            connectTimeoutMillis = timeout
            requestTimeoutMillis = timeout
            socketTimeoutMillis = timeout
        }
        //Default Request Setting
        defaultRequest {
            contentType(ContentType.Application.Json)
            accept(ContentType.Application.Json)
            url {
                protocol = URLProtocol.HTTPS
            }
        }
    }

    override fun client(token: String?): HttpClient {
        if (token == _currentClientToken) {
            return _client
        }
        _currentClientToken = token
        // token has changedwe need to re-construct the client
        if (token == null) {
            createClientWithoutToken()
        } else {
            createClientWithToken(token)
        }
        return _client
    }

    private fun createClientWithToken(token: String) {
        _client = HttpClient(engine) {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    useAlternativeNames = false
                })
            }
            install(Logging) {
                logger = object : Logger {
                    override fun log(message: String) {
                        httpClientAnalytics.logMessage(message)
                    }
                }

                level = LogLevel.INFO
            }
            install(HttpTimeout) {
                val timeout = 30000L
                connectTimeoutMillis = timeout
                requestTimeoutMillis = timeout
                socketTimeoutMillis = timeout
            }
            //Default Request Setting
            defaultRequest {
                contentType(ContentType.Application.Json)
                accept(ContentType.Application.Json)
                headers {
                    append("Accept-Version", "v1")
                    append(HttpHeaders.Authorization, "Bearer " + token)
                }
                url {
                    protocol = _listshopUrl.schemeToProtocol()
                    host = _listshopUrl.host
                }
            }
        }
    }

    private fun createClientWithoutToken() {
        _client = HttpClient(engine) {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    useAlternativeNames = false
                })
            }
            install(Logging) {
                logger = object : Logger {
                    override fun log(message: String) {
                        httpClientAnalytics.logMessage(message)
                    }
                }

                level = LogLevel.INFO
            }
            install(HttpTimeout) {
                val timeout = 30000L
                connectTimeoutMillis = timeout
                requestTimeoutMillis = timeout
                socketTimeoutMillis = timeout
            }
            install(Logging) {
                logger = Logger.SIMPLE
                level = LogLevel.ALL
            }
            //Default Request Setting
            defaultRequest {
                contentType(ContentType.Application.Json)
                accept(ContentType.Application.Json)
                url {
                    protocol = _listshopUrl.schemeToProtocol()
                    host = _listshopUrl.host
                }
            }
        }

    }

    override fun token(): String? {
        return sessionService.currentUserSession().userToken
    }

    override fun buildPath(path: String): String {
        return _listshopUrl.pathSegments + path
    }

    override suspend fun postRequest(path: String, body: String?): HttpResponse {
        try {
            val response: HttpResponse = client(token())
                .post(path) {
                    contentType(ContentType.Application.Json)
                    setBody(body)
                }
            return response
        } catch (e: Exception) {
            throw HttpClientException(REQUEST_FAILURE_MESSAGE + e.message)
        }
    }

    override suspend fun putRequest(path: String, body: String?): HttpResponse {
        try {
            val response: HttpResponse = client(token())
                .put(path) {
                    contentType(ContentType.Application.Json)
                    setBody(body)
                }
            return response
        } catch (e: Exception) {
            throw HttpClientException(REQUEST_FAILURE_MESSAGE + e.message)
        }
    }

    override suspend fun getRequest(path: String): HttpResponse {
        try {
            val response: HttpResponse = client(token()).get(path)
            return response
        } catch (e: Exception) {
            throw HttpClientException(REQUEST_FAILURE_MESSAGE + e.message)
        }
    }

    override fun mapNonSuccessToException(statusValue: Int, exception: Exception) {
        if (!(statusValue in 200..399)) {
            throw exception
        }
    }
}
