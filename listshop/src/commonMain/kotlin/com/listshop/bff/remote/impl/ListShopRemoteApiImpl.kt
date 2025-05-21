package com.listshop.bff.remote.impl

import com.listshop.analytics.AppInfo
import com.listshop.analytics.HttpClientAnalytics
import com.listshop.analytics.ListShopAnalytics
import com.listshop.bff.exceptions.HttpClientException
import com.listshop.bff.remote.ListShopRemoteApi
import com.listshop.bff.remote.ListShopUrl
import com.listshop.bff.services.UserSessionService
import com.listshop.bff.tools.StringUtils
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE
import io.ktor.client.request.accept
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.URLProtocol
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

internal class ListShopRemoteApiImpl(
    private val engine: HttpClientEngine,
    private val sessionService : UserSessionService,
    private val appInfo: AppInfo,
    private val httpClientAnalytics: HttpClientAnalytics,
    private val listShopAnalytics: ListShopAnalytics
) : ListShopRemoteApi {

    private var _currentClientToken: String? = "init"

    private var _listshopUrl : ListShopUrl = StringUtils.buildUrl(appInfo.baseUrl)

    private var  _client = HttpClient(engine) {
        expectSuccess = true
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
        // token has changed, we need to re-construct the client
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
        return sessionService.currentSession().userToken
    }

    override fun buildPath(path: String): String {
            return _listshopUrl.pathSegments + path
    }

    override suspend fun postRequest(urlString: String, body: String?): HttpResponse {
        try {
            val response: HttpResponse = client(token())
                .post(urlString) {
                    contentType(ContentType.Application.Json)
                    setBody(body)
                }
            return response
        } catch (e: Exception) {
            throw HttpClientException("post request failed with message: " + e.message)
        }
    }

    override suspend fun getRequest(urlString: String): HttpResponse {
        try {
            val response: HttpResponse = client(token()).get(urlString)
            return response
        } catch (e: Exception) {
            throw HttpClientException("post request failed with message: " + e.message)
        }
    }

    override fun mapNonSuccessToException(statusValue: Int, exception: Exception) {
        if (!(statusValue in 200..399)) {
            throw exception
        }
    }
}
