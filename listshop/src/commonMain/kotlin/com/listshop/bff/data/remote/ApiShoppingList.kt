package com.listshop.bff.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ApiShoppingList(
    @SerialName("list_id")
    val externalId: String?,
    @SerialName("created")
    val created: String?,
    @SerialName("updated")
    val updated: String?,
    @SerialName("item_count")
    val itemCount: Int?,
    @SerialName("layout_id")
    val layoutId: String? = "",
    @SerialName("name")
    var name: String?,
    @SerialName("is_starter_list")
    val isStarter: Boolean?,
    @SerialName("categories")
    var categories : List<ApiShoppingListCategory>? = emptyList(),
    @SerialName("legend")
    val legend : List<ApiShoppingListLegendPoint>? = emptyList()
)

@Serializable
data class ApiShoppingListLegend(

    val points: List<ApiShoppingListLegendPoint>?

    )

@Serializable
data class ApiShoppingListLegendPoint(

    @SerialName("display")
    val display: String?,
    @SerialName("related_id")
    val id: String?,
    @SerialName("source_type")
    val sourceType: String?,
)

@Serializable
data class ApiShoppingListCategory(

    @SerialName("name")
    val name: String,

    @SerialName("category_id")
    val categoryId: String,
    @SerialName("display_order")
    val displayOrder: Int? = null,
    @SerialName("items")
    val items: List<ApiShoppingListItem>
)

@Serializable
data class ApiShoppingListItem(
    @SerialName("item_id")
    val itemId: String?,
    @SerialName("added")
    val added: String?,
    @SerialName("updated")
    val updated: String? = null,
    @SerialName("last_changed")
    val lastChanged: String? = null,
    @SerialName("crossed_off")
    val crossedOff: String? = null,
    @SerialName("used_count")
    val usedCount: Int?,
    @SerialName("sources")
    val sourceKeys: List<String>?,
    @SerialName("amount_type")
    val amountType: String?,

    @SerialName("tag")
    val tag: ApiShoppingListTag,
    @SerialName("amount")
    val amount: ApiShoppingListAmount? = null,
    @SerialName("details")
    val details: List<ApiShoppingListDetails>,
    )

@Serializable
data class ApiShoppingListDetails(
    val amount: ApiShoppingListAmount? = null,
    @SerialName("dish_id")
    val linkedDishId: String? = null,
    @SerialName("list_id")
    val linkedListId: String? = null,
    @SerialName("contains_unspecified")
    val containsUnspecified: Boolean = false
    )

@Serializable
data class ApiShoppingListAmount(
    val quantity: Double? = null,
    @SerialName("whole_quantity")
    val wholeQuantity: Int? = null,
    @SerialName("fractional_quantity")
    val fractionalQuantity: String? = null,
    @SerialName("rounded_quantity")
    val roundedQuantity: Double? = null,
    @SerialName("quantity_display")
    val quantityDisplay: String? = null,
    @SerialName("unit_id")
    val unitId: String? = null,
    @SerialName("unit_display")
    val unitDisplay: String? = null,
    @SerialName("display")
    val display: String? = null
    )

@Serializable
data class ApiShoppingListTag(
    @SerialName("tag_id")
    val tagId: String?,
    @SerialName("name")
    val name: String?
)

@Serializable
data class ApiLegendItems(
    @SerialName("key")
    val key: String?,
    @SerialName("display")
    val display: String?,
)




@Serializable
data class ApiShoppingListEmbeddedList(
    @SerialName("shopping_list")
    val embeddedList: ApiShoppingList
)

@Serializable
data class ApiShoppingListResourceList(
    @SerialName("shoppingListResourceList")
    val shoppingListResourceList: List<ApiShoppingListEmbeddedList>
)

@Serializable
data class ApiShoppingListList  (
    @SerialName("list_of_lists")
    val lists: List<ApiShoppingList>
)
