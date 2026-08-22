package com.listshop.bff.services.impl

import com.listshop.analytics.Analytics
import com.listshop.analytics.initDummyAnalytics
import com.listshop.bff.data.model.TagTreeDescendantType
import com.listshop.bff.data.model.TagTreeNodeType
import com.listshop.bff.data.model.TagType
import com.listshop.bff.data.remote.ApiTag
import com.listshop.bff.db.TagEntity
import com.listshop.bff.remote.ShoppingListApi
import com.listshop.bff.repositories.ListRepository
import com.listshop.bff.services.SessionService
import com.listshop.bff.services.TagTree
import dev.mokkery.mock
import kotlinx.coroutines.test.runTest
import kotlin.test.*

class TagTreeTest {
    private val remoteApi = mock<ShoppingListApi>()
    private val listRepo = mock<ListRepository>()
    private val sessionService = mock<SessionService>()
    private val analytics = mock<Analytics>()
    private val listShopAnalytics = initDummyAnalytics(analytics).listShopAnalytics

    private lateinit var service: ListServiceImpl

    @BeforeTest
    fun setUp() {
        service = ListServiceImpl(
            remoteApi = remoteApi,
            listRepo = listRepo,
            sessionService = sessionService,
            listShopAnalytics = listShopAnalytics
        )
    }

    @Test
    fun `when tagTree is created from a list of TagEntity objects, the tag tree is filled`() = runTest {
        val baseTagEntityList = createIngredientBaseEntityList()

        val result = TagTree(baseTagEntityList)

        assertTrue(result.isFilled(), "Tag tree is not filled")
    }

    @Test
    fun `when I get the Ingredient group content for the base test TagTree, I get five groups`() = runTest {
        val baseTagEntityList = createIngredientBaseEntityList()

        val tagTree = TagTree(baseTagEntityList)
        val result = tagTree.contentList(
            "0",
            nodeType = TagTreeNodeType.GROUPS_ONLY,
            tagTypes = listOf(TagType.INGREDIENT),
            descendantType = TagTreeDescendantType.ALL
        )


        assertNotNull(result, "Result list should not be null")
        assertEquals(5, result.size)
        assertTrue(result.all { it.isGroup })

        val fullTagTree = TagTree(buildListAllTags())
        val fullResult = fullTagTree.contentList(
            "0",
            
            nodeType = TagTreeNodeType.GROUPS_ONLY,
            tagTypes = listOf(TagType.INGREDIENT),
            descendantType = TagTreeDescendantType.ALL
        )


        assertNotNull(fullResult, "Result list should not be null")
        assertEquals(5, fullResult.size)
        assertTrue(fullResult.all { it.isGroup })

    }

    @Test
    fun `when I get direct Ingredient group content for the base test TagTree, I get the three groups`() = runTest {
        val baseTagEntityList = createIngredientBaseEntityList()

        val tagTree = TagTree(baseTagEntityList)
        val result = tagTree.contentList(
            "0",
            
            nodeType = TagTreeNodeType.GROUPS_ONLY,
            tagTypes = listOf(TagType.INGREDIENT),
            descendantType = TagTreeDescendantType.DIRECT
        )


        assertNotNull(result, "Result list should not be null")
        assertEquals(3, result.size)
        assertTrue(result.all { it.isGroup })

        val fullTagTree = TagTree(buildListAllTags())
        val fullResult = fullTagTree.contentList(
            "0",
            
            nodeType = TagTreeNodeType.GROUPS_ONLY,
            tagTypes = listOf(TagType.INGREDIENT),
            descendantType = TagTreeDescendantType.DIRECT
        )


        assertNotNull(fullResult, "Result list should not be null")
        assertEquals(3, fullResult.size)
        assertTrue(fullResult.all { it.isGroup })

    }

    @Test
    fun `when I get all group content for the base test TagTree, I get nine groups`() = runTest {
        val fullTagTree = TagTree(buildListAllTags())
        val fullResult = fullTagTree.contentList(
            "0",
            
            nodeType = TagTreeNodeType.GROUPS_ONLY,
            descendantType = TagTreeDescendantType.ALL
        )


        assertNotNull(fullResult, "Result list should not be null")
        assertEquals(9, fullResult.size)
        assertTrue(fullResult.all { it.isGroup })

    }

