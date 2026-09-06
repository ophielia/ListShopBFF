package com.listshop.bff.data.model

import com.listshop.bff.db.TagEntity

class TagTreeDisplay(
    var name: String,
    var id: String,
    var isGroup: Boolean = false,
    // var isRating: Boolean = false,  //MM check for deletion
    var isUserTag: Boolean = false,
    var addedDishCount: Long = 0, //MM will be used for statistics, whenever that comes
    var addedListCount: Long = 0,//MM will be used for statistics, whenever that comes
    var tagType: TagType = TagType.EMPTY,
    var power: String = "0",  //MM also check for deletion - is this used??
) {
    companion object Factory {
        fun create(tag: TagEntity): TagTreeDisplay {
            return TagTreeDisplay(
                name = tag.name ?: "",
                id = tag.externalId!!,
                isGroup = tag.isGroup,
                isUserTag = tag.userId != null,
                tagType = TagType.fromDisplay(tag.tagType ?: "Empty") ?: TagType.EMPTY
            )
        }

        fun empty(): TagTreeDisplay {
            return TagTreeDisplay(
                name = "empty",
                id = "-99",
                isGroup = false,
                isUserTag = false,
                tagType =  TagType.EMPTY
            )
        }
    }


    fun updateFromTag(tag: TagEntity) {
        name = tag.name ?: ""
        id = tag.externalId!!
        //isGroup = tag.isGroup
        isUserTag = tag.userId != null
        tagType = TagType.fromDisplay(tag.tagType ?: "Empty") ?: TagType.EMPTY
    }

}

