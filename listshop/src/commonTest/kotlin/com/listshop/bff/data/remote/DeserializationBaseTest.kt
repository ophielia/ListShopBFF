package com.listshop.bff.data.remote

import com.goncalossilva.resources.Resource

open class DeserializationBaseTest {
    protected fun loadJsonString(jsonFileName: String): String {
        if (jsonFileName.isEmpty()) return ""
        
        val fileName = "$jsonFileName.json"
        val paths = listOf(
            "deserialization/$fileName",
            "src/commonTest/resources/deserialization/$fileName"
        )

        for (path in paths) {
            try {
                return Resource(path).readText()
            } catch (e: Exception) {
                // Try next path
            }
        }
        
        throw Exception("Could not find resource $jsonFileName in any of the attempted paths: $paths")
    }
}
