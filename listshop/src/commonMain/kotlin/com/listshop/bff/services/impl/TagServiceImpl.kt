package com.listshop.bff.services.impl

import com.listshop.analytics.AppInfo
import com.listshop.analytics.ListShopAnalytics
import com.listshop.bff.data.model.TagType
import com.listshop.bff.remote.TagApi
import com.listshop.bff.repositories.TagRepository
import com.listshop.bff.services.TagService
import com.listshop.bff.services.TagTree

class TagServiceImpl internal constructor(

    private val tagApi: TagApi,
    private val tagRepo: TagRepository,
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

}
