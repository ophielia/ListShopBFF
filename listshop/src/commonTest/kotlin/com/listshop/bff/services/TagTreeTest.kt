package com.listshop.bff.services

import com.listshop.bff.data.model.SemanticVersion
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TagTreeTest {

    //MM START HERE!!!

    @Test
    fun testCreate() {
        val startString = "1.2.3"
        val semanticVersion = SemanticVersion.Factory.create(startString)
        assertEquals(1, semanticVersion.major)
        assertEquals(2, semanticVersion.minor)
        assertEquals(3, semanticVersion.patch)
    }

}
