package com.listshop.bff.data.remote

import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertNotNull

class PutMergeRequestSerializationTest : DeserializationBaseTest() {

    @Test
    fun `when i deserialize the PutMergeRequestSample - I get a json object`() = runTest {
        val jsonString = loadJsonString("PutMergeRequestSample")
        val deserializer = Json { ignoreUnknownKeys = true }
        val putRequest = deserializer.decodeFromString<PutMergeRequest>(jsonString)

        assertNotNull(putRequest)
    }

    @Test
    fun `when i serialize the PutMergeRequestSample - it doesn't blow up`() = runTest {
        val listId = "110000"
        val now = Clock.System.now().toLocalDateTime(TimeZone.UTC).toString()

        val mergeItems = dummyMergeItems(listId)
        val mergeRequest = PutMergeRequest(
            listId = listId,
            lastChanged = now,
            layoutId = 5,
            mergeItems = mergeItems
        )
        val resultString = Json.encodeToString(mergeRequest)
        assertNotNull(resultString)
    }

    private fun dummyMergeItems(listId: String): List<MergeItem> {
        val tag1 = buildTag("443", "chicken thighs", "Ingredient")
        val item1 = buildItem(10012, listId, tag1, 1, true, false, false, true)
        val tag2 = buildTag("21", "broccoli", "Ingredient")
        val item2 = buildItem(10009, listId, tag2, 1, true, false, false, true)
        val tag3 = buildTag("81", "carrots", "Ingredient")
        val item3 = buildItem(10006, listId, tag3, 1, true, false, true, true)
        val tag4 = buildTag("15", "celery", "Ingredient")
        val item4 = buildItem(10004, listId, tag4, 1, true, false, false, true)
        val tag5 = buildTag("34", "cucumber", "Ingredient")
        val item5 = buildItem(10016, listId, tag5, 1, true, false, false, true)
        val tag6 = buildTag("212", "fresh ginger", "Ingredient")
        val item6 = buildItem(10008, listId, tag6, 1, true, false, false, true)
        val tag7 = buildTag("19", "garlic", "Ingredient")
        val item7 = buildItem(10007, listId, tag7, 1, true, false, false, true)
        val tag8 = buildTag("32", "lettuce", "Ingredient")
        val item8 = buildItem(10014, listId, tag8, 1, true, false, false, false)
        val tag9 = buildTag("16", "onion", "Ingredient")
        val item9 = buildItem(10005, listId, tag9, 1, true, false, false, false)
        val tag10 = buildTag("33", "tomatoes", "Ingredient")
        val item10 = buildItem(10015, listId, tag10, 1, true, false, false, false)
        return listOf<MergeItem>(item1, item2, item3, item4, item5, item6, item7, item8, item9, item10)

    }

    private fun buildItem(
        itemId: Long,
        listId: String,
        tag: MergeItem.MergeTag,
        usedCount: Int,
        added: Boolean,
        removed: Boolean,
        updated: Boolean,
        crossedOff: Boolean
    ): MergeItem {
        val now = Clock.System.now().toLocalDateTime(TimeZone.UTC).toString()
        val tagIdLong = tag.tagId.toLong()
        var item = MergeItem(
            tag = tag,
            itemId = itemId,
            listId = listId,
            tagId = tagIdLong,
            usedCount = usedCount
        )
        if (added) item.added = now
        if (removed) item.removed = now
        if (updated) item.updated = now
        if (crossedOff) item.crossedOff = now
        return item
    }

    private fun buildTag(tagId: String, name: String, tagType: String): MergeItem.MergeTag {
        return MergeItem.MergeTag(
            tagId = tagId,
            name = name
        )
    }


}
