package com.listshop.bff.usecases.dashboard

import com.listshop.analytics.AnalyticsHandle
import com.listshop.analytics.debug
import com.listshop.bff.data.bff.BFFError
import com.listshop.bff.data.bff.BFFErrorSubtype
import com.listshop.bff.data.bff.BFFErrorType
import com.listshop.bff.data.bff.NewBFFResult
import com.listshop.bff.data.model.ShoppingListTag
import com.listshop.bff.services.TagService

class CreateTag(
    private val tagName: String,
    private val tagType: String,
    private val parentId: String,
    private val tagService: TagService,
    private val analyticsHandle: AnalyticsHandle
) {
    suspend fun process(): NewBFFResult<ShoppingListTag> {
        analyticsHandle.debug("CreateTag - begin use case")
        if (tagName.isBlank() || tagType.isBlank() || parentId.isBlank()) {
            val bfferror =
                BFFError(BFFErrorType.VALIDATION, BFFErrorSubtype.INVALID_INPUT, "name, type or parentId is blank")
            return NewBFFResult.Failure(bfferror)
        }
        try {
            val newTag = tagService.createTag(tagName, parentId, tagType)
            analyticsHandle.debug("CreateTag - end use case")
            return NewBFFResult(newTag)
        } catch (e: Exception) {
            analyticsHandle.listShopAnalytics.error("Error while creating tag")
            return NewBFFResult.Failure(rawErrorFromException(e))
        }

    }

}

