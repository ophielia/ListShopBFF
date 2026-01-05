package com.listshop.bff.repositories.impl

import com.listshop.bff.data.model.ShoppingList
import com.listshop.bff.data.remote.ApiLayout
import com.listshop.bff.data.remote.ApiLayoutCategory
import com.listshop.bff.db.LayoutCategoryMappingEntity
import com.listshop.bff.db.ListCategoryEntity
import com.listshop.bff.db.ListItemEntity
import com.listshop.bff.db.ListshopDb
import com.listshop.bff.db.ShoppingListEntity
import com.listshop.bff.repositories.ListRepository
import com.listshop.bff.repositories.ListShopDatabase

class ListRepositoryImpl(
    private val listShopDatabase: ListShopDatabase
) : ListRepository {
    private val dbRef: ListshopDb = listShopDatabase.db

    override fun clearLocalListData() {
        dbRef.listDefinitionQueries.removeAllListItemEntities()
        dbRef.listDefinitionQueries.removeAllListCategoryEntities()
        dbRef.listDefinitionQueries.removeAllShoppingListEntities()
    }

    override fun saveListLocally(shoppingList: ShoppingList) {
        val listId = shoppingList.externalId ?: "0"
        // go through all categories, pulling items
        for (cat in shoppingList.categories) {
            val catId = cat.externalId.toString()
            val items = cat.items.map {
                ListItemEntity(
                    it.externalId.toString(), catId, it.tag.externalId, it.added,
                    it.removed, it.updatedOn, it.usedCount.toLong(), "")
            }
            insertListItems(items)
            val categoryEntity = ListCategoryEntity(cat.name, cat.externalId.toString(), listId, cat.displayOrder.toLong())
            insertListCategory( categoryEntity)
       }
        // save list
        val listEntity = ShoppingListEntity(
            name = shoppingList.name,
            externalId = listId,
            createdOn = shoppingList.created,
            updatedOn = shoppingList.updated,
            lastLocalChange = shoppingList.lastLocalChange,
            lastSync = shoppingList.lastSynced,
            itemCount = shoppingList.itemCount?.toLong(),
            layoutId = shoppingList.layoutId
        )
        insertList(listEntity)
    }

    private fun insertList(listEntity: ShoppingListEntity) {
        dbRef.listDefinitionQueries
            .insertIntoShoppingListEntity(
                listEntity.name,
                 listEntity.externalId,
                 listEntity.createdOn,
                 listEntity.updatedOn,
                 listEntity.lastLocalChange,
                 listEntity.lastSync,
                 listEntity.itemCount,
                 listEntity.layoutId
            )
    }

    private  fun insertListCategory(categoryEntity: ListCategoryEntity) {
        dbRef.listDefinitionQueries
            .insertIntoListCategoryEntity(
                name = categoryEntity.name,
                externalId = categoryEntity.externalId,
                listExternalId = categoryEntity.listExternalId,
                displayOrder = categoryEntity.displayOrder
            )
    }

    private fun insertListItems(items: List<ListItemEntity>) {
        dbRef.listDefinitionQueries.transaction {
            items.forEach { listItemEntity ->
                dbRef.listDefinitionQueries
                    .insertIntoListItemEntity(
                        externalId = listItemEntity.externalId,
                        categoryExternalId = listItemEntity.categoryExternalId,
                        tagExternalId = listItemEntity.tagExternalId,
                        added = listItemEntity.added,
                        removed = listItemEntity.removed,
                        updatedOn = listItemEntity.updatedOn,
                        usedCount = listItemEntity.usedCount,
                        legendKeys = listItemEntity.legendKeys
                    )
            }
        }
    }




}
