package com.listshop.bff.services.impl

import com.listshop.analytics.AppInfo
import com.listshop.analytics.ListShopAnalytics
import com.listshop.bff.data.model.ShoppingListTag
import com.listshop.bff.data.model.Tag
import com.listshop.bff.data.model.TagType
import com.listshop.bff.data.remote.PostApiTag
import com.listshop.bff.data.remote.PutApiTag
import com.listshop.bff.db.TagEntity
import com.listshop.bff.exceptions.InternalDataException
import com.listshop.bff.exceptions.LoggedOutException
import com.listshop.bff.remote.TagApi
import com.listshop.bff.repositories.TagRepository
import com.listshop.bff.services.LayoutService
import com.listshop.bff.services.SessionService
import com.listshop.bff.services.TagService
import com.listshop.bff.services.TagTree

class TagServiceImpl internal constructor(

    private val tagApi: TagApi,
    private val tagRepo: TagRepository,
    private val layoutService: LayoutService,
    private val sessionService: SessionService,
    val appInfo: AppInfo,
    val listShopAnalytics: ListShopAnalytics? = null
) : TagService {

    override suspend fun retrieveTagsAndSaveLocally() {
        // get the list of api tags
        val apiTags = tagApi.retrieveApiTags()

        // clear all current api tags
        tagRepo.deleteAll()

        // save api tags locally
        tagRepo.insertApiTagsLocally(apiTags)
    }

    override suspend fun buildTagTree(): TagTree {
        val typesForTreeAsStrings = TagType.entries.map { it.display }
        // get the saved tags
        val lookups = tagRepo.findTagsByTypes(typesForTreeAsStrings)
        val tagTree = TagTree(lookups)

        return tagTree
    }

    override suspend fun createTag(tagName: String,
                                   parentId: String,
                                   tagType: String): ShoppingListTag {
        // if user isn't logged in, stop everything right here
        checkUserLoggedIn()

        // create payload from parameters
        val postApiTag = PostApiTag(name=tagName,
            parentId = parentId,
            tagType = tagType)
        // call api
        val newTagId = tagApi.createTag(postApiTag)

        // retrieve new tag
        val newTag = tagApi.retrieveTag(newTagId)
        // set parentid in tag (workaround, while endpoint still isnt returning parent id for single tag
        newTag.parentId = parentId

        // save tag locally
        tagRepo.insertApiTagsLocally(listOf(newTag))
        // reload user layouts
        //MM - once we have an endpoint, we can change this so that we only add the tag to the layout
        // AND / OR - change this so that it runs in the background
        // fixing this - LS-2323
        layoutService.retrieveLayoutsAndSaveLocally()

        return ShoppingListTag.create(newTag, true)
    }

    override suspend fun updateTag(tagId: String, tagName: String) : ShoppingListTag {
        // if user isn't logged in, stop everything right here
        checkUserLoggedIn()
        // get parent id
        val originalTag = getOriginalTagFromLocal(tagId)
        val parentId = originalTag.parentId ?: ""
        if (parentId.isEmpty()) {
           throw InternalDataException("Parent id not found for tag $tagId")
        }
        // create payload from parameters
        val putApiTag = PutApiTag(name = tagName)
        // call api
        tagApi.updateTag(tagId,putApiTag)

        // retrieve tag
        val updatedTag = tagApi.retrieveTag(tagId)
        // set parentid in tag (workaround, while endpoint still isnt returning parent id for single tag
        updatedTag.parentId = parentId

        // save tag locally
        tagRepo.updateApiTagLocally(tagId, tagName)

        // reload user layouts
        //MM - once we have an endpoint, we can change this so that we only add the tag to the layout
        // AND / OR - change this so that it runs in the background
        // fixing this - LS-2323
        layoutService.retrieveLayoutsAndSaveLocally()

        return ShoppingListTag.create(updatedTag, true)
    }

    override suspend fun searchTags(
        fragment: String,
        tagTypes: List<String>,
        excludeGroups: Boolean
    ): List<Tag> {
        val lowerCaseFragment = fragment.lowercase()
        val tagEntities = tagRepo.searchTags(lowerCaseFragment, tagTypes, excludeGroups)
        return tagEntities.map { Tag.create(it) }
    }

    private suspend fun getOriginalTagFromLocal(tagId: String) : TagEntity {
        try {
            return tagRepo.retrieveTagLocally(tagId)
        } catch (e: Exception) {
            throw InternalDataException("Tag $tagId not found locally")
        }

    }

    private fun checkUserLoggedIn() {
        if (sessionService.currentUserSession().userToken == null) {
            throw LoggedOutException("User cannot perform tag operation when not user is not logged in")
        }
    }

    override suspend fun clearUserTags() {
        // clear existing tags and replace with standard tags
        retrieveTagsAndSaveLocally()
    }
}
