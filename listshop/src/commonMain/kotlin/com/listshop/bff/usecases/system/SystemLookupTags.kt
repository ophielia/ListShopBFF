package com.listshop.bff.usecases.system

import com.listshop.analytics.AnalyticsHandle
import com.listshop.analytics.debug
import com.listshop.analytics.error
import com.listshop.bff.data.bff.BFFError
import com.listshop.bff.data.bff.BFFResult
import com.listshop.bff.data.model.TagList
import com.listshop.bff.services.TagService

class SystemLookupTags(
    private val fragment: String,
    private val tagTypes: List<String>,
    private val excludeGroups: Boolean,
    private val tagService: TagService,
    private val analyticsHandle: AnalyticsHandle
) {

    suspend fun process(): BFFResult<TagList> {
        analyticsHandle.debug("SystemLookupTags - begin use case")


        try {
            val tags = tagService.searchTags(fragment, tagTypes, excludeGroups)
            analyticsHandle.debug("SystemLookupTags - end use case")
            return BFFResult(value = TagList(tags))
        } catch (e: Exception) {
            analyticsHandle.error("SystemLookupTags - error while searching tags - ${e.message}")
            return BFFError.errorFromException(e)
        }


    }


}
