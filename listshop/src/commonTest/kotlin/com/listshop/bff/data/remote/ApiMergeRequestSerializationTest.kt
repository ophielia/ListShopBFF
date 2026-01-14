package com.listshop.bff.data.remote

import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertNotNull

class ApiMergeRequestSerializationTest : DeserializationBaseTest() {

    @Test
    fun `when i deserialize the MergeResultSample - I get a json object`() = runTest {
        val jsonString = loadJsonString("MergeResultSample")
        val deserializer = Json { ignoreUnknownKeys = true }
        val mergeResult = deserializer.decodeFromString<MergeResult>(jsonString)

        assertNotNull(mergeResult)
    }


}
