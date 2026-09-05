package com.listshop.bff.data.model

import com.listshop.bff.data.remote.ApiShoppingList
import com.listshop.bff.data.remote.ApiShoppingListAmount
import com.listshop.bff.data.remote.ApiShoppingListCategory
import com.listshop.bff.data.remote.ApiShoppingListDetails
import com.listshop.bff.data.remote.ApiShoppingListItem
import com.listshop.bff.data.remote.ApiShoppingListLegendPoint
import com.listshop.bff.data.remote.ApiShoppingListTag
import com.listshop.bff.data.remote.ApiTag
import com.listshop.bff.db.LegendPointEntity
import com.listshop.bff.db.ListCategoryEntity
import com.listshop.bff.db.ListItemDetailEntity
import com.listshop.bff.db.ListItemEntity
import com.listshop.bff.db.ShoppingListEntity
import kotlinx.datetime.Clock
import kotlin.jvm.JvmName

data class ShoppingList(
    var externalId: String,
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
            val legendPoints = apiValue.legend?.map { LegendPoint.create(it) }
            val now = Clock.System.now().toString()
            return ShoppingList(
                apiValue.externalId,
                apiValue.name,
                categories ?: emptyList(),
                created = apiValue.created,
                updated = apiValue.updated,
                layoutId = apiValue.layoutId,
                itemCount = apiValue.itemCount,
                isStarterList = apiValue.isStarter,
                loading = false,
                lastLocalChange = null,
                lastSynced = now,
                legend = ShoppingListLegend.create(legendPoints)
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

        fun create(dbValue: ShoppingListEntity, modelCategories: List<ShoppingListCategory>, legendPoints: List<LegendPointEntity>): ShoppingList {
            val apiPoints = legendPoints.map{ LegendPoint.create(dbValue = it) }
            return ShoppingList(
                externalId = dbValue.externalId ?: "",
                name = dbValue.name,
                categories = modelCategories,
                created = dbValue.createdOn,
                updated = dbValue.updatedOn,
                layoutId = dbValue.layoutId,
                itemCount = dbValue.itemCount?.toInt() ?: 0,
                isStarterList = dbValue.isStarter,
                legend = ShoppingListLegend(apiPoints),
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
    var externalId: String

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
                externalId = apiValue.categoryId ?: "",
            )
        }

        @JvmName("createFromDbEntities")
        fun create(dbValue: ListCategoryEntity, dbItems: List<ListItemEntity>): ShoppingListCategory {
            val items = dbItems.map { ShoppingListItem.create(it) }
            return ShoppingListCategory(
                name = dbValue.name ?: "",
                displayOrder = dbValue.displayOrder?.toInt() ?: 0,
                items = items,
                externalId = dbValue.externalId ?: ""
            )
        }

        fun create(dbValue: ListCategoryEntity, items: List<ShoppingListItem>): ShoppingListCategory {
            return ShoppingListCategory(
                name = dbValue.name ?: "",
                displayOrder = dbValue.displayOrder?.toInt() ?: 0,
                items = items,
                externalId = dbValue.externalId ?: ""
            )
        }
    }
}

