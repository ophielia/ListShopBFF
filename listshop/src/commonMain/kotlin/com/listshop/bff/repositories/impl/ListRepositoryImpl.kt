package com.listshop.bff.repositories.impl

import com.listshop.bff.data.model.ShoppingList
import com.listshop.bff.data.model.ShoppingListCategory
import com.listshop.bff.data.model.ShoppingListDetail
import com.listshop.bff.data.model.ShoppingListItem
import com.listshop.bff.db.ListCategoryEntity
import com.listshop.bff.db.ListItemDetailEntity
import com.listshop.bff.db.ListItemEntity
import com.listshop.bff.db.ListshopDb
import com.listshop.bff.db.ShoppingListEntity
import com.listshop.bff.repositories.ListRepository
import com.listshop.bff.repositories.ListShopDatabase
import com.listshop.bff.services.SessionService
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class ListRepositoryImpl(
    private val listShopDatabase: ListShopDatabase,
    private val sessionService: SessionService
) : ListRepository {
    private val dbRef: ListshopDb = listShopDatabase.db

    private fun clearLocalListData() {
        dbRef.listDefinitionQueries.removeAllListItemDetailEntities()
        dbRef.listDefinitionQueries.removeAllListItemEntities()
        dbRef.listDefinitionQueries.removeAllListCategoryEntities()
        dbRef.listDefinitionQueries.removeAllShoppingListEntities()
    }

    override fun retrieveLocalList(): ShoppingList? {
        val localLists = dbRef.listDefinitionQueries.selectAllLists().executeAsList()
        if (localLists.isEmpty()) {
            return null;
        }
        val localList = localLists.first()
        val localListId = localList.externalId
        // get categories
        val categories = dbRef.listDefinitionQueries.selectAllCategoriesForList(localListId).executeAsList()
        val modelCategories = mutableListOf<ShoppingListCategory>()
        for (category in categories) {
            val items =
                dbRef.listDefinitionQueries.selectAllItemsForCategory(category.externalId ?: "0").executeAsList()
            modelCategories.add(ShoppingListCategory.create(category, items))
        }

        return ShoppingList.create(localList, modelCategories)
    }

    override fun retrieveOrCreateLocalList(): ShoppingList {
        val localLists = dbRef.listDefinitionQueries.selectAllLists().executeAsList()
        if (localLists.isEmpty()) {
            return createAndSaveLocalList()
        }
        val localList = localLists.first()
        val localListId = localList.externalId
        // get categories
        val categories = dbRef.listDefinitionQueries.selectAllCategoriesForList(localListId).executeAsList()
        val modelCategories = mutableListOf<ShoppingListCategory>()
        for (category in categories) {
            val items =
                dbRef.listDefinitionQueries.selectAllItemsForCategory(category.externalId ?: "0").executeAsList()
            modelCategories.add(ShoppingListCategory.create(category, items))
        }

        return ShoppingList.create(localList, modelCategories)
    }

    override fun createAndSaveLocalList(): ShoppingList {
        // get empty list
        val shoppingList: ShoppingList = ShoppingList.empty()
        // add created, set session
        val now = Clock.System.now().toLocalDateTime(TimeZone.UTC).toString()
        shoppingList.created = now
        shoppingList.updated = now
        sessionService.setLocalListUpdated(now)
        // save list
        saveListLocally(shoppingList)
        // return
        return shoppingList
    }

    override fun deleteLocalList(){
        clearLocalListData()
    }

    override fun setIdInLocalList(listId: String) {
        dbRef.listDefinitionQueries.updateListId(listId)
    }

    override fun saveListLocally(shoppingList: ShoppingList) {
        clearLocalListData()
        val listId = shoppingList.externalId ?: "0"
        // go through all categories, pulling items
        for (cat in shoppingList.categories) {
            val catId = cat.externalId
            var detailList = mutableListOf<ListItemDetailEntity>();
            val items = cat.items.map {mapShoppingListItem(it, catId, detailList)}

            insertListItems(items)
            insertListItemDetails(detailList)
            val categoryEntity =
                ListCategoryEntity(cat.name, cat.externalId.toString(), listId, cat.displayOrder)
            insertListCategory(categoryEntity)
        }
        // save list
        val listEntity = ShoppingListEntity(
            name = shoppingList.name,
            externalId = listId,
            createdOn = shoppingList.created,
            updatedOn = shoppingList.updated,
            lastLocalChange = shoppingList.lastLocalChange,
            lastSync = shoppingList.lastSynced,
            itemCount = shoppingList.itemCount,
            layoutId = shoppingList.layoutId,
            isStarter = shoppingList.isStarterList ?: false,
        )
        insertList(listEntity)
    }

    private fun mapShoppingListItem(it: ShoppingListItem, catId: String, detailList: MutableList<ListItemDetailEntity>): ListItemEntity {
        val itemId = it.externalId
        val details = it.details.map{ mapShoppingListItemDetail(it,itemId) }
        detailList.addAll(details)
        return ListItemEntity(
            externalId = it.externalId,
            categoryExternalId = catId,
            tagExternalId = it.tag.externalId,
            added = it.added,
            removed = it.removed,
            crossedOff = it.crossedOff,
            updatedOn = it.updatedOn,
            usedCount = it.usedCount,
            tagName = it.tag.display,
            quantity = it.amount?.quantity,
            wholeQuantity = it.amount?.wholeQuantity,
            roundedQuantity = it.amount?.roundedQuantity,
            quantityDisplay = it.amount?.quantityDisplay,
            unitId = it.amount?.unitId,
            unitDisplay = it.amount?.unitDisplay,
            amountDisplay = it.amount?.amountDisplay,
            tagType = "",  //MM REMOVE IF POSSIBLE
            legendKeys = it.legendKeys.joinToString(",")
        )
    }

    private fun mapShoppingListItemDetail(it: ShoppingListDetail, itemId: String): ListItemDetailEntity {
        return ListItemDetailEntity(
            itemExternalId = itemId,
            dishId = it.dishId,
            listId = it.listId,
            containsUnspecified = it.containsUnspecified,
            quantity = it.amount?.quantity,
            quantityDisplay = it.amount?.quantityDisplay,
            unitId = it.amount?.unitId,
            unitDisplay = it.amount?.unitDisplay,
            amountDisplay = it.amount?.amountDisplay,
            wholeQuantity = it.amount?.wholeQuantity,
            roundedQuantity = it.amount?.roundedQuantity
        )
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
                listEntity.layoutId,
                listEntity.isStarter,
            )
    }

    private fun insertListCategory(categoryEntity: ListCategoryEntity) {
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
                        crossedOff = listItemEntity.crossedOff,
                        usedCount = listItemEntity.usedCount,
                        tagName = listItemEntity.tagName,
                        quantity = listItemEntity.quantity,
                        wholeQuantity = listItemEntity.wholeQuantity,
                        roundedQuantity = listItemEntity.roundedQuantity,
                        quantityDisplay = listItemEntity.quantityDisplay,
                        unitId = listItemEntity.unitId,
                        unitDisplay = listItemEntity.unitDisplay,
                        amountDisplay = listItemEntity.amountDisplay,
                        tagType = listItemEntity.tagType,
                        legendKeys = listItemEntity.legendKeys,
                    )
            }
        }
    }

    private fun insertListItemDetails(details: List<ListItemDetailEntity>) {
        dbRef.listDefinitionQueries.transaction {
            details.forEach { detailEntity ->
                dbRef.listDefinitionQueries
                    .insertIntoListItemDetailEntity(
                        itemExternalId = detailEntity.itemExternalId,
                        dishId = detailEntity.dishId,
                        listId = detailEntity.listId,
                        containsUnspecified = detailEntity.containsUnspecified,
                        quantity = detailEntity.quantity,
                        wholeQuantity = detailEntity.wholeQuantity,
                        roundedQuantity = detailEntity.roundedQuantity,
                        quantityDisplay = detailEntity.quantityDisplay,
                        unitId = detailEntity.unitId,
                        unitDisplay = detailEntity.unitDisplay,
                        amountDisplay = detailEntity.amountDisplay
                    )
            }
        }
    }


}