    @Test
    fun `when I get direct group content for the base test TagTree, I get six groups`() = runTest {
        val fullTagTree = TagTree(buildListAllTags())
        val fullResult = fullTagTree.contentList(
            "0",
            
            nodeType = TagTreeNodeType.GROUPS_ONLY,
            descendantType = TagTreeDescendantType.DIRECT
        )


        assertNotNull(fullResult, "Result list should not be null")
        assertEquals(6, fullResult.size)
        assertTrue(fullResult.all { it.isGroup })

    }

    @Test
    fun `when I get the Ingredient tag content for the base test TagTree, I get 6 tags`() = runTest {
        val baseTagEntityList = createIngredientBaseEntityList()

        val tagTree = TagTree(baseTagEntityList)
        val result = tagTree.contentList(
            "0",
            
            nodeType = TagTreeNodeType.TAGS_ONLY,
            tagTypes = listOf(TagType.INGREDIENT),
            descendantType = TagTreeDescendantType.ALL
        )


        assertNotNull(result, "Result list should not be null")
        assertEquals(6, result.size)
        assertTrue(result.all { !it.isGroup })

        val fullTagTree = TagTree(buildListAllTags())
        val fullResult = fullTagTree.contentList(
            "0",
            
            nodeType = TagTreeNodeType.TAGS_ONLY,
            tagTypes = listOf(TagType.INGREDIENT),
            descendantType = TagTreeDescendantType.ALL
        )


        assertNotNull(fullResult, "Result list should not be null")
        assertEquals(6, fullResult.size)
        assertTrue(fullResult.all { !it.isGroup })

    }

    @Test
    fun `when I get direct Ingredient tag content for the base test TagTree, I get the one tag`() = runTest {
        val baseTagEntityList = createIngredientBaseEntityList()

        val tagTree = TagTree(baseTagEntityList)
        val result = tagTree.contentList(
            "0",
            
            nodeType = TagTreeNodeType.TAGS_ONLY,
            tagTypes = listOf(TagType.INGREDIENT),
            descendantType = TagTreeDescendantType.DIRECT
        )


        assertNotNull(result, "Result list should not be null")
        assertEquals(0, result.size)

        val fullTagTree = TagTree(buildListAllTags())
        val fullResult = fullTagTree.contentList(
            "0",
            
            nodeType = TagTreeNodeType.TAGS_ONLY,
            tagTypes = listOf(TagType.INGREDIENT),
            descendantType = TagTreeDescendantType.DIRECT
        )


        assertNotNull(fullResult, "Result list should not be null")
        assertEquals(0, fullResult.size)
        assertTrue(fullResult.all { !it.isGroup })

    }

    @Test
    fun `when I get all tag content for the base test TagTree, I get 16 tags`() = runTest {
        val fullTagTree = TagTree(buildListAllTags())
        val fullResult = fullTagTree.contentList(
            "0",
            
            nodeType = TagTreeNodeType.TAGS_ONLY,
            descendantType = TagTreeDescendantType.ALL
        )


        assertNotNull(fullResult, "Result list should not be null")
        assertEquals(16, fullResult.size)
        assertTrue(fullResult.all { !it.isGroup })

    }

    @Test
    fun `when I get direct tag content for the base test TagTree, I get 0 tags`() = runTest {
        val fullTagTree = TagTree(buildListAllTags())
        val fullResult = fullTagTree.contentList(
            "0",
            
            nodeType = TagTreeNodeType.TAGS_ONLY,
            descendantType = TagTreeDescendantType.DIRECT
        )


        assertNotNull(fullResult, "Result list should not be null")
        assertEquals(0, fullResult.size)

    }


    @Test
    fun `when I get the Ingredient content - all, for the base test TagTree, I get 11 elements`() = runTest {
        val baseTagEntityList = createIngredientBaseEntityList()

        val tagTree = TagTree(baseTagEntityList)
        val result = tagTree.contentList(
            "0",
            
            nodeType = TagTreeNodeType.ALL,
            tagTypes = listOf(TagType.INGREDIENT),
            descendantType = TagTreeDescendantType.ALL
        )


        assertNotNull(result, "Result list should not be null")
        assertEquals(11, result.size)
        assertEquals(5,result.count { it.isGroup })
        assertEquals(6,result.count { !it.isGroup })

        val fullTagTree = TagTree(buildListAllTags())
        val fullResult = fullTagTree.contentList(
            "0",
            
            nodeType = TagTreeNodeType.ALL,
            tagTypes = listOf(TagType.INGREDIENT),
            descendantType = TagTreeDescendantType.ALL
        )


        assertNotNull(fullResult, "Result list should not be null")
        assertEquals(11, fullResult.size)
        assertEquals(5,result.count { it.isGroup })
        assertEquals(6,result.count { !it.isGroup })

    }

