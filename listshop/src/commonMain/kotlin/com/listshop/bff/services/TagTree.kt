package com.listshop.bff.services

import com.listshop.bff.db.TagEntity

public class TagTree() {

    init {
        // will initialize private lookup dictionary to null
    }

    // secondary constructor
    constructor(tagList: List<TagEntity>) : this() {
        // will fill in object based on passed list
        /*
        ios code
            func construct(from tagList: [TagLkupEntity]) {
        lookupDictionary = [:]
        let baseTag = TagTreeDisplay(name: "All", id: TagTree.BASE_GROUP, addedListCount: 0, addedDishCount: 0, tagType: "empty")
        let baseNode = TagTreeNode(display: baseTag, parentId: -99)
        lookupDictionary[TagTree.BASE_GROUP] = baseNode

        for tagLkup in tagList {
            let id = Int32(tagLkup.externalId)
            // make TagTreeDisplay
            let tagTreeDisplay = findOrCreateTagTreeDisplay(from: tagLkup)

            // set in dictionary
            let parentId = tagLkup.parentId
            if let existingNode = lookupDictionary[id] {
                existingNode.display = tagTreeDisplay
                existingNode.parentId = parentId
            } else {
                lookupDictionary[id] = TagTreeNode(display: tagTreeDisplay,
                        parentId: parentId)
            }

            // set in parent
            setTagDisplayInParent(childOptional: lookupDictionary[id], parentId: parentId)
        }

        // now, sift the tags in each of the nodes
        lookupDictionary.forEach { id, treeNode in
            treeNode.processChildren()
        }

    }
         */
    }

}
