package com.listshop.bff.data.model

import com.listshop.bff.data.remote.ApiTagLookup
import com.listshop.bff.db.TagLookupEntity

data class Tag(
    val externalId: String?,
    val name: String?,
    val parentId: String?,
    val tagType: String?,
) {
    companion object Factory {
        fun create(apiValue: ApiTagLookup): Tag {
            return Tag(
                apiValue.externalId,
                apiValue.name,
                apiValue.parentId,
                apiValue.tagType
            )
        }

        fun create(dbValue: TagLookupEntity): Tag {
            return Tag(
                dbValue.externalId,
                dbValue.name,
                dbValue.parentId,
                dbValue.tagType
            )
        }
    }

}