    @Test
    fun `when I get direct Ingredient content - all, for the base test TagTree, I get the three elements`() = runTest {
        val baseTagEntityList = createIngredientBaseEntityList()

        val tagTree = TagTree(baseTagEntityList)
        val result = tagTree.contentList(
            "0",
            
            nodeType = TagTreeNodeType.ALL,
            tagTypes = listOf(TagType.INGREDIENT),
            descendantType = TagTreeDescendantType.DIRECT
        )


        assertNotNull(result, "Result list should not be null")
        assertEquals(3, result.size)
        assertEquals(3,result.count { it.isGroup })

        val fullTagTree = TagTree(buildListAllTags())
        val fullResult = fullTagTree.contentList(
            "0",
            
            nodeType = TagTreeNodeType.ALL,
            tagTypes = listOf(TagType.INGREDIENT),
            descendantType = TagTreeDescendantType.DIRECT
        )


        assertNotNull(fullResult, "Result list should not be null")
        assertEquals(3, fullResult.size)
        assertEquals(3,result.count { it.isGroup })

    }

    @Test
    fun `when I get all content - all, for the base test TagTree, I get 25 elements`() = runTest {
        val fullTagTree = TagTree(buildListAllTags())
        val fullResult = fullTagTree.contentList(
            "0",
            
            nodeType = TagTreeNodeType.ALL,
            descendantType = TagTreeDescendantType.ALL
        )


        assertNotNull(fullResult, "Result list should not be null")
        assertEquals(25, fullResult.size)
        assertEquals(9,fullResult.count { it.isGroup })
        assertEquals(16,fullResult.count { !it.isGroup })

    }

    @Test
    fun `when I get direct content - all, for the base test TagTree, I get 6 elements`() = runTest {
        val fullTagTree = TagTree(buildListAllTags())
        val fullResult = fullTagTree.contentList(
            "0",
            
            nodeType = TagTreeNodeType.ALL,
            descendantType = TagTreeDescendantType.DIRECT
        )


        assertNotNull(fullResult, "Result list should not be null")
        assertEquals(6, fullResult.size)
        assertTrue(fullResult.all { it.isGroup })
    }

    @Test
    fun `when I get the Tag Type group content for the Dietary Type group, I get one group`() = runTest {
        val baseTagEntityList = createTagTypeBaseEntityList()

        val tagTree = TagTree(baseTagEntityList)
        val result = tagTree.contentList(
            "1300",
            
            nodeType = TagTreeNodeType.GROUPS_ONLY,
            tagTypes = listOf(TagType.TAG_TYPE),
            descendantType = TagTreeDescendantType.ALL
        )


        assertNotNull(result, "Result list should not be null")
        assertEquals(1, result.size)
        assertTrue(result.all { it.isGroup })

        val fullTagTree = TagTree(buildListAllTags())
        val fullResult = fullTagTree.contentList(
            "1300",
            
            nodeType = TagTreeNodeType.GROUPS_ONLY,
            tagTypes = listOf(TagType.TAG_TYPE),
            descendantType = TagTreeDescendantType.ALL
        )


        assertNotNull(fullResult, "Result list should not be null")
        assertEquals(1, fullResult.size)
        assertTrue(fullResult.all { it.isGroup })

    }

    @Test
    fun `when I get direct Tag Type group content for the Dietary Type group, I get one group`() = runTest {
        val baseTagEntityList = createTagTypeBaseEntityList()

        val tagTree = TagTree(baseTagEntityList)
        val result = tagTree.contentList(
            "1300",
            
            nodeType = TagTreeNodeType.GROUPS_ONLY,
            tagTypes = listOf(TagType.TAG_TYPE),
            descendantType = TagTreeDescendantType.DIRECT
        )


        assertNotNull(result, "Result list should not be null")
        assertEquals(1, result.size)
        assertTrue(result.all { it.isGroup })

        val fullTagTree = TagTree(buildListAllTags())
        val fullResult = fullTagTree.contentList(
            "1300",
            
            nodeType = TagTreeNodeType.GROUPS_ONLY,
            tagTypes = listOf(TagType.TAG_TYPE),
            descendantType = TagTreeDescendantType.DIRECT
        )


        assertNotNull(fullResult, "Result list should not be null")
        assertEquals(1, fullResult.size)
        assertTrue(fullResult.all { it.isGroup })

    }

