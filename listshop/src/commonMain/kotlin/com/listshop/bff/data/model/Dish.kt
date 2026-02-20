package com.listshop.bff.data.model

import com.listshop.bff.data.remote.ApiDish

data class Dish(
    val externalId: String,
    val name: String
) {
    companion object Factory {
        fun create(apiValue: ApiDish): Dish {
            return Dish(
                externalId = apiValue.externalId.toString(),
                name = apiValue.name ?: ""
            )
        }
    }
}
