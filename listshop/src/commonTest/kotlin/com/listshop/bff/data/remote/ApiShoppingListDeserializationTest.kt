package com.listshop.bff.data.remote

import com.listshop.bff.data.model.ShoppingList
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class ApiShoppingListDeserializationTest : DeserializationBaseTest() {

    @Test
    fun `when i deserialize a list of lists there aren't any issues`() = runTest {
        val jsonString = loadJsonString("ApiShoppingListOfListsSample")
        val deserializer = Json{ ignoreUnknownKeys = true }
        val embeddedList = deserializer.decodeFromString<ApiShoppingListList>(jsonString)

        val listOfLists : List<ApiShoppingList> = embeddedList.lists
        assertNotNull(listOfLists)
        assertFalse(listOfLists.isNullOrEmpty(), "list of lists is empty")
        assertEquals(5, listOfLists.size, "list size is not equal")
        val list1 = listOfLists.first()
        assertEquals("500", list1.externalId)
        assertNotNull(list1.created)
        assertNull(list1.updated)
        assertEquals(5, list1.itemCount)
        assertEquals("list3", list1.name)
        assertFalse(list1.isStarter ?: false)
        val list2 = listOfLists.last()
        assertEquals("609991", list2.externalId)
        assertEquals(2, list2.itemCount)
        assertEquals("remove from this list", list2.name)
        assertFalse(list2.isStarter ?: false)

    }

    @Test
    fun `when i deserialize a single list there aren't any issues`() = runTest {
        val jsonString = loadJsonString("ApiShoppingListSample")
        val deserializer = Json{ ignoreUnknownKeys = true }
        val list = deserializer.decodeFromString<ApiShoppingList>(jsonString)

        assertNotNull(list)
        assertEquals("51210", list.externalId)
        assertNotNull(list.created)
        assertNotNull(list.updated)
        assertEquals(86, list.itemCount)
        assertEquals("12", list.layoutId)
        assertEquals("Monop", list.name)
        assertFalse(list.isStarter ?: false)
        assertEquals(7, list.categories?.size)
        // check category items - frozen, 1 crossed off
        val produceCategory = list.categories?.first { it.categoryId.equals("52018") }
        assertEquals("Produce", produceCategory?.name)
        assertEquals(100, produceCategory?.displayOrder)
        assertEquals(28, produceCategory?.items?.size)
        // check item crossed off
        assertNotNull(produceCategory?.items?.first()?.crossedOff)
        // check dairy items - should be 5, feta should not be crossed off
        val dairy = list.categories?.first { it.categoryId.equals("7") }
        assertEquals("Dairy", dairy?.name)
        assertEquals(300, dairy?.displayOrder)
        assertEquals(11, dairy?.items?.size)
        // check feta not crossed off
        assertNull(dairy?.items?.first{(it.itemId ?: "").equals("113211") }?.crossedOff)

    }

    @Test
    fun `when i deserialize a single list I can convert to a ShoppingList`() = runTest {
        val jsonString = loadJsonString("ApiShoppingListSample")
        val deserializer = Json{ ignoreUnknownKeys = true }
        val list = deserializer.decodeFromString<ApiShoppingList>(jsonString)

        assertNotNull(list)
        val model = ShoppingList.create(list)
        assertNotNull(model)
    }

}
