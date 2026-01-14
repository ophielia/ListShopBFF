package com.listshop.bff.services

import com.listshop.bff.data.model.SemanticVersion
import com.listshop.bff.data.model.TagType
import com.listshop.bff.db.ListInfoEntity
import com.listshop.bff.db.TagEntity
import io.kotest.core.NamedTag
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class TagTreeTest {


    @Test
    fun blowUpTest() {
        val tagList = TestUtils.dummyTagStructure()
        val tagTree = TagTree(tagList)
        assertNotNull(tagTree)
    }

    // until we have access methods on the TagTree, no way to test much on this
}
