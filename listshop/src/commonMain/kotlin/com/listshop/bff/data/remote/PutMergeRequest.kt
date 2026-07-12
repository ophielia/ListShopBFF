@file:OptIn(ExperimentalSerializationApi::class)

package com.listshop.bff.data.remote

import com.listshop.bff.data.model.ShoppingListItem
import com.listshop.bff.data.model.ShoppingListTag
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PutMergeRequest(
    @SerialName("list_id")
    val listId: String,
    @SerialName("last_changed")
    val lastChanged: String?,
    @SerialName("layout_id")
    val layoutId: Long,
    @SerialName("merge_items")
    val mergeItems: List<MergeItem> = emptyList()
)

@Serializable
data class MergeItem @OptIn(ExperimentalSerializationApi::class) constructor(
    @SerialName("tag")
    val tag: MergeTag,
    @SerialName("item_id")
    val itemId: Long,
    @SerialName("added")
    @EncodeDefault
    var added: String? = null,
    @SerialName("removed")
    @EncodeDefault
    var removed: String? = null,
    @SerialName("updated")
    @EncodeDefault
    var updated: String? = null,
    @SerialName("last_changed")
    var lastChanged: String? = null,
    @SerialName("crossed_off")
    @EncodeDefault
    var crossedOff: String? = null,
    @SerialName("list_id")
    val listId: String,
    @SerialName("tag_id")
    val tagId: Long,
    @SerialName("used_count")
    val usedCount: Int,
    @SerialName("source_keys")
    val sourceKeys: List<String> = emptyList()
) {
    companion object Factory {
        fun create(modelItem: ShoppingListItem, listId: String): MergeItem {

            val tag = MergeTag.create(modelItem.tag)
            return MergeItem(
                tag = tag,
                itemId = modelItem.externalId.toLong(),
                added = modelItem.added,
                removed = modelItem.removed,
                updated = modelItem.updatedOn,
                crossedOff = modelItem.crossedOff,
                lastChanged = modelItem.lastChanged,
                listId = listId,
                tagId = modelItem.tag.externalId.toLong(),
                usedCount = modelItem.usedCount,
                sourceKeys = emptyList()
            )
        }
    }


    @Serializable
    data class MergeTag(
        @SerialName("tag_id")
        val tagId: String,
        @SerialName("name")
        val name: String
    ) {
        companion object {
            fun create(tag: ShoppingListTag): MergeTag {
                return MergeTag(
                    tagId = tag.externalId,
                    name = tag.display
                )

            }
        }
    }
}
