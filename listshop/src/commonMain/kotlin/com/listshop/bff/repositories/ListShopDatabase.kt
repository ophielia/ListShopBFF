package com.listshop.bff.repositories

import app.cash.sqldelight.ColumnAdapter
import app.cash.sqldelight.db.SqlDriver
import com.listshop.analytics.ListShopAnalytics
import com.listshop.bff.db.*

class ListShopDatabase(
    sqlDriver: SqlDriver,
    val analytics: ListShopAnalytics,
) {
    private val intAdapter = object : ColumnAdapter<Int, Long> {
        override fun decode(databaseValue: Long): Int = databaseValue.toInt()
        override fun encode(value: Int): Long = value.toLong()
    }

    val db: ListshopDb = ListshopDb(
        driver = sqlDriver,
        ListCategoryEntityAdapter = ListCategoryEntity.Adapter(
            displayOrderAdapter = intAdapter
        ),
        ListItemDetailEntityAdapter = ListItemDetailEntity.Adapter(
            wholeQuantityAdapter = intAdapter
        ),
        ListItemEntityAdapter = ListItemEntity.Adapter(
            usedCountAdapter = intAdapter,
            wholeQuantityAdapter = intAdapter
        ),
        ShoppingListEntityAdapter = ShoppingListEntity.Adapter(
            itemCountAdapter = intAdapter
        )
    )
}