    @Test
    fun `when I get all group content for the Dietary Type group, I get one group`() = runTest {
        val fullTagTree = TagTree(buildListAllTags())
        val fullResult = fullTagTree.contentList(
            "1300",
            
            nodeType = TagTreeNodeType.GROUPS_ONLY,
            descendantType = TagTreeDescendantType.ALL
        )


        assertNotNull(fullResult, "Result list should not be null")
        assertEquals(1, fullResult.size)
        assertTrue(fullResult.all { it.isGroup })

    }

    @Test
    fun `when I get direct group content for the Dietary Type group, I get one group`() = runTest {
        val fullTagTree = TagTree(buildListAllTags())
        val fullResult = fullTagTree.contentList(
            "1300",
            
            nodeType = TagTreeNodeType.GROUPS_ONLY,
            descendantType = TagTreeDescendantType.DIRECT
        )


        assertNotNull(fullResult, "Result list should not be null")
        assertEquals(1, fullResult.size)
        assertTrue(fullResult.all { it.isGroup })

    }

    @Test
    fun `when I get the Tag Type tag content for the Dietary Type group, I get 3 tags`() = runTest {
        val baseTagEntityList = createTagTypeBaseEntityList()

        val tagTree = TagTree(baseTagEntityList)
        val result = tagTree.contentList(
            "1300",
            
            nodeType = TagTreeNodeType.TAGS_ONLY,
            tagTypes = listOf(TagType.TAG_TYPE),
            descendantType = TagTreeDescendantType.ALL
        )


        assertNotNull(result, "Result list should not be null")
        assertEquals(3, result.size)
        assertTrue(result.all { !it.isGroup })

        val fullTagTree = TagTree(buildListAllTags())
        val fullResult = fullTagTree.contentList(
            "1300",
            
            nodeType = TagTreeNodeType.TAGS_ONLY,
            tagTypes = listOf(TagType.TAG_TYPE),
            descendantType = TagTreeDescendantType.ALL
        )


        assertNotNull(fullResult, "Result list should not be null")
        assertEquals(3, fullResult.size)
        assertTrue(fullResult.all { !it.isGroup })

    }

    @Test
    fun `when I get direct Tag Type tag content for the Dietary Type group, I get 0 tags`() = runTest {
        val baseTagEntityList = createTagTypeBaseEntityList()

        val tagTree = TagTree(baseTagEntityList)
        val result = tagTree.contentList(
            "1300",
            
            nodeType = TagTreeNodeType.TAGS_ONLY,
            tagTypes = listOf(TagType.TAG_TYPE),
            descendantType = TagTreeDescendantType.DIRECT
        )


        assertNotNull(result, "Result list should not be null")
        assertEquals(0, result.size)

        val fullTagTree = TagTree(buildListAllTags())
        val fullResult = fullTagTree.contentList(
            "1300",
            
            nodeType = TagTreeNodeType.TAGS_ONLY,
            tagTypes = listOf(TagType.TAG_TYPE),
            descendantType = TagTreeDescendantType.DIRECT
        )


        assertNotNull(fullResult, "Result list should not be null")
        assertEquals(0, fullResult.size)

    }

    @Test
    fun `when I get all tag content for the Dietary Type group, I get 3 tags`() = runTest {
        val fullTagTree = TagTree(buildListAllTags())
        val fullResult = fullTagTree.contentList(
            "1300",
            
            nodeType = TagTreeNodeType.TAGS_ONLY,
            descendantType = TagTreeDescendantType.ALL
        )


        assertNotNull(fullResult, "Result list should not be null")
        assertEquals(3, fullResult.size)
        assertTrue(fullResult.all { !it.isGroup })

    }

