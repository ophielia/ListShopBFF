package com.listshop.bff.services

import com.listshop.bff.data.model.ShoppingListTag
import com.listshop.bff.data.model.TagTreeDisplay
import com.listshop.bff.data.model.TagTreeNode
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
        stringLookupDictionary.entries.forEach { entry -> entry.value.processChildren() }
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
        groupsOnly: Boolean = false,
        tagTypes: List<TagType>? = null,
        showOnlyDirectChildren: Boolean = true
    ): List<TagTreeDisplay> {
        val contentNode = stringLookupDictionary[id] ?: return emptyList()

        if (id == BASE_GROUP_STRING) {
            return baseContentList(isAbbreviated, groupsOnly, tagTypes, showOnlyDirectChildren)
        }

        val childGroups = contentNode.groups.mapNotNull { it.display }
        var childTags = if (!groupsOnly) {
            contentNode.allChildren()
                .sortedWith(compareByDescending<TagTreeDisplay> { it.addedListCount }.thenBy { it.name })
        } else {
            emptyList()
        }

        if (isAbbreviated && childTags.isNotEmpty()) {
            val maxArray = minOf(ABBREVIATED_DISPLAY_COUNT, childTags.size)
            val limit = maxOf(0, maxArray - 1)
            val itemList = childTags.take(limit).toMutableList()
            if (maxArray >= ABBREVIATED_DISPLAY_COUNT) {
                itemList.add(TagTreeDisplay(name = "Show All", id = -1, tagType = TagType.EMPTY))
            }
            childTags = itemList
        }

        return childGroups + childTags
    }

    private fun baseContentList(
        isAbbreviated: Boolean,
        groupsOnly: Boolean,
        tagTypes: List<TagType>?,
        showOnlyDirectChildren: Boolean = true
    ): List<TagTreeDisplay> {
        val types = tagTypes ?: return emptyList()
        if (types.isEmpty()) return emptyList()

        val contentNode = stringLookupDictionary[BASE_GROUP_STRING] ?: return emptyList()

        // filter groups
        val childGroups = contentNode.groups
            .mapNotNull { it.display }
            .filter { types.contains(it.tagType) }

        // retrieve tags
        var childTags = if (!groupsOnly) {
            // add tags directly assigned to node
            val directTags = contentNode.tags
                .mapNotNull { it.display }
                .filter { types.contains(it.tagType) }

            val groupNodes = contentNode.groups.filter { node ->
                node.display?.let { types.contains(it.tagType) } ?: false
            }

            val descendantTags = if (!showOnlyDirectChildren) {
                groupNodes.flatMap { it.allChildren() }
            } else {
                emptyList()
            }

            (directTags + descendantTags)
                .sortedWith(compareByDescending<TagTreeDisplay> { it.addedListCount }.thenBy { it.name })
        } else {
            emptyList()
        }

        if (isAbbreviated && !showOnlyDirectChildren && childTags.isNotEmpty()) {
            val maxArray = minOf(ABBREVIATED_DISPLAY_COUNT, childTags.size)
            val limit = maxOf(0, maxArray - 1)
            val itemList = childTags.take(limit).toMutableList()
            if (maxArray >= ABBREVIATED_DISPLAY_COUNT) {
                itemList.add(TagTreeDisplay(name = "Show All", id = -1, tagType = TagType.EMPTY))
            }
            childTags = itemList
        }

        return childGroups + childTags
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
