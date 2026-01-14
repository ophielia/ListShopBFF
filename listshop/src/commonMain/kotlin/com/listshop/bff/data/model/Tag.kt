package com.listshop.bff.data.model

import com.listshop.bff.data.remote.ApiTag
import com.listshop.bff.db.TagEntity

data class Tag(
    val externalId: String?,
    val name: String?,
    val parentId: String?,
    val tagType: String?,
) {
    companion object Factory {
        fun create(apiValue: ApiTag): Tag {
            return Tag(
                apiValue.externalId,
                apiValue.name,
                apiValue.parentId,
                apiValue.tagType
            )
        }

        fun create(dbValue: TagEntity): Tag {
            return Tag(
                dbValue.externalId,
                dbValue.name,
                dbValue.parentId,
                dbValue.tagType
            )
        }
    }

}

