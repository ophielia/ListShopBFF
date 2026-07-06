package com.listshop.bff.data.remote

import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull

class ApiDishListDeserializationTest : DeserializationBaseTest() {

    @Test
    fun `when i deserialize a list of dishes there aren't any issues`() = runTest {
        val jsonString = loadJsonString("ApiDishListSample")
        val deserializer = Json{ ignoreUnknownKeys = true }
        val embeddedDishList = deserializer.decodeFromString<ApiDishList>(jsonString)

        val dishList : List<ApiDish> = embeddedDishList.dishes
        assertNotNull(dishList)
        assertFalse(dishList.isNullOrEmpty(), "list of dishes is empty")
        assertEquals(10, dishList.size, "list size is not equal")
        val dish1 = dishList.first()
        assertEquals(43, dish1.externalId)
        assertEquals("arrugula gnocchi", dish1.name)
    }



}