data class ShoppingListItem(
    var externalId: String,
    var added: String,
    var removed: String?,
    var updatedOn: String?,
    var lastChanged: String?,
    var crossedOff: String?,
    var usedCount: Int,
    var tag: ShoppingListTag,
    var amount: ListShopAmount?,
    var details: List<ShoppingListDetail> = emptyList(),
    var amountType: String? = null,
    var legendKeys: List<String> = emptyList()


) {
    companion object Factory {
        fun create(apiValue: ApiShoppingListItem): ShoppingListItem {
            val details = apiValue.details.map { ShoppingListDetail.create(it) }
            return ShoppingListItem(
                externalId = apiValue.itemId ?: "",
                added = apiValue.added ?: "",
                removed = null,
                updatedOn = apiValue.updated,
                crossedOff = apiValue.crossedOff,
                lastChanged = apiValue.lastChanged,
                usedCount = apiValue.usedCount ?: 0,
                tag = ShoppingListTag.create(apiValue = apiValue.tag),
                amount = ListShopAmount.create(apiValue = apiValue.amount),
                details = details,
                amountType = apiValue.amountType,
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
                lastChanged = dbValue.lastChanged,
                usedCount = dbValue.usedCount?.toInt() ?: 0,
                tag = tag,
                amount = if (dbValue.quantity != null || dbValue.wholeQuantity != null) {
                    ListShopAmount(
                        quantity = dbValue.quantity,
                        wholeQuantity = dbValue.wholeQuantity?.toInt(),
                        roundedQuantity = dbValue.roundedQuantity,
                        quantityDisplay = dbValue.quantityDisplay,
                        unitId = dbValue.unitId,
                        unitDisplay = dbValue.unitDisplay,
                        amountDisplay = dbValue.amountDisplay
                    )
                } else null,
                legendKeys = dbValue.legendKeys?.split(",")?.filter { it.isNotEmpty() } ?: emptyList()
            )
        }

        fun create(dbValue: ListItemEntity, details: List<ListItemDetailEntity>): ShoppingListItem {
            val detailList = details.map { ShoppingListDetail.create(it) }
            val tag = ShoppingListTag.create(dbValue)
            return ShoppingListItem(
                externalId = dbValue.externalId ?: "0",
                added = dbValue.added ?: "",
                removed = dbValue.removed ?: "",
                lastChanged = dbValue.lastChanged,
                updatedOn = dbValue.updatedOn ?: "",
                crossedOff = dbValue.crossedOff,
                usedCount = dbValue.usedCount?.toInt() ?: 0,
                tag = tag,
                amount = ListShopAmount.create(dbValue),
                details = detailList,
                legendKeys = dbValue.legendKeys?.split(",")?.filter { it.isNotEmpty() } ?: emptyList()
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
                isUser = null ,
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

data class ListShopAmount(
    var quantity: Double?,
    var wholeQuantity: Int?,
    var roundedQuantity: Double?,
    var quantityDisplay: String?,
    var unitId: String?,
    var unitDisplay: String?,
    var amountDisplay: String?,
    var fractionalQuantity: String? = null,
) {
    companion object {
        fun empty() = ListShopAmount(null, null, null, null, null, null, null)
        fun create(apiValue: ApiShoppingListAmount?) : ListShopAmount? {
            if (apiValue == null) {
                return null
            }
            return ListShopAmount(
                quantity = apiValue.quantity,
                wholeQuantity = apiValue.wholeQuantity,
                fractionalQuantity = apiValue.fractionalQuantity,
                roundedQuantity = apiValue.roundedQuantity,
                quantityDisplay = apiValue.quantityDisplay,
                unitId = apiValue.unitId,
                unitDisplay = apiValue.unitDisplay,
                amountDisplay = apiValue.display
            )
        }

        fun create(dbListDetail: ListItemDetailEntity?) : ListShopAmount? {
            if (dbListDetail == null || (dbListDetail.quantity == null && dbListDetail.wholeQuantity == null)) {
                return null
            }
            return ListShopAmount(
                quantity = dbListDetail.quantity,
                wholeQuantity = dbListDetail.wholeQuantity,
                fractionalQuantity = dbListDetail.fractionalQuantity,
                roundedQuantity = dbListDetail.roundedQuantity,
                quantityDisplay = dbListDetail.quantityDisplay,
                unitId = dbListDetail.unitId,
                unitDisplay = dbListDetail.unitDisplay,
                amountDisplay = dbListDetail.amountDisplay
            )
        }


        fun create(dbListItem: ListItemEntity?) : ListShopAmount? {
            if (dbListItem == null || (dbListItem.quantity == null && dbListItem.wholeQuantity == null)) {
                return null
            }
            return ListShopAmount(
                quantity = dbListItem.quantity,
                wholeQuantity = dbListItem.wholeQuantity,
                fractionalQuantity = dbListItem.fractionalQuantity,
                roundedQuantity = dbListItem.roundedQuantity,
                quantityDisplay = dbListItem.quantityDisplay,
                unitId = dbListItem.unitId,
                unitDisplay = dbListItem.unitDisplay,
                amountDisplay = dbListItem.amountDisplay
            )
        }
    }
}

data class ShoppingListDetail(
    var amount: ListShopAmount?,
    var dishId: String?,
    var listId: String?,
    var containsUnspecified: Boolean = false
)
{
    companion object {
        fun create(apiValue: ApiShoppingListDetails): ShoppingListDetail {
            return ShoppingListDetail(
                amount = ListShopAmount.create(apiValue = apiValue.amount),
                dishId = apiValue.linkedDishId,
                listId = apiValue.linkedListId,
                containsUnspecified = apiValue.containsUnspecified ?: false
            )
        }
        fun create(dbValue: ListItemDetailEntity): ShoppingListDetail {
            return ShoppingListDetail(
                amount = ListShopAmount.create(dbValue),
                dishId = dbValue.dishId,
                listId = dbValue.listId,
                containsUnspecified = dbValue.containsUnspecified
            )
        }
    }
}

data class ShoppingListLegend(
    var points: List<LegendPoint> = emptyList())
    {
        companion object {
        fun create(points: List<LegendPoint>?) = ShoppingListLegend(points ?: emptyList())
    }
    }

data class LegendPointSource(
    var color: String,
    var icon: String
)

data class LegendPoint(
    var key: String,
    var display: String?,
    var type: LegendPointType,
    var iconSource: LegendPointSource? = null
) {
    companion object Factory {
        fun create(apiValue: ApiShoppingListLegendPoint): LegendPoint {
            return LegendPoint(
                key = apiValue.id ?: "",
                display = apiValue.display,
                type = LegendPointType.fromDisplay(apiValue.sourceType) ?: LegendPointType.DISH,

            )
        }

        fun create(dbValue: LegendPointEntity): LegendPoint {
            return LegendPoint(
                key = dbValue.id ?: "",
                display = dbValue.display,
                type = LegendPointType.fromDisplay(dbValue.type) ?: LegendPointType.DISH

                )
        }
    }
}

enum class LegendPointType(val display: String) {
    DISH("DISH"),
    LIST("LIST");

    companion object {
        private val map = entries.associateBy(LegendPointType::display)
        fun fromDisplay(display: String?) = map[display]
    }
}

