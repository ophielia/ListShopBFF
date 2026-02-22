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
    var tagType: String,
    var categoryId: String?,
    var parentId: String? = null,
    var isUser: Boolean?,


    ) {
    companion object {
        fun create(apiValue: ApiShoppingListTag): ShoppingListTag {
            return ShoppingListTag(
                externalId = apiValue.tagId ?: "",
                display = apiValue.name ?: "",
                tagType = apiValue.tagType ?: TagType.INGREDIENT.display,
                categoryId = null,
                isUser = null,
            )
        }

        fun create(dbValue: ListItemEntity): ShoppingListTag {
            return ShoppingListTag(
                externalId = dbValue.externalId ?: "",
                display = dbValue.tagName ?: "",
                tagType = dbValue.tagType ?: "",
                categoryId = dbValue.categoryExternalId,
                isUser = null
            )
        }

        fun create(apiValue: ApiTag, isUser: Boolean): ShoppingListTag {
            return ShoppingListTag(
                externalId = apiValue.externalId ?: "",
                display = apiValue.name ?: "",
                tagType = apiValue.tagType ?: "",
                categoryId = "",
                parentId = apiValue.parentId ?: "",
                isUser = isUser
            )
        }
    }
}


data class ShoppingListLegend(
    var legendLkup: Map<String, LegendPoint> = emptyMap(),
    var legendKeys: List<LegendPoint> = emptyList()
)

data class LegendPointSource(
    var color: String,
    var icon: String
)

data class LegendPoint(
    var key: String,
    var display: String?,
    var iconSource: LegendPointSource?
)


/*
//MM legend is open

currently, provides map and keys
map contains LegendPoints - which haven't been coded yet

model implemented like this in ios
code for legendpoint, legend, and legendpointsource
public struct LegendPointSource: Codable, Equatable {


    let color: String
    let icon: String

    init(color: String, icon: String) {
        self.color = color
        self.icon = icon
    }

    func toPath(forCircle: Bool) -> String {
        let base = forCircle ? "circles/" : ""
        let source = base + color + "/@" + icon
        return source
    }

    static public func overflowPoint() -> LegendPointSource {
        LegendPointSource(color: "orange", icon: "placeholder")
    }

}

public struct LegendPoint: Codable {


    let key: String
    let display: String
    var iconSource: LegendPointSource?

    init(key: String,
         display: String) {
        self.key = key
        self.display = display
        iconSource = nil
    }

    init(key: String,
         display: String,
         iconSource: LegendPointSource) {
        self.key = key
        self.display = display
        self.iconSource = iconSource
    }

    init(key: String,
         display: String,
         icon: String,
         color: String) {
        self.key = key
        self.display = display
        iconSource = LegendPointSource(color: color, icon: icon)
    }

    init(networkLegend: ApiLegend) {
        key = networkLegend.key
        display = networkLegend.display
    }


}

public struct ShoppingListLegend: Codable {
    var legendLkup: Dictionary<String, LegendPoint>

    var legendKeys: Array<LegendPoint> {
        var legendPoints: Array<LegendPoint> = []
        let itemKeys = legendLkup.keys
        itemKeys.forEach { key in
            if let legend = legendLkup[key] {
                legendPoints.append(legend)
            }
        }
        legendPoints.sort { (point: LegendPoint, point2: LegendPoint) in
            point.display.capitalized < point2.display.capitalized
        }
        return legendPoints
    }

    init(legendPoints: [LegendPoint]) {
        legendLkup = [:]
        legendPoints.forEach { point in
            legendLkup[point.key] = point
        }

    }

    func isEmpty() -> Bool {
        legendLkup.keys.count == 0
    }
}

{
    companion object {
        fun create(apiValue: ApiShoppingListLegend)  : ShoppingListLegend {
            return ShoppingListLegend(
                externalId = apiValue.tagId ?: "",
                display = apiValue.name ?: "",
                tagType = apiValue.tagType ?: TagType.INGREDIENT.display,
                categoryId = null,
                isUser = null,
            )
        }
    }
}

*/