    @Test
    fun `when I get direct tag content for the Dietary Type group, I get 0 tags`() = runTest {
        val fullTagTree = TagTree(buildListAllTags())
        val fullResult = fullTagTree.contentList(
            "1300",
            
            nodeType = TagTreeNodeType.TAGS_ONLY,
            descendantType = TagTreeDescendantType.DIRECT
        )


        assertNotNull(fullResult, "Result list should not be null")
        assertEquals(0, fullResult.size)

    }


    @Test
    fun `when I get the Tag Type content - all, for the Dietary Type group, I get 4 elements`() = runTest {
        val baseTagEntityList = createTagTypeBaseEntityList()

        val tagTree = TagTree(baseTagEntityList)
        val result = tagTree.contentList(
            "1300",
            
            nodeType = TagTreeNodeType.ALL,
            tagTypes = listOf(TagType.TAG_TYPE),
            descendantType = TagTreeDescendantType.ALL
        )


        assertNotNull(result, "Result list should not be null")
        assertEquals(4, result.size)
        assertEquals(1,result.count { it.isGroup })
        assertEquals(3,result.count { !it.isGroup })

        val fullTagTree = TagTree(buildListAllTags())
        val fullResult = fullTagTree.contentList(
            "1300",
            
            nodeType = TagTreeNodeType.ALL,
            tagTypes = listOf(TagType.TAG_TYPE),
            descendantType = TagTreeDescendantType.ALL
        )


        assertNotNull(fullResult, "Result list should not be null")
        assertEquals(4, fullResult.size)
        assertEquals(1,fullResult.count { it.isGroup })
        assertEquals(3,fullResult.count { !it.isGroup })

    }

    @Test
    fun `when I get direct Tag Type content - all, for the Dietary Type group, I get one element`() = runTest {
        val baseTagEntityList = createTagTypeBaseEntityList()

        val tagTree = TagTree(baseTagEntityList)
        val result = tagTree.contentList(
            "1300",
            
            nodeType = TagTreeNodeType.ALL,
            tagTypes = listOf(TagType.TAG_TYPE),
            descendantType = TagTreeDescendantType.DIRECT
        )


        assertNotNull(result, "Result list should not be null")
        assertEquals(1, result.size)
        assertEquals(1,result.count { it.isGroup })

        val fullTagTree = TagTree(buildListAllTags())
        val fullResult = fullTagTree.contentList(
            "1300",
            
            nodeType = TagTreeNodeType.ALL,
            tagTypes = listOf(TagType.TAG_TYPE),
            descendantType = TagTreeDescendantType.DIRECT
        )


        assertNotNull(fullResult, "Result list should not be null")
        assertEquals(1, fullResult.size)
        assertEquals(1,fullResult.count { it.isGroup })

    }

    @Test
    fun `when I get all content - all, for the Dietary Type group, I get 4 elements`() = runTest {
        val fullTagTree = TagTree(buildListAllTags())
        val fullResult = fullTagTree.contentList(
            "1300",
            
            nodeType = TagTreeNodeType.ALL,
            descendantType = TagTreeDescendantType.ALL
        )


        assertNotNull(fullResult, "Result list should not be null")
        assertEquals(4, fullResult.size)
        assertEquals(1,fullResult.count { it.isGroup })
        assertEquals(3,fullResult.count { !it.isGroup })

    }

    @Test
    fun `when I get direct content - all, for the Dietary Type group, I get one element`() = runTest {
        val fullTagTree = TagTree(buildListAllTags())
        val fullResult = fullTagTree.contentList(
            "1300",
            
            nodeType = TagTreeNodeType.ALL,
            descendantType = TagTreeDescendantType.DIRECT
        )


        assertNotNull(fullResult, "Result list should not be null")
        assertEquals(1, fullResult.size)
        assertTrue(fullResult.all { it.isGroup })
    }




// done - next up - repeat the core tests for all (instead of groups or tags)
// done - repeat all three core type tests a child category - like produce
// test - revisit abbreviation (pass in abbreviation limit)
// repeat three core tests (groups, tags, all) with abbreviation

    @Test
    fun `when I get abbreviated direct content, for the Cuisine group, I get three elements`() = runTest {
        val fullTagTree = TagTree(buildListAllTags())
        val fullResult = fullTagTree.contentList(
            "1100",
            abbreviatedTo = 2,
            nodeType = TagTreeNodeType.ALL,
            descendantType = TagTreeDescendantType.DIRECT
        )


        assertNotNull(fullResult, "Result list should not be null")
        assertEquals(3, fullResult.size)
//        val resultIds : List<String> = fullResult.map { it.id }
  //      assertFalse(resultIds.contains("1101")) /
        // -- Mexican 1101
        // -- Chinese 1102
        // -- Indian 1103
        assertTrue(fullResult.all { it.isGroup })
    }


