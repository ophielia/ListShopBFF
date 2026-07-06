package com.listshop.bff.data.remote

import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull

class ApiTagListDeserializationTest : DeserializationBaseTest() {

    @Test
    fun `when i deserialize a list of tags there aren't any issues`() = runTest {
        val jsonString = loadJsonString("ApiTagListSample")
        val deserializer = Json{ ignoreUnknownKeys = true }
        val apiTagList = deserializer.decodeFromString<ApiTagList>(jsonString)

        val tagList : List<ApiTag> = apiTagList.tagList
        assertNotNull(tagList)
        assertFalse(tagList.isNullOrEmpty(), "list of tags is empty")
        assertEquals(50, tagList.size, "list size is not equal")
        val tag = tagList.first()
        assertEquals("251", tag.externalId)
        assertEquals("stew meat", tag.name)
    }



}
