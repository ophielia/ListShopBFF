package com.listshop.bff.services

import com.listshop.bff.data.model.ShoppingListTag
import com.listshop.bff.data.model.TagTreeDisplay
import com.listshop.bff.data.model.TagTreeNode
import com.listshop.bff.data.model.TagType
import com.listshop.bff.db.TagEntity

class TagTree() {
    var lookupDictionary = hashMapOf<Long, TagTreeNode>()
    val BASE_GROUP = 0L

    init {
        // will initialize private lookup dictionary to null
        lookupDictionary = hashMapOf()
    }

    // secondary constructor
    constructor(tagList: List<TagEntity>) : this() {
        // will fill in object based on passed list
        val baseTag = TagTreeDisplay(
            name = "All",
            id = BASE_GROUP,
            tagType = TagType.EMPTY
        )
        val baseNode = TagTreeNode(display = baseTag, parentId = -99L)
        lookupDictionary.put(BASE_GROUP, baseNode)

        for (tag in tagList) {
            val parentId = tag.parentId?.toLong() ?: -1L
            val tagTreeNode = createOrUpdateNode(tag)
            // set node in parent
            addNodeToParent(tagTreeNode, parentId)
        }

        // sift tags in each of the nodes
        lookupDictionary.entries.forEach { entry -> entry.value.processChildren() }
    }

    fun append(tag: ShoppingListTag, parentId: String) {
        val parentIdAsLong = parentId.toLongOrNull() ?: -1L
        val tagIdAsLong = tag.externalId.toLongOrNull() ?: -1L
        if (parentIdAsLong < 0 ||
            tagIdAsLong < 0 ||
            !lookupDictionary.containsKey(parentIdAsLong)) {
            return
        }
        val newDisplay = TagTreeDisplay(
            name = tag.display,
            id = tagIdAsLong,
            isGroup = false,
            isUserTag = tag.isUser ?: false,
            tagType = TagType.valueOf(tag.tagType),
        )
        val newNode = TagTreeNode(newDisplay, parentIdAsLong)
        lookupDictionary.put(tagIdAsLong, newNode)
        val parentNode = lookupDictionary.get(parentIdAsLong)
        parentNode?.tags += newNode
    }

    fun isFilled() : Boolean {
        return lookupDictionary.size > 0
    }

    private fun addNodeToParent(tagTreeNode: TagTreeNode, parentId: Long) {
        // get parent node
        if (!lookupDictionary.containsKey(parentId)) {
            lookupDictionary[parentId] = TagTreeNode.empty()
        }
        val parentNode = lookupDictionary[parentId]!!

        // add node to raw children
        parentNode.addChild(tagTreeNode)
    }

    private fun createOrUpdateNode(tag: TagEntity): TagTreeNode {
        val tagId = tag.externalId?.toLong() ?: -1L
        val parentId = tag.parentId?.toLong() ?: -1L
        if (lookupDictionary.containsKey(tagId)) {
            // node exists - update display
            var node = lookupDictionary[tagId]
            node?.display?.updateFromTag(tag)
            node?.parentId = parentId
        } else {
            // no node - create one
            val tagTreeDisplay = TagTreeDisplay.create(tag)
            val tagTreeNode = TagTreeNode(display = tagTreeDisplay, parentId = parentId)
            // set node in lookupDictionary
            lookupDictionary[tagId] = tagTreeNode
        }
        return lookupDictionary.get(tagId)!!
    }


}
