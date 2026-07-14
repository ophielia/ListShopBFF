package com.listshop.bff.data.model

import com.listshop.bff.data.remote.ApiShoppingList
import com.listshop.bff.data.remote.ApiShoppingListEmbeddedList
import com.listshop.bff.services.TestSampleProvider
import kotlinx.coroutines.test.runTest
import kotlin.test.*

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
    fun `when i create a ShoppingList from the api object - mapping is correct`() = runTest {
        val sampleProvider = TestSampleProvider("deserialization")

        val testApiObject = sampleProvider.fillSample<ApiShoppingList>("ApiShoppingListSample",)
        val testResult = ShoppingList.create(apiValue = testApiObject)

        assertNotNull(testResult)
        assertNotNull(testResult)
        assertEquals("51210", testResult.externalId)
        assertNotNull(testResult.created)
        assertNotNull(testResult.updated)
        assertEquals(86, testResult.itemCount)
        assertEquals("12", testResult.layoutId)
        assertEquals("Monop", testResult.name)
        assertFalse(testResult.isStarterList ?: false)
        assertEquals(7, testResult.categories.size)

        // check category items - frozen, 1 crossed off
        val frozen = testResult.categories.first { it.externalId.equals("52017") }
        assertEquals("Frozen", frozen.name)
        assertEquals(600, frozen.displayOrder)
        assertEquals(2, frozen.items.size)
        // check item crossed off
        assertNotNull(frozen.items.first().crossedOff)
        // check dairy items - should be 5, feta should not be crossed off
        val dairy = testResult.categories.first { it.externalId.equals("7") }
        assertEquals("Dairy", dairy.name)
        assertEquals(300, dairy.displayOrder)
        assertEquals(12, dairy.items.size)
        // check feta not crossed off
        assertNull(dairy.items.first { it.externalId == "110151" }.crossedOff)

        // check details on eggs
        val eggs = dairy.items.first { it.externalId == "110041" }
        assertEquals("eggs", eggs.tag.display)
        // check amount
        val eggsAmount = eggs.amount ?: ListShopAmount.empty()
        assertEquals(3.0, eggsAmount.quantity)
        assertEquals(3, eggsAmount.wholeQuantity)
        assertEquals(3.0, eggsAmount.roundedQuantity)
        assertEquals("3", eggsAmount.quantityDisplay)
        assertEquals("1011", eggsAmount.unitId)
        assertEquals("unit", eggsAmount.unitDisplay)
        assertEquals("3 medium", eggsAmount.amountDisplay)

        // check details
        assertEquals(3, eggs.details.size)
        eggs.details.filter { it.dishId != null }
            .map { it.dishId }
            .forEach { assertTrue(listOf("56767","104").contains(it)) }
        eggs.details.filter { it.listId != null }
            .map { it.listId }
            .forEach { assertTrue(listOf("51210","50550").contains(it)) }

        // check amount_type
        assertEquals("MIXED", eggs.amountType)

    }
}
