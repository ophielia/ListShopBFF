package com.listshop.bff.services.impl

import com.listshop.analytics.AppInfo
import com.listshop.analytics.ListShopAnalytics
import com.listshop.bff.data.remote.ApiTag
import com.listshop.bff.remote.TagApi
import com.listshop.bff.repositories.SessionInfoRepository
import com.listshop.bff.repositories.TagRepository
import com.listshop.bff.services.TagService

class TagServiceImpl internal constructor(
    private val sessionRepo: SessionInfoRepository,
    private val tagApi: TagApi,
    private val tagRepo: TagRepository,
    val appInfo: AppInfo,
    val listShopAnalytics: ListShopAnalytics? = null
) : TagService {

    override suspend fun retrieveTagsAndSaveLocally(): List<ApiTag> {
        // get the list of api tags
        val apiTags = tagApi.retrieveApiTags()

        // clear all current api tags
        tagRepo.deleteAll()

        // save api tags locally
        tagRepo.insertApiTagsLocally(apiTags)
        // return the api tags
        //MM not sure I need to return this....
        return apiTags
    }


}
