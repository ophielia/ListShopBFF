package com.listshop.bff.usecases.dashboard

import com.listshop.analytics.AnalyticsHandle
import com.listshop.analytics.debug
import com.listshop.analytics.error
import com.listshop.bff.data.bff.BFFError
import com.listshop.bff.data.bff.BFFErrorSubtype
import com.listshop.bff.data.bff.BFFErrorType
import com.listshop.bff.data.bff.NewBFFResult
import com.listshop.bff.data.model.ShoppingListTag
import com.listshop.bff.services.TagService

class UpdateTag(
    private val tagId: String,
    private val tagName: String,
    private val tagService: TagService,
    private val analyticsHandle: AnalyticsHandle
) {
    suspend fun process(): NewBFFResult<ShoppingListTag> {
        analyticsHandle.debug("UpdateTag - begin use case")
        if (tagName.isBlank() || tagId.isBlank()) {
            val bfferror = BFFError(BFFErrorType.VALIDATION, BFFErrorSubtype.INVALID_INPUT, "name or id is blank")
            return NewBFFResult.Companion.error(bfferror)
        }
        try {
            val newTag = tagService.updateTag(tagId, tagName)
            analyticsHandle.debug("UpdateTag - end use case")
            return NewBFFResult(newTag)
        } catch (e: Exception) {
            analyticsHandle.error("Error while creating tag")
            return NewBFFResult.Failure(rawErrorFromException(e))
        }
    }

}
