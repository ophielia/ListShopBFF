package com.listshop.bff.services.impl

import com.listshop.analytics.AppInfo
import com.listshop.analytics.ListShopAnalytics
import com.listshop.bff.data.model.TagType
import com.listshop.bff.remote.TagApi
import com.listshop.bff.repositories.SessionInfoRepository
import com.listshop.bff.repositories.TagRepository
import com.listshop.bff.services.TagService
import com.listshop.bff.services.TagTree

class TagServiceImpl internal constructor(
    private val sessionRepo: SessionInfoRepository,
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
        /*
                do {
                    // get tag lookups for all types
                    let allTagTypes = [TagType.Ingredient, TagType.NonEdible, TagType.DishType, TagType.Rating, TagType.TagType]

                    let lookups = try coreDataApi.findTags(with: allTagTypes)
                        // create tag tree
                        guard lookups.count > 0 else {
                            let lse = ListShopError(type: .service, title: "can't make a TagTree")
                            throw lse
                        }
                        // construct from tag lookups
                        let tagTree = TagTree()
                        tagTree.construct(from: lookups)
                        return Promise.value(tagTree)
                    } catch {
                        let lse = ListShopError(type: .service, title: "can't make a TagTree")
                        throw lse
                    }
                    */
        return tagTree
    }

}
