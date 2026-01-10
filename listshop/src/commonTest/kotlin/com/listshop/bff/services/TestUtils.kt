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
            --  -- vegetables 11 (group no tags)
            -- household 7
            --   -- cleaning 8
            --      -- spray 9
            --   --chair 10

             */
            val produce = buildTagEntity("produce", 1, 0, true)
            val fruit = buildTagEntity("fruit", 2, 1, true)
            val apples = buildTagEntity("apples", 4, 2, false)
            val banannas = buildTagEntity("banannas", 5, 2, false)
            val carrots = buildTagEntity("carrots", 6, 1, false)
            val household = buildTagEntity("household", 7, 0, true)
            val cleaning = buildTagEntity("cleaning", 8, 7, true)
            val spray = buildTagEntity("spray", 9, 8, false)
            val chair = buildTagEntity("chair", 10, 7, false)
            val vegetables = buildTagEntity("vegetables", 11, 1, true)
            val tagList = listOf(produce, fruit, apples, banannas, carrots, household, cleaning, spray, chair, vegetables)

            return tagList
        }

        fun dummyApiTagList(): List<ApiTag> {
            val tag1 = ApiTag("1", "tag1")
            val tag2 = ApiTag("2", "tag2")
            return listOf(tag1, tag2)
        }

        fun buildTagEntity(name: String, id: Long, parent: Long, isGroup: Boolean): TagEntity {
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
