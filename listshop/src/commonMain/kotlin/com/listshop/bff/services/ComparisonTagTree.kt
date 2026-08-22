package com.listshop.bff.services

import com.listshop.bff.data.model.ShoppingListTag
import com.listshop.bff.data.model.TagTreeDisplay
import com.listshop.bff.data.model.TagTreeNode
import com.listshop.bff.data.model.TagType
import com.listshop.bff.db.TagEntity

class ComparisonTagTree() {
    var stringLookupDictionary = hashMapOf<String, TagTreeNode>()
    val BASE_GROUP = 0L
    val BASE_GROUP_STRING = "0"

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




    /*

    static let BASE_GROUP: Int32 = 0
    static let ABBREVIATED_DISPLAY_COUNT: Int = 15

    var lookupDictionary: [Int32: TagTreeNode]

    override init() {
        lookupDictionary = [:]
    }

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

    func isFilled() -> Bool {
        lookupDictionary.keys.count > 1
    }

    func append(tag: ShoppingListTag, parentId: String) {
        guard let intId = Int32(tag.externalId),
              let parentIntId = Int32(parentId),
              lookupDictionary[intId] == nil,
              let parentNode = lookupDictionary[parentIntId]
        else {
            return
        }
        // make tagTreeDisplay
        let newTagDisplay = TagTreeDisplay(
                name: tag.display ,
                id: intId,
                addedListCount: 0,
                addedDishCount: 0,
                tagType: tag.tagType )

        // make node
        let newNode = TagTreeNode(display: newTagDisplay, parentId: parentIntId)
        // set in dictionary
        lookupDictionary[intId] = newNode

        // set in parent (assuming non-group (leaf not node) for now)
        parentNode.tags.append(newNode)
        // not raw children - but like in process children


    }

    func update(display: TagTreeDisplay) {
        let id = display.id
        guard let nodeToUpdate = lookupDictionary[id] else {
            // cant find tag to update
            return
        }
        nodeToUpdate.display = display
    }

    private func setTagDisplayInParent(childOptional: TagTreeNode?, parentId: Int32) {
        guard let child = childOptional else {
            return
        }
        // if doesn't exist - create new node
        if lookupDictionary[parentId] == nil {
            lookupDictionary[parentId] = TagTreeNode()
        }

        // pull parent node
        guard let parentNode = lookupDictionary[parentId] else {
            //MM error
            return
        }
        // add child tag
        parentNode.addChild(childNode: child)
        // if display for node doesn't exist, create one
        if parentNode.display == nil {
            parentNode.display = TagTreeDisplay()
        }
        // set node in dictionary
        lookupDictionary[parentId] = parentNode
    }

    private func findOrCreateTagTreeDisplay(from lookupTag: TagLkupEntity) -> TagTreeDisplay {
        let id = Int32(lookupTag.externalId)

        if let node = lookupDictionary[id] {
            if let display = node.display {
                display.update(from: lookupTag)
                return display
            } else {
                let display = TagTreeDisplay(tagObject: lookupTag)
                node.display = display
                return display
            }
            return node.display!
        }
        return TagTreeDisplay(tagObject: lookupTag)
    }

    func navigationList(for id: Int32, ignoreBase: Bool = true) -> [TagTreeDisplay] {
        if id == TagTree.BASE_GROUP && ignoreBase {
            return []
        }

        guard let navForNode = lookupDictionary[id] else {
            // MM error
            return []
        }

        if id == TagTree.BASE_GROUP {
            return [navForNode.display ?? TagTreeDisplay()]
        }

        var navigationList = [TagTreeDisplay]()

        var parentId = navForNode.display?.id ?? 0
        repeat {
            // get the node of parent id
            if let nextNode = lookupDictionary[parentId] {
                // add the display at the beginning of the array
                navigationList.insert(nextNode.display ?? TagTreeDisplay(), at: 0)
                // set the parent id
                parentId = nextNode.parentId ?? 0

            }
        } while parentId >= 0

        return navigationList
    }

    func contentList(for id: Int32, isAbbreviated: Bool, groupsOnly: Bool = false, tagTypes: [TagType]? = nil, showOnlyDirectChildren: Bool = true) -> [TagTreeDisplay] {
        guard let contentNode = lookupDictionary[id] else {
            os_log("Error in tag tree. Requested id [%d] not available in tree.", log: Log.model, type: .error, id)
            return []
        }

        if id == TagTree.BASE_GROUP {
            return baseContentList(isAbbreviated: isAbbreviated, groupsOnly: groupsOnly, tagTypes: tagTypes, showOnlyDirectChildren: showOnlyDirectChildren)
        }

        var childTags = [TagTreeDisplay]()
        var childGroups = [TagTreeDisplay]()

        childGroups.append(contentsOf: contentNode.groups.map { (node: TagTreeNode) -> TagTreeDisplay in
            node.display ?? TagTreeDisplay()
        })
        if !groupsOnly {
            childTags.append(contentsOf: contentNode.allChildren())

            // sort tags
            childTags.sort {
                if $0.addedListCount != $1.addedListCount { // first, compare by list count
                    return ($0.addedListCount) > ($1.addedListCount)
                } else { // All other fields are tied, break ties by  name
                    return ($0.name) < ($1.name)
                }
            }

            if isAbbreviated {
                var maxArray = min(TagTree.ABBREVIATED_DISPLAY_COUNT, childTags.count)
                maxArray = max(0, maxArray - 1)
                var itemList = Array(childTags[0..<maxArray])
                if (maxArray >= TagTree.ABBREVIATED_DISPLAY_COUNT - 1) {
                    itemList.append(TagCollectionHelper.SHOW_ALL_DISPLAY)
                }
                childTags = itemList
            }
        }
        return childGroups + childTags
    }

    func allUserDisplays() -> [TagTreeDisplay] {
        lookupDictionary.values.filter {
            $0.display?.isUserTag ?? false
        }
        .compactMap {
            $0.display
        }
        .sorted{ itemA, itemB in
            itemA.name   < itemB.name
        }
    }

    public func display(for id: Int32) -> TagTreeDisplay? {
        let node = lookupDictionary[id]
        if node == nil {
            return nil
        }
        return node?.display
    }

    public func displayByName(for name: String) -> [TagTreeDisplay] {
        lookupDictionary.values.filter {
                    $0.display?.name.lowercased() == name.lowercased()
                }
                .flatMap {
                    $0.display
                }

    }

    public func displayContains(_ text: String) -> [TagTreeDisplay] {
        lookupDictionary.values.filter {
                    ($0.display?.name.lowercased().contains(text.lowercased()) ?? false)
                }
                .flatMap {
                    $0.display
                }

    }

    private func baseContentList(isAbbreviated: Bool, groupsOnly: Bool, tagTypes: [TagType]?, showOnlyDirectChildren: Bool = true) -> [TagTreeDisplay] {
        guard let types = tagTypes,
              types.count > 0
        else {
            os_log("TagTree - content request base without TagTypes", log: Log.model, type: .error)
            return []
        }
        guard let contentNode = lookupDictionary[TagTree.BASE_GROUP] else {
            os_log("Error in tag tree. BASE_GROUP not available in tree.", log: Log.model, type: .error)
            return []
        }

        var childTags = [TagTreeDisplay]()
        var childGroups = [TagTreeDisplay]()

        // convert tagType array of enums to strings
        let targetTypes = types.map { (typeEnum: TagType) -> String in
            typeEnum.rawValue
        }

        // filter groups
        childGroups.append(contentsOf: contentNode.groups.map({ (node: TagTreeNode) -> TagTreeDisplay in
            node.display ?? TagTreeDisplay()
        }).filter({ (tagDisplay: TagTreeDisplay) in
            targetTypes.contains(tagDisplay.tagType)
        }
        ))
        // retrieve tags
        if !groupsOnly {
            // add tags directly assigned to node
            childTags.append(contentsOf: contentNode.tags.map({ (node: TagTreeNode) -> TagTreeDisplay in
                node.display ?? TagTreeDisplay()
            }).filter({ (tagDisplay: TagTreeDisplay) in
                targetTypes.contains(tagDisplay.tagType)
            }
            ))

            let groupNodes = contentNode.groups.filter({ (tagNode: TagTreeNode) in
                targetTypes.contains((tagNode.display?.tagType) ?? "empty")
            }
            )

            if !showOnlyDirectChildren {
                groupNodes.forEach { (tagGroup: TagTreeNode) in
                    childTags += tagGroup.allChildren()
                }
            }

            // sort tags
            childTags.sort {
                if $0.addedListCount != $1.addedListCount { // first, compare by list count
                    return ($0.addedListCount) > ($1.addedListCount)
                } else { // All other fields are tied, break ties by  name
                    return ($0.name) < ($1.name)
                }
            }

            if isAbbreviated && !showOnlyDirectChildren {
                var maxArray = min(TagTree.ABBREVIATED_DISPLAY_COUNT, childTags.count)
                maxArray = max(0, maxArray - 1)
                var itemList = Array(childTags[0..<maxArray])
                if (maxArray >= TagTree.ABBREVIATED_DISPLAY_COUNT - 1) {
                    itemList.append(TagCollectionHelper.SHOW_ALL_DISPLAY)
                }
                childTags = itemList
            }
        }
        return childGroups + childTags
    }


     */


}
