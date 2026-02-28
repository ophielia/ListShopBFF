package com.listshop.bff.data.remote

import com.listshop.bff.data.model.TagType
import com.listshop.bff.db.LayoutCategoryMappingEntity
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ApiDishListDeserializationTest : DeserializationBaseTest() {

    @Test
    fun `when i deserialize a list of dishes there aren't any issues`() = runTest {
        val jsonString = loadJsonString("ApiDishListSample")
        val deserializer = Json{ ignoreUnknownKeys = true }
        val embeddedDishList = deserializer.decodeFromString<EmbeddedDishResourceList>(jsonString)

        val dishList : List<ApiDish> = embeddedDishList.embeddedList.dishResourceList.map { it.embeddedDish}
        assertNotNull(dishList)
        assertFalse(dishList.isNullOrEmpty(), "list of dishes is empty")
        assertEquals(9, dishList.size, "list size is not equal")
        val dish1 = dishList.first()
        assertEquals(56890, dish1.externalId)
        assertEquals("Roasted Tuna", dish1.name)
    }



}
