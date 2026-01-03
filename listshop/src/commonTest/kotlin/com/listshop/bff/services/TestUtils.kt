package com.listshop.bff.services

import com.listshop.bff.data.remote.ApiTag
import com.listshop.bff.db.TagEntity

class TestUtils {


    companion object {
        fun dummyTagStructure(): List<TagEntity> {

            /*
            -- tags
            --produce 1
            --  -- fruit 2
            --    -- apples 4
            --    -- banannas 5
            --   -- carrots 6
            -- household 7
            --   -- cleaning 8
            --      -- spray 9
            --   --chair 10

             */
            var produce = buildTag("produce", 1, 0, true)
            var fruit = buildTag("fruit", 2, 1, true)
            var apples = buildTag("apples", 4, 2, false)
            var banannas = buildTag("banannas", 5, 2, false)
            var carrots = buildTag("carrots", 6, 1, false)
            var household = buildTag("household", 7, 0, true)
            var cleaning = buildTag("cleaning", 8, 7, true)
            var spray = buildTag("spray", 9, 8, false)
            var chair = buildTag("chair", 10, 7, false)
            var tagList = listOf(produce, fruit, apples, banannas, carrots, household, cleaning,spray, chair)

            return tagList
        }
        fun dummyApiTagList(): List<ApiTag> {
            val tag1 = ApiTag("1", "tag1")
            val tag2 = ApiTag("2", "tag2")
            return listOf(tag1, tag2)
        }

         fun buildTag(name: String, id: Long, parent: Long, isGroup: Boolean) : TagEntity {
            return TagEntity(
                externalId = id.toString(),
                isGroup = isGroup,
                name = name,
                parentId = parent.toString(),
                power = "0",
                tagType = "Ingredient",
                userId = null
            )
        }

    }


}
