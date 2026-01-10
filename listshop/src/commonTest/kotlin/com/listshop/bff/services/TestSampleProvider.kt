package com.listshop.bff.services

import com.goncalossilva.resources.Resource
import kotlinx.serialization.json.Json


open class TestSampleProvider( val sourcePath: String) {

    inline fun <reified T> fillSample(sampleFile: String): T {
        val fullPath = if (!sampleFile.isEmpty()) {
                "$sourcePath/$sampleFile.json"
        } else {
            "bad path"
        }

        val jsonAsString = Resource(fullPath).readText()
        val deserializer = Json { ignoreUnknownKeys = true }
        return deserializer.decodeFromString<T>(jsonAsString)


    }

}
