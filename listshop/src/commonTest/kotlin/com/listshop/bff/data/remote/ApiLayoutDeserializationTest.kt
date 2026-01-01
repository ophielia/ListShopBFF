package com.listshop.bff.data.remote

import com.goncalossilva.resources.Resource
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ApiLayoutDeserializationTest : DeserializationBaseTest() {

    @Test
    fun `when i deserialize an ApiLayout there aren't any issues`() = runTest {
        val jsonString = loadJsonString("ApiLayoutSample")
        val deserializer = Json{ ignoreUnknownKeys = true }
        val embeddedLayout = deserializer.decodeFromString<EmbeddedApiLayout>(jsonString)

        assertNotNull(embeddedLayout)
        val layout = embeddedLayout?.embeddedLayout
        assertNotNull(layout)
        // we'll check the basic information
        assertEquals("RoughGrained", layout.name,"name is wrong")
        assertEquals(5, layout.externalId, "external id is wrong")
        assertEquals(true, layout.isDefault, "isDefault is wrong")
        assertNull(layout.userId)
        assertNotNull(layout.categories)
        // now check the categories
        val categories = layout.categories
        assertEquals(2, categories.size, "category size is wrong")
        val dairyCategory = categories.filter{ cat -> cat.name == "Dairy" }.first()
        assertNotNull(dairyCategory)
        assertEquals(5, dairyCategory.tags?.size, "dairy category size is wrong")
        assertEquals(300, dairyCategory.displayOrder, "external id is wrong")
        assertFalse(dairyCategory.isDefault, "isDefault is wrong")
        val noCatCategory = categories.filter{ cat -> cat.name == "Not (yet) categorized" }.first()
        assertNotNull(noCatCategory)
        assertEquals(3, noCatCategory.tags?.size, "dairy category size is wrong")
        assertEquals(700, noCatCategory.displayOrder, "external id is wrong")
        assertTrue(noCatCategory.isDefault, "isDefault is wrong")
    }

}
