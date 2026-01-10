package com.listshop.bff.data.remote

import com.goncalossilva.resources.Resource

open class DeserializationBaseTest {
    val DESERIALIZATION_RESOURCES_PATH = "src/commonTest/resources/deserialization"
    protected fun loadJsonString(jsonFileName: String): String {
        val fullPath = if (!jsonFileName.isEmpty()) {
            DESERIALIZATION_RESOURCES_PATH + "/" + jsonFileName + ".json"
        } else {
            return ""
        }

        return Resource(fullPath).readText()

    }
}
