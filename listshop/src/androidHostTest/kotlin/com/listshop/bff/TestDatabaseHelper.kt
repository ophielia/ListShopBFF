package com.listshop.bff

import com.listshop.bff.data.model.ShoppingList
import com.listshop.bff.data.model.Tag
import com.listshop.bff.db.ListCategoryEntity
import com.listshop.bff.db.ListInfoEntity
import com.listshop.bff.db.ListItemEntity
import com.listshop.bff.db.ShoppingListEntity
import com.listshop.bff.db.TagEntity
import com.listshop.bff.db.UserInfoEntity
import com.listshop.bff.db.UserPropertiesEntity
import com.listshop.bff.repositories.ListShopDatabase
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime

class TestDatabaseHelper(
    private val listShopDatabase: ListShopDatabase
) {

    fun clearDatabase() {
        listShopDatabase.db.userSessionDefinitionQueries.removeAllUserInfo()
        listShopDatabase.db.tagDefinitionQueries.removeAllTags()
        listShopDatabase.db.listDefinitionQueries.removeAllData()
        listShopDatabase.db.layoutDefinitionQueries.removeAllData()
    }

    fun standardUser():UserInfoEntity {
        val now = Clock.System.now()
        val created = LocalDateTime.parse("2017-02-15T18:00:00.000")
        val lastSeen = LocalDateTime.parse("2024-01-01T18:00:00.000")
        val lastSignedIn = LocalDateTime.parse("2024-07-01T18:00:00.000")

        return UserInfoEntity("user@the-list-shop.com",
            "a randomized token",
                    created.toString(),
            lastSeen.toString(),
            lastSignedIn.toString(),
            lastSignedIn.toString()
            )
    }

    fun standardListInfo(): ListInfoEntity {
        val now = Clock.System.now().toString()

        return ListInfoEntity(
            lastInternalUpdate = now,
            lastUpdate = now,
            localListUpdated = now,
            serverListId = "dummyId",
            lookupDataLastSynced = now,
            statisticsLastSynced = now,
            localLastSynced = now,
            serverListLastSynced = now
        )


    }

    fun setUser(user: UserInfoEntity?) {
        if (user == null) {
            return
        }
        listShopDatabase.db.userSessionDefinitionQueries.insertIntoUserInfo(
            user.userName,user.userToken, user.userInfoCreated, user.userLastSeen,user.userLastSignedIn, user.userCreatedOnServer
        )
    }

    fun setTag(tag: TagEntity?) {
        if (tag == null) {
            return
        }
        listShopDatabase.db.tagDefinitionQueries.insertIntoTag(
            tag.externalId,
            tag.isGroup,
            tag.name,
            tag.parentId,
            tag.power,
            tag.tagType,
            tag.userId)
    }

    fun setShoppingList(shoppingList: ShoppingList) {
        val listId = shoppingList.externalId ?: "0"
        var itemsByCategory = HashMap<ListCategoryEntity, List<ListItemEntity>>()

        // go through all categories, pulling items
        for (cat in shoppingList.categories) {
            val catId = cat.externalId.toString()
            val items = cat.items.map {
                ListItemEntity(
                    externalId = it.externalId.toString(),
                    categoryExternalId = catId,
                    tagExternalId = it.tag.externalId,
                    added = it.added,
                    removed = it.removed,
                    crossedOff = it.crossedOff,
                    updatedOn = it.updatedOn,
                    usedCount = it.usedCount.toLong(),
                    tagName = it.tag.display,
                    tagType = it.tag.tagType,
                    legendKeys = ""
                )
            }

            val categoryEntity =
                ListCategoryEntity(cat.name, cat.externalId.toString(), listId, cat.displayOrder.toLong())
            itemsByCategory.put(categoryEntity, items)
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
            layoutId = shoppingList.layoutId,
            isStarter = shoppingList.isStarterList ?: false,
        )
        setShoppingList(listEntity, itemsByCategory)

    }

    private fun setShoppingList(shoppingList: ShoppingListEntity?, itemsByCategory: Map<ListCategoryEntity, List<ListItemEntity>>) {
        if (shoppingList == null) {
            return
        }
        listShopDatabase.db.listDefinitionQueries.insertIntoShoppingListEntity(
            name = shoppingList.name,
            externalId = shoppingList.externalId,
            createdOn = shoppingList.createdOn,
            updatedOn = shoppingList.updatedOn,
            lastLocalChange = shoppingList.lastLocalChange,
            lastSync = shoppingList.lastSync,
            itemCount = shoppingList.itemCount,
            layoutId = shoppingList.layoutId,
            isStarter = shoppingList.isStarter
        )
        val savedListId = shoppingList.externalId ?: "0"
        itemsByCategory.entries.forEach { it ->
            listShopDatabase.db.listDefinitionQueries.insertIntoListCategoryEntity(
                name = it.key.name,
                externalId = it.key.externalId,
                listExternalId = savedListId,
                displayOrder = it.key.displayOrder
            )
            val categoryId = it.key.externalId ?: "0"
            it.value.forEach { item ->
                listShopDatabase.db.listDefinitionQueries.insertIntoListItemEntity(
                    externalId = item.externalId,
                    categoryExternalId = categoryId,
                    tagExternalId = item.tagExternalId,
                    added = item.added,
                    removed = item.removed,
                    crossedOff = item.crossedOff,
                    updatedOn = item.updatedOn,
                    usedCount = item.usedCount,
                    tagName = item.tagName,
                    tagType = item.tagType,
                    legendKeys = item.legendKeys
                )
            }
        }
    }

    fun setListInfo(list: ListInfoEntity?) {
        if (list == null) {
            return
        }
        listShopDatabase.db.userSessionDefinitionQueries.insertIntoListInfo(
            lastInternalUpdate = list.lastInternalUpdate,
            lastUpdate = list.lastUpdate,
            localListUpdated = list.localListUpdated,
            serverListId = list.serverListId,
            lookupDataLastSynced = list.lookupDataLastSynced,
            statisticsLastSynced = list.statisticsLastSynced,
            localLastSynced = list.localLastSynced,
            serverListLastSynced = list.serverListLastSynced
        )
    }

    fun getUserProperties(): List<UserPropertiesEntity> {
        return listShopDatabase.db.userSessionDefinitionQueries.selectAllUserProperties().executeAsList()
    }

    fun setUserProperty(key: String, value: String) {
        listShopDatabase.db.userSessionDefinitionQueries.insertIntoUserProperties(key, value)
    }

    fun currentListInfo() : List<ListInfoEntity>{
        return listShopDatabase.db.userSessionDefinitionQueries.selectAllListInfos().executeAsList()
    }
    fun currentUserInfo() : List<UserInfoEntity>{
        return listShopDatabase.db.userSessionDefinitionQueries.selectAllUserInfos().executeAsList()
    }

    fun setServerListId(listId: String) {
        listShopDatabase.db.userSessionDefinitionQueries.insertIntoListInfo(
            lastInternalUpdate = null,
            lastUpdate = null,
            localListUpdated = null,
            serverListId = listId,
            lookupDataLastSynced =null,
            statisticsLastSynced = null,
            localLastSynced = null,
            serverListLastSynced = null
        )
    }

     fun setTags(tags: List<Tag>) {
        tags.forEach { t ->
            listShopDatabase.db.tagDefinitionQueries.insertIntoTag(
                externalId = t.externalId,
                isGroup = false,
                name = t.name,
                parentId = t.parentId,
                power = "1",
                tagType = t.tagType,
                userId = null
            ) }
    }
}
