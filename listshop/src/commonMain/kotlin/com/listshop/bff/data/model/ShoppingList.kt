package com.listshop.bff.data.model

import com.listshop.bff.data.remote.ApiShoppingList
import com.listshop.bff.data.remote.ApiShoppingListCategory
import com.listshop.bff.data.remote.ApiShoppingListItem
import com.listshop.bff.data.remote.ApiShoppingListTag

data class ShoppingList(
    var externalId: String?,
    val name: String?,
    val categories : List<ShoppingListCategory> = emptyList(),
    var created: String?,
    var updated: String?,
    var layoutId: String?,
    var itemCount: Int?,

    val isStarterList: Boolean?,
    //val legend: ShoppingListLegend?
    val loading: Boolean,
    val lastLocalChange: String?,
    val lastSynced: String?

) {
    companion object Factory {
        fun create(apiValue: ApiShoppingList): ShoppingList {
            val categories = apiValue.categories.map { ShoppingListCategory.create(it) }
            return ShoppingList(
                apiValue.externalId.toString(),
                apiValue.name,
                categories,
                created = apiValue.created,
                updated = apiValue.updated,
                layoutId = apiValue.layoutId,
                itemCount = apiValue.itemCount,
                isStarterList = apiValue.isStarter,
                loading = false,
                lastLocalChange = null,
                lastSynced = null
            )
        }

        fun empty(): ShoppingList {
            return ShoppingList(
                "empty",
                "empty",
                categories = emptyList(),
                created = null,
                updated = null,
                layoutId = null,
                itemCount = null,
                isStarterList = null,
                loading = false,
                lastLocalChange = null,
                lastSynced = null,
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

    }
}

data class ShoppingListItem(
    var externalId: Long,
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
                externalId = apiValue.itemId ?: 0,
                added = apiValue.added ?: "",
                removed = null,
                updatedOn = apiValue.updated,
                crossedOff = apiValue.crossedOff,
                usedCount = apiValue.usedCount?.toInt() ?: 0,
                tag = ShoppingListTag.create(apiValue = apiValue.tag),
                legendKeys = apiValue.sourceKeys ?: emptyList(),
            )
        }
    }
}

data class ShoppingListTag(
    var externalId: String,
    var display: String,
    var tagType: String,
    var categoryId: String?,
    var isUser: Boolean?,


) {
    companion object {
        fun create(apiValue: ApiShoppingListTag)  : ShoppingListTag {
            return ShoppingListTag(
                externalId = apiValue.tagId ?: "",
                display = apiValue.name ?: "",
                tagType = apiValue.tagType ?: TagType.INGREDIENT.display,
                categoryId = null,
                isUser = null,
            )
        }
    }
}
data class ShoppingListLegend(
    var unknown: String?


) /*
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
