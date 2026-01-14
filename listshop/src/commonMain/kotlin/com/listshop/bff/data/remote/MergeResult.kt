@file:OptIn(ExperimentalSerializationApi::class)

package com.listshop.bff.data.remote

import com.listshop.bff.data.model.ShoppingListItem
import com.listshop.bff.data.model.ShoppingListTag
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MergeResult(
    @SerialName("mergeResult")
    val mergeResult: ApiMergeShoppingList,

)


@Serializable
data class ApiMergeShoppingList(
    @SerialName("shoppingList")
    val shoppingList: ApiShoppingList,

    )

