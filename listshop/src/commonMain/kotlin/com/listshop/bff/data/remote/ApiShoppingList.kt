package com.listshop.bff.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ApiShoppingList(
    @SerialName("list_id")
    val externalId: Int?,
    @SerialName("created")
    val created: String?,
    @SerialName("updated")
    val updated: String?,
    @SerialName("item_count")
    val itemCount: Int?,
    @SerialName("user_id")
    val userId: Int?,
    @SerialName("layout_id")
    val layoutId: String?,
    @SerialName("name")
    val name: String?,
    @SerialName("is_starter_list")
    val isStarter: Boolean?,
    @SerialName("categories")
    var categories : List<ApiShoppingListCategory>? = emptyList(),
    @SerialName("legend")
    val legend : List<ApiLegendItems>
)

@Serializable
data class ApiShoppingListCategory(

    @SerialName("name")
    val name: String?,

    @SerialName("category_id")
    val categoryId: Long?,
    @SerialName("displayOrder")
    val displayOrder: Int?,
    @SerialName("items")
    val items: List<ApiShoppingListItem>
)

@Serializable
data class ApiShoppingListItem(
    @SerialName("item_id")
    val itemId: Long?,
    @SerialName("added")
    val added: String?,
    @SerialName("updated")
    val updated: String? = null,
    @SerialName("crossed_off")
    val crossedOff: String? = null,
    @SerialName("tag_name")
    val tagName: String?,
    @SerialName("used_count")
    val usedCount: Int?,
    @SerialName("source_keys")
    val sourceKeys: List<String>?,
    @SerialName("tag")
    val tag: ApiShoppingListTag,
)

@Serializable
data class ApiShoppingListTag(
    @SerialName("tag_id")
    val tagId: String?,
    @SerialName("name")
    val name: String?,
    @SerialName("tag_type")
    val tagType: String?,
    @SerialName("is_group")
    val isGroup: Boolean = false,

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
data class ApiShoppingListEmbedded  (
    @SerialName("_embedded")
    val embeddedList: ApiShoppingListResourceList
)
