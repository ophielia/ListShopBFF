package com.listshop.bff.data.model

import com.listshop.bff.data.remote.ApiShoppingListEmbeddedList
import com.listshop.bff.services.TestSampleProvider
import com.listshop.bff.services.TestUtils
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ShoppingListTest {

    @Test
    fun testCreate() {
        val startString = "1.2.3"
        val semanticVersion = SemanticVersion.create(startString)
        assertEquals(1, semanticVersion.major)
        assertEquals(2, semanticVersion.minor)
        assertEquals(3, semanticVersion.patch)
    }


    @Test
    fun `when i create a ShoppingList from the api object, mapping is correct`() = runTest {
        val sampleProvider = TestSampleProvider()

        val testApiObject = sampleProvider.fillSample<ApiShoppingListEmbeddedList>("ApiShoppingListSample")
        val testResult = ShoppingList.create(apiValue = testApiObject.embeddedList)

//MM up next - filling in categories, items (and tags) in ShoppingList from api.

        assertNotNull(testResult)
        assertNotNull(testResult)
        assertEquals("51167", testResult.externalId)
        assertNotNull(testResult.created)
        assertNotNull(testResult.updated)
        assertEquals(34, testResult.itemCount)
        assertEquals("12", testResult.layoutId)
        assertEquals("Monop", testResult.name)
        assertFalse(testResult.isStarterList ?: false)
        assertEquals(3, testResult.categories.size)

        // check category items - frozen, 1 crossed off
        val frozen = testResult.categories.first { it.externalId == 10L }
        assertEquals("Frozen", frozen.name)
        assertEquals(600, frozen.displayOrder)
        assertEquals(1, frozen.items.size)
        // check item crossed off
        assertNotNull(frozen.items.first().crossedOff)
        // check dairy items - should be 5, feta should not be crossed off
        val dairy = testResult.categories.first { it.externalId == 7L }
        assertEquals("Dairy", dairy.name)
        assertEquals(300, dairy.displayOrder)
        assertEquals(5, dairy.items.size)
        // check feta not crossed off
        assertNull(dairy.items.first{it.externalId == 100976L }.crossedOff)

        //MM STILL OPEN - legend tests

    }
}
