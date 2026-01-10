package com.listshop.bff.data.model

class TagTreeNode(
    var display: TagTreeDisplay?,
    var parentId: Long,
    var rawChildren: List<TagTreeNode> = emptyList(),
    var groups: List<TagTreeNode> = emptyList(),
    var tags: List<TagTreeNode> = emptyList()

) {
    fun addChild(tagTreeNode: TagTreeNode) {
        rawChildren += tagTreeNode
    }

    private fun isGroup(): Boolean {
        if (display == null) {
            return false
        }
        return display!!.isGroup
    }

    fun processChildren() {
        if (rawChildren.isEmpty()) {
            return
        }
        rawChildren.forEach { child ->
            if (child.isGroup()) {
                groups += child
            } else {
                tags += child
            }
        }
        // clear raw children
        rawChildren = emptyList()
    }

    companion object Factory {
        fun empty(): TagTreeNode {
            return TagTreeNode(
                display = null,
                parentId = 0,
                rawChildren = emptyList(),
                groups = emptyList(),
                tags = emptyList()
            )
        }
    }
}

