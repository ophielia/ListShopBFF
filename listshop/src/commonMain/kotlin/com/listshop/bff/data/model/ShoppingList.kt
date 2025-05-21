package com.listshop.bff.data.model

import com.listshop.bff.data.remote.ApiShoppingList

data class ShoppingList(
    var externalId: String?,
    val name: String?,
    // categories let categories: [ShoppingListCategory]?
    var created: String?,
    var updated: String?,
    var layoutId: String?,
    var itemCount: Int?,

    val isStarterList: Boolean?,
    // var legend: ShoppingListLegend?
    val loading: Boolean,
    val lastLocalChange: String?,
    val lastSynced: String?
) {
    companion object Factory {
        fun create(apiValue: ApiShoppingList): ShoppingList {
            return ShoppingList(
                apiValue.externalId.toString(),
                apiValue.name,
                apiValue.created,
                apiValue.updated,
                apiValue.layoutId,
                apiValue.itemCount,
                apiValue.isStarter,
                false,  // is loading
                null,
                null
            )
        }

        fun create(id: String, name: String): ShoppingList {
            return ShoppingList(
                id,
                name,
                null,
                null,
                null,
                null,
                null,
                false,
                null,
                null
            )
        }
    }

}