    private fun buildListAllTags(): List<TagEntity> {
        var allTags: MutableList<TagEntity> = mutableListOf<TagEntity>()
        allTags.addAll(createIngredientBaseEntityList())
        allTags.addAll(createTagTypeBaseEntityList())
        return allTags
    }

    private fun createIngredientBaseEntityList(): List<TagEntity> {
        // Produce 100
        // -- Fruit 110
        // --   Oranges 111
        // -- Vegetables 120
        // --    Cucumbers 121
        // --    Carrots 122
        // --    Rutabega 123
        // -- Drinks 200
        // --   Iced Tea - 201
        // -- Frozen 300
        // --   Ice Cream - 301

        val produceTags = listOf(
            buildIngredientTag("100", "Produce", "0"),
            buildIngredientTag("110", "Fruit", "100"),
            buildIngredientTag("111", "Oranges", "110"),
            buildIngredientTag("120", "Vegetables", "100"),
            buildIngredientTag("121", "Cucumbers", "120"),
            buildIngredientTag("123", "Rutabega", "120"),
            buildIngredientTag("122", "Carrots", "120"),
            buildIngredientTag("200", "Drinks", "0"),
            buildIngredientTag("201", "Iced Tea", "200"),
            buildIngredientTag("300", "Frozen", "0"),
            buildIngredientTag("301", "Ice Cream", "300")
        )

        return produceTags.map {
            TagEntity(
                externalId = it.externalId,
                isGroup = it.isgroup ?: false,
                name = it.name,
                parentId = it.parentId,
                power = it.power?.toString() ?: "0",
                tagType = it.tagType,
                userId = it.userId
            )
        }
    }

    private fun createTagTypeBaseEntityList(): List<TagEntity> {
        // Cuisine 1100
        // -- Mexican 1101
        // -- Chinese 1102
        // -- Indian 1103
        // Occasion 1200
        // -- Christmas 1201
        // -- Thanksgiving 1202
        // -- Easter 1203
        //.-- Ramadan 1204
        // Dietary Type 1300
        // -- Low in 1310
        // ---- Salt  1311
        // ---- Carbs 1312
        // ---- Fat -1313

        val produceTags = listOf(
            buildTagTypeTag("1100", "Cuisine", "0"),
            buildTagTypeTag("1101", "Mexican", "1100"),
            buildTagTypeTag("1102", "Chinese", "1100"),
            buildTagTypeTag("1103", "Indian", "1100"),
            buildTagTypeTag("1200", "Occasion", "0"),
            buildTagTypeTag("1201", "Christmas", "1200"),
            buildTagTypeTag("1202", "Thanksgiving", "1200"),
            buildTagTypeTag("1203", "Easter", "1200"),
            buildTagTypeTag("1204", "Ramadan", "1200"),
            buildTagTypeTag("1300", "Dietary Type", "0"),
            buildTagTypeTag("1310", "Low in", "1300"),
            buildTagTypeTag("1311", "Salt", "1310"),
            buildTagTypeTag("1312", "Carbs", "1310"),
            buildTagTypeTag("1313", "Fat", "1310")
        )

        return produceTags.map {
            TagEntity(
                externalId = it.externalId,
                isGroup = it.isgroup ?: false,
                name = it.name,
                parentId = it.parentId,
                power = it.power?.toString() ?: "0",
                tagType = it.tagType,
                userId = it.userId
            )
        }
    }

    private fun buildIngredientTag(externalId: String, name: String, parentId: String?): ApiTag {
        return buildTag(externalId = externalId, name = name, parentId = parentId, tagType = "Ingredient")
    }


    private fun buildTagTypeTag(externalId: String, name: String, parentId: String?): ApiTag {
        return buildTag(externalId = externalId, name = name, parentId = parentId, tagType = "TagType")
    }

    private fun buildTag(externalId: String, name: String, parentId: String?, tagType: String): ApiTag {
        return ApiTag(externalId = externalId, name = name, parentId = parentId, tagType = tagType)
    }
}


