package com.listshop.bff.ucp

import com.listshop.analytics.ListShopAnalytics
import com.listshop.bff.data.model.Tag
import com.listshop.bff.remote.TagApi
import com.listshop.bff.repositories.TagRepository

class TagUCP internal constructor(
    private val dataRepo: TagRepository,
    private val tagApi: TagApi,
    private val listShopAnalytics: ListShopAnalytics
) {

    @Throws(Exception::class) suspend fun getTags(forceReload: Boolean): List<Tag> {
        val cachedTags = dataRepo.selectAllTags()
        return if (cachedTags.isNotEmpty() && !forceReload) {
            cachedTags
        } else {
            tagApi.getAllTags().also {
                listShopAnalytics.tagsFetchedFromNetwork(it.size)
                dataRepo.deleteAll()
                dataRepo.insertTags(it)
            }
        }
    }
}


