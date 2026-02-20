package com.listshop.bff.usecases.dashboard

import com.listshop.analytics.AnalyticsHandle
import com.listshop.bff.data.bff.BFFError
import com.listshop.bff.data.bff.BFFErrorSubtype
import com.listshop.bff.data.bff.BFFErrorType
import com.listshop.bff.data.bff.BFFResult
import com.listshop.bff.data.model.ShoppingListTag
import com.listshop.bff.data.state.OnboardingViewState
import com.listshop.bff.data.state.TransitionViewState
import com.listshop.bff.services.TagService
import com.listshop.bff.services.UserService

class UpdateTag(
    private val tagId: String,
    private val tagName: String,
    private val tagService: TagService,
    private val analyticsHandle: AnalyticsHandle
) {
    suspend fun process(): BFFResult<ShoppingListTag> {
        if (tagName.isBlank() || tagId.isBlank()) {
            val bfferror = BFFError(BFFErrorType.DASHBOARD, BFFErrorSubtype.CALL_FAILED, "name or id is blank")
            return BFFResult.Companion.error(bfferror)
        }
        try {
            val newTag = tagService.updateTag(tagId, tagName)
            return BFFResult(newTag)
        } catch (e: Exception) {
            analyticsHandle.listShopAnalytics.error("Error while creating tag")
            val bfferror = BFFError(BFFErrorType.DASHBOARD, BFFErrorSubtype.CALL_FAILED, "unable to create tag")
            return BFFResult.Companion.error(bfferror)
        }
    }

}
