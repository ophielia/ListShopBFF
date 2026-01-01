package com.listshop.bff.data.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SemanticVersionTest {

    @Test
    fun testCreate() {
        val startString = "1.2.3"
        val semanticVersion = SemanticVersion.create(startString)
        assertEquals(1, semanticVersion.major)
        assertEquals(2, semanticVersion.minor)
        assertEquals(3, semanticVersion.patch)
    }

    @Test
    fun testGreaterThanWhenEqual() {
        val startString = "1.2.3"
        val comparison = "1.2.3"
        val semanticVersion = SemanticVersion.create(startString)
        val comparisonVersion = SemanticVersion.create(comparison)
        assertTrue(SemanticVersion.isGreaterThanOrEquals(semanticVersion, comparisonVersion))
    }

    @Test
    fun testGreaterThanWhenGreater() {
        val startString = "1.2.3"
        val comparison = "0.2.3"
        val semanticVersion = SemanticVersion.create(startString)
        val comparisonVersion = SemanticVersion.create(comparison)
        assertTrue(SemanticVersion.isGreaterThanOrEquals(semanticVersion, comparisonVersion))

        val minor = "1.0.3"
        val minorComparison = SemanticVersion.create(minor)
        assertTrue(SemanticVersion.isGreaterThanOrEquals(semanticVersion, minorComparison))

        val patch = "1.2.2"
        val patchComparison = SemanticVersion.create(patch)
        assertTrue(SemanticVersion.isGreaterThanOrEquals(semanticVersion, patchComparison))

    }
}
