package com.listshop.bff.data.model

import com.listshop.bff.data.remote.ApiShoppingList
import com.listshop.bff.data.remote.ApiShoppingListCategory
import com.listshop.bff.data.remote.ApiShoppingListItem
import com.listshop.bff.data.remote.ApiShoppingListTag
import com.listshop.bff.data.remote.ApiTag
import com.listshop.bff.db.ListCategoryEntity
import com.listshop.bff.db.ListItemEntity
import com.listshop.bff.db.ShoppingListEntity

data class ShoppingList(
    var externalId: String?,
    val name: String?,
    val categories: List<ShoppingListCategory> = emptyList(),
    var created: String?,
    var updated: String?,
    var layoutId: String?,
    var itemCount: Int?,

    val isStarterList: Boolean?,
    val legend: ShoppingListLegend?,
    val loading: Boolean,
    val lastLocalChange: String?,
    val lastSynced: String?

) {
    companion object Factory {
        fun create(apiValue: ApiShoppingList): ShoppingList {
            val categories = apiValue.categories?.map { ShoppingListCategory.create(it) }
            return ShoppingList(
                apiValue.externalId.toString(),
                apiValue.name,
                categories ?: emptyList(),
                created = apiValue.created,
                updated = apiValue.updated,
                layoutId = apiValue.layoutId,
                itemCount = apiValue.itemCount,
                isStarterList = apiValue.isStarter,
                loading = false,
                lastLocalChange = null,
                lastSynced = null,
                legend = ShoppingListLegend()
            )
        }

        fun empty(): ShoppingList {
            return ShoppingList(
                "",
                "",
                categories = emptyList(),
                created = null,
                updated = null,
                layoutId = null,
                itemCount = null,
                isStarterList = null,
                loading = false,
                lastLocalChange = null,
                lastSynced = null,
                legend = ShoppingListLegend()
            )
        }

        fun create(dbValue: ShoppingListEntity, modelCategories: List<ShoppingListCategory>): ShoppingList {
            return ShoppingList(
                externalId = dbValue.externalId,
                name = dbValue.name,
                categories = modelCategories,
                created = dbValue.createdOn,
                updated = dbValue.updatedOn,
                layoutId = dbValue.layoutId,
                itemCount = dbValue.itemCount?.toInt() ?: 0,
                isStarterList = dbValue.isStarter,
                legend = ShoppingListLegend(),
                loading = false,
                lastLocalChange = dbValue.lastLocalChange,
                lastSynced = dbValue.lastSync
            )
        }
    }

}


data class ShoppingListCategory(
    var name: String,
    var displayOrder: Int,
    var items: List<ShoppingListItem>,
    var externalId: Long

) {

    companion object Factory {
        fun create(apiValue: ApiShoppingListCategory): ShoppingListCategory {
            val items = apiValue.items.map {
                ShoppingListItem.create(it)
            }
            return ShoppingListCategory(
                apiValue.name ?: "",
                displayOrder = apiValue.displayOrder ?: 0,
                items = items,
                externalId = apiValue.categoryId ?: 0,
            )
        }

        fun create(dbValue: ListCategoryEntity, dbItems: List<ListItemEntity>): ShoppingListCategory {
            val items = dbItems.map { ShoppingListItem.create(it) }
            return ShoppingListCategory(
                name = dbValue.name ?: "",
                displayOrder = dbValue.displayOrder?.toInt() ?: 0,
                items = items,
                externalId = dbValue.externalId?.toLong() ?: 0
            )
        }

    }
}

data class ShoppingListItem(
    var externalId: String,
    var added: String,
    var removed: String?,
    var updatedOn: String?,
    var crossedOff: String?,
    var usedCount: Int,
    var tag: ShoppingListTag,
    var legendKeys: List<String> = emptyList()


) {
    companion object Factory {
        fun create(apiValue: ApiShoppingListItem): ShoppingListItem {
            return ShoppingListItem(
                externalId = apiValue.itemId.toString(),
                added = apiValue.added ?: "",
                removed = null,
                updatedOn = apiValue.updated,
                crossedOff = apiValue.crossedOff,
                usedCount = apiValue.usedCount ?: 0,
                tag = ShoppingListTag.create(apiValue = apiValue.tag),
                legendKeys = apiValue.sourceKeys ?: emptyList(),
            )
        }

        fun create(dbValue: ListItemEntity): ShoppingListItem {
            val tag = ShoppingListTag.create(dbValue)
            return ShoppingListItem(
                externalId = dbValue.externalId ?: "0",
                added = dbValue.added ?: "",
                removed = dbValue.removed ?: "",
                updatedOn = dbValue.updatedOn ?: "",
                crossedOff = dbValue.crossedOff,
                usedCount = dbValue.usedCount?.toInt() ?: 0,
                tag = tag,
                legendKeys = emptyList()
            )
        }
    }
}

data class ShoppingListTag(
    var externalId: String,
    var display: String,
    var categoryId: String?,
    var parentId: String? = null,
    var isUser: Boolean?,


    ) {
    companion object {
        fun create(apiValue: ApiShoppingListTag): ShoppingListTag {
            return ShoppingListTag(
                externalId = apiValue.tagId ?: "",
                display = apiValue.name ?: "",
                categoryId = null,
                isUser = null,
            )
        }

        fun create(dbValue: ListItemEntity): ShoppingListTag {
            return ShoppingListTag(
                externalId = dbValue.externalId ?: "",
                display = dbValue.tagName ?: "",
                categoryId = dbValue.categoryExternalId,
                isUser = null
            )
        }

        fun create(apiValue: ApiTag, isUser: Boolean): ShoppingListTag {
            return ShoppingListTag(
                externalId = apiValue.externalId ?: "",
                display = apiValue.name ?: "",
                categoryId = "",
                parentId = apiValue.parentId ?: "",
                isUser = isUser
            )
        }
    }
}


data class ShoppingListLegend(
    var points: List<LegendPoint> = emptyList()
)

data class LegendPointSource(
    var color: String,
    var icon: String
)

data class LegendPoint(
    var key: String,
    var display: String?,
    var type: LegendPointType,
    var iconSource: LegendPointSource? = null
)

enum class LegendPointType(val display: String) {
    DISH("DISH"),
    LIST("LIST");

    companion object {
        private val map = entries.associateBy(LegendPointType::display)
        fun fromDisplay(display: String) = map[display]
    }
}

