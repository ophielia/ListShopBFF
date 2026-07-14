package com.listshop.bff.services

import com.goncalossilva.resources.Resource
import kotlinx.serialization.json.Json


open class TestSampleProvider(val sourcePath: String) {

    inline fun <reified T> fillSample(sampleFile: String): T {
        if (sampleFile.isEmpty()) throw Exception("Empty sample file name")

        val fileName = "$sampleFile.json"
        val paths = listOf(
            "$sourcePath/$fileName",
            "src/commonTest/resources/$sourcePath/$fileName"
        )

        var jsonAsString: String? = null
        for (path in paths) {
            try {
                jsonAsString = Resource(path).readText()
                break
            } catch (e: Exception) {
                // Try next path
            }
        }

        if (jsonAsString == null) {
            throw Exception("Could not find resource $sampleFile in any of the attempted paths: $paths")
        }

        val deserializer = Json { ignoreUnknownKeys = true }
        return deserializer.decodeFromString<T>(jsonAsString)
    }
}
