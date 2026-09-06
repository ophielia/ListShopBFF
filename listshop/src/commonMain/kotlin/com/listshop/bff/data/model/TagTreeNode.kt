package com.listshop.bff.data.model

class TagTreeNode(
    var display: TagTreeDisplay?,
    var parentId: String,
    var rawChildren: List<TagTreeNode> = emptyList(),
    var groups: List<TagTreeNode> = emptyList(),
    var tags: List<TagTreeNode> = emptyList()

) {
    fun addChild(tagTreeNode: TagTreeNode) {
        rawChildren += tagTreeNode
    }

    private fun isGroup(): Boolean {
        return rawChildren.isNotEmpty()
    }

    fun processChildren() {
        if (rawChildren.isEmpty()) {
            return
        }
        display?.isGroup = true
        rawChildren.forEach { child ->
            if (child.isGroup()) {
                groups += child
            } else {
                tags += child
            }
        }
    }

    fun descendantGroups(tagTypes: List<TagType>?, descendantType: TagTreeDescendantType): MutableList<TagTreeDisplay> {
        val displays = mutableListOf<TagTreeDisplay>()

        groups.forEach { groupNode ->
            groupNode.display?.let { display ->
                if (tagTypes == null || tagTypes.contains(display.tagType)) {
                    displays.add(display)
                }
            }

            if (descendantType == TagTreeDescendantType.ALL) {
                displays.addAll(groupNode.descendantGroups(tagTypes, TagTreeDescendantType.ALL))
            }
        }

        return displays
    }

    fun descendantTags(tagTypes: List<TagType>?, descendantType: TagTreeDescendantType) : MutableList<TagTreeDisplay> {
        val displays = mutableListOf<TagTreeDisplay>()

        tags.forEach { tagNode ->
            tagNode.display?.let { display ->
                if (tagTypes == null || tagTypes.contains(display.tagType)) {
                    displays.add(display)
                }
            }
        }

        if (descendantType == TagTreeDescendantType.ALL) {
            groups.forEach { groupNode ->
                displays.addAll(groupNode.descendantTags(tagTypes, TagTreeDescendantType.ALL))
            }
        }

        return displays
    }

    companion object Factory {
        fun empty(): TagTreeNode {
            return TagTreeNode(
                display = null,
                parentId = "0",
                rawChildren = emptyList(),
                groups = emptyList(),
                tags = emptyList()
            )
        }
    }
}


