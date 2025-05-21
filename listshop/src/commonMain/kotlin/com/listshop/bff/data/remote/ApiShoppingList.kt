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
    val isStarter: Boolean?
    //let legend: [ApiLegend]?
    //case legend = "legend


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
data class ApiShoppingListEmbedded(
    @SerialName("_embedded")
    val embeddedList: ApiShoppingListResourceList
)