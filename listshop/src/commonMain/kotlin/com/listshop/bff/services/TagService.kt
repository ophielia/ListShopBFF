package com.listshop.bff.services

import com.listshop.bff.data.bff.BFFResult
import com.listshop.bff.data.model.ShoppingListTag

interface TagService {
    suspend fun retrieveTagsAndSaveLocally()
    suspend fun buildTagTree() : TagTree
    suspend fun createTag(tagName: String,   parentId: String, tagType: String): ShoppingListTag
    suspend fun clearUserTags()
    suspend fun updateTag(tagId: String, tagName: String): ShoppingListTag


}
