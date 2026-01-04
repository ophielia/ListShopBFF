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
    }
}
