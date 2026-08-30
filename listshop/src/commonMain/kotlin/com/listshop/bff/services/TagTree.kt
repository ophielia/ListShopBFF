package com.listshop.bff.services

import com.listshop.bff.data.model.ShoppingListTag
import com.listshop.bff.data.model.TagTreeDescendantType
import com.listshop.bff.data.model.TagTreeDisplay
import com.listshop.bff.data.model.TagTreeNode
import com.listshop.bff.data.model.TagTreeNodeType
import com.listshop.bff.data.model.TagType
import com.listshop.bff.db.TagEntity

class TagTree() {
    var stringLookupDictionary = hashMapOf<String, TagTreeNode>()
    val BASE_GROUP = 0L
    val BASE_GROUP_STRING = "0"
    val ABBREVIATED_DISPLAY_COUNT = 15

    init {
        // will initialize private lookup dictionary to null
        stringLookupDictionary = hashMapOf()
    }

    // secondary constructor
    constructor(tagList: List<TagEntity>) : this() {
        // will fill in object based on passed list
        val baseTag = TagTreeDisplay(
            name = "All",
            id = BASE_GROUP,
            tagType = TagType.EMPTY
        )
        val baseNode = TagTreeNode(display = baseTag, parentId = "-99")
        stringLookupDictionary.put(BASE_GROUP_STRING, baseNode)

        for (tag in tagList) {
            val tagTreeNode = createOrUpdateNode(tag)
            // set node in parent
            addNodeToParent(tagTreeNode, tag.parentId ?: "-1")
        }

        // sift tags in each of the nodes
        siftNodes()
    }

    private fun siftNodes() {
        stringLookupDictionary.entries.forEach { entry -> entry.value.processChildren() }
        stringLookupDictionary.entries.forEach { entry -> entry.value.rawChildren = emptyList() }
    }

    fun append(tag: ShoppingListTag, parentId: String, tagType: TagType) {
        val parentIdAsLong = parentId.toLongOrNull() ?: -1L
        val tagIdAsLong = tag.externalId.toLongOrNull() ?: -1L
        if (parentIdAsLong < 0 ||
            tagIdAsLong < 0 ||
            !stringLookupDictionary.containsKey(parentId)
        ) {
            return
        }
        val newDisplay = TagTreeDisplay(
            name = tag.display,
            id = tagIdAsLong,
            isGroup = false,
            isUserTag = tag.isUser ?: false,
            tagType = tagType,
        )
        val newNode = TagTreeNode(newDisplay, parentId)
        stringLookupDictionary.put(tag.externalId, newNode)
        val parentNode = stringLookupDictionary.get(parentId)
        parentNode?.tags += newNode
    }

    fun isFilled(): Boolean {
        return stringLookupDictionary.size > 0
    }

    fun contentList(
        id: String,
        isAbbreviated: Boolean,
        nodeType: TagTreeNodeType = TagTreeNodeType.ALL,
        tagTypes: List<TagType>? = null,
        descendantType: TagTreeDescendantType = TagTreeDescendantType.ALL
    ): List<TagTreeDisplay> {
        val contentNode = stringLookupDictionary[id] ?: return emptyList()

        val simpleList : MutableList<TagTreeDisplay> = when (nodeType) {
            TagTreeNodeType.ALL -> {
                var allDisplays : MutableList<TagTreeDisplay>  = mutableListOf<TagTreeDisplay>()
                allDisplays.addAll(contentNode.descendantGroups(tagTypes, descendantType))
                allDisplays.addAll(contentNode.descendantTags(tagTypes, descendantType))
                return allDisplays
            }
            TagTreeNodeType.GROUPS_ONLY -> contentNode.descendantGroups(tagTypes, descendantType)
            TagTreeNodeType.TAGS_ONLY -> contentNode.descendantTags(tagTypes, descendantType)
        }

        // sort
        sortList(simpleList, nodeType)

        // abbreviate
        if (isAbbreviated && simpleList.isNotEmpty()) {
            val maxArray = minOf(ABBREVIATED_DISPLAY_COUNT, simpleList.size)
            val limit = maxOf(0, maxArray - 1)
            val itemList = simpleList.take(limit).toMutableList()
            if (maxArray >= ABBREVIATED_DISPLAY_COUNT) {
                itemList.add(TagTreeDisplay(name = "Show All", id = -1, tagType = TagType.EMPTY))
            }
            //simpleList = itemList
        }

        return simpleList
    }

    private fun sortList(
        simpleList: MutableList<TagTreeDisplay>,
        nodeType: TagTreeNodeType
    ) {
        if (nodeType.equals(TagTreeNodeType.TAGS_ONLY)) {
            simpleList.sortWith(compareByDescending<TagTreeDisplay> { it.addedListCount }.thenBy { it.name })
        } else {
            simpleList.sortWith(compareByDescending<TagTreeDisplay> { it.name })
        }
    }

    private fun addNodeToParent(tagTreeNode: TagTreeNode, parentId: String) {
        // get parent node
        if (!stringLookupDictionary.containsKey(parentId)) {
            stringLookupDictionary[parentId] = TagTreeNode.empty()
        }
        val parentNode = stringLookupDictionary[parentId]!!

        // add node to raw children
        parentNode.addChild(tagTreeNode)
    }

    private fun createOrUpdateNode(tag: TagEntity): TagTreeNode {
        val tagId = tag.externalId ?: "-1"
        val parentId = tag.parentId ?: "-1"
        if (stringLookupDictionary.containsKey(tagId)) {
            // node exists - update display
            var node = stringLookupDictionary[tagId]
            node?.display?.updateFromTag(tag)
            node?.parentId = parentId
        } else {
            // no node - create one
            val tagTreeDisplay = TagTreeDisplay.create(tag)
            val tagTreeNode = TagTreeNode(display = tagTreeDisplay, parentId = parentId)
            // set node in lookupDictionary
            stringLookupDictionary[tagId] = tagTreeNode
        }
        return stringLookupDictionary.get(tagId)!!
    }


}
