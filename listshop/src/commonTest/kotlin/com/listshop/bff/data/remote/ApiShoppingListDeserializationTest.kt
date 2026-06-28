package com.listshop.bff.data.remote

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
        assertEquals(3, listOfLists.size, "list size is not equal")
        val list1 = listOfLists.first()
        assertEquals(51167, list1.externalId)
        assertNotNull(list1.created)
        assertNotNull(list1.updated)
        assertEquals(34, list1.itemCount)
        assertEquals(20, list1.userId)
        assertEquals("12", list1.layoutId)
        assertEquals("Monop", list1.name)
        assertFalse(list1.isStarter ?: false)
        val list2 = listOfLists.last()
        assertEquals(50824, list2.externalId)
        assertEquals(0, list2.itemCount)
        assertEquals(20, list2.userId)
        assertEquals("Market list", list2.name)
        assertFalse(list2.isStarter ?: false)

    }

    @Test
    fun `when i deserialize a single list there aren't any issues`() = runTest {
        val jsonString = loadJsonString("ApiShoppingListSample")
        val deserializer = Json{ ignoreUnknownKeys = true }
        val list = deserializer.decodeFromString<ApiShoppingList>(jsonString)

        assertNotNull(list)
        assertEquals(51167, list.externalId)
        assertNotNull(list.created)
        assertNotNull(list.updated)
        assertEquals(34, list.itemCount)
        assertEquals(20, list.userId)
        assertEquals("12", list.layoutId)
        assertEquals("Monop", list.name)
        assertFalse(list.isStarter ?: false)
        assertEquals(3, list.categories?.size)
        // check category items - frozen, 1 crossed off
        val frozen = list.categories?.first { it.categoryId == 10L }
        assertEquals("Frozen", frozen?.name)
        assertEquals(600, frozen?.displayOrder)
        assertEquals(1, frozen?.items?.size)
        // check item crossed off
        assertNotNull(frozen?.items?.first()?.crossedOff)
        // check dairy items - should be 5, feta should not be crossed off
        val dairy = list.categories?.first { it.categoryId == 7L }
        assertEquals("Dairy", dairy?.name)
        assertEquals(300, dairy?.displayOrder)
        assertEquals(5, dairy?.items?.size)
        // check feta not crossed off
        assertNull(dairy?.items?.first{it.itemId == 100976L }?.crossedOff)

    }


}
