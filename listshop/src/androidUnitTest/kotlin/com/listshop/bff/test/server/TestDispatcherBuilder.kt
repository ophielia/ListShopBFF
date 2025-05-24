package com.listshop.bff.test.server

import com.goncalossilva.resources.Resource
import com.google.gson.Gson
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse

class TestDispatcherBuilder(val testCaseName : String)  {
    val configFileNames: MutableList<String> = mutableListOf()

    val gson = Gson()
    val RESOURCES_PATH = "src/commonTest/resources"
    val CONFIG_SUBPATH = "/mock/config/"
    val JSON_SUBPATH = "/mock/json/"



    fun withConfigFile(filename: String) : TestDispatcherBuilder {
        configFileNames.add(filename)
        return this
    }


    fun build() : Dispatcher {
        if (configFileNames.isEmpty()) {
            return TestDispatcher(emptyList())
        }
        val requestMappings = configFileNames
            .map { cfn -> loadMappingFromConfig(cfn)}
        return TestDispatcher(requestMappings)
    }

    private fun loadMappingFromConfig(configFileName: String): RequestMapping {
        // load config
        val configuration = loadConfiguration(configFileName)
        // load request and response bodies, if necessary
        val requestBody = loadBodyData(configuration.requestBodyFilename)
        val responseBody = loadBodyData(configuration.responseBodyFilename)
        // create response
        val response =  MockResponse()
            .setBody(responseBody)
            .setResponseCode(configuration.responseStatus)
            .setHeader("Content-Type","application/json")
        // create RequestMapping and return
        val requestMapping = RequestMapping(configuration.requestPath,
            configuration.requestMethod,
            requestBody,
            response
            )
        return requestMapping
    }

    private fun loadBodyData(dataFilename: String): String {
        if (dataFilename.isEmpty()) {
            return ""
        }
        val fullPath = RESOURCES_PATH + JSON_SUBPATH + dataFilename
        return Resource(fullPath).readText()
    }

    private fun loadConfiguration(configFileName: String): RequestMappingConfig {
        var fullPath = RESOURCES_PATH + CONFIG_SUBPATH
        if (!testCaseName.isEmpty()) {
            fullPath  += "/" + testCaseName + "/"
        }
        fullPath += configFileName
        val jsonString =  Resource(fullPath).readText()
        return gson.fromJson(jsonString, RequestMappingConfig::class.java)
    }
}