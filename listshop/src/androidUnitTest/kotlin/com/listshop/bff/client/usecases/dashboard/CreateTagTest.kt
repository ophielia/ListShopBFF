package com.listshop.bff.client.usecases.dashboard

import com.listshop.analytics.*
import com.listshop.bff.TestDatabaseHelper
import com.listshop.bff.TestServiceLocator
import com.listshop.bff.data.model.TagType
import com.listshop.bff.test.server.TestDispatcherBuilder
import com.listshop.bff.ucp.DashboardUCP
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Clock
import okhttp3.mockwebserver.MockWebServer
import kotlin.test.*

class CreateTagTest {

    var useCaseProvider: DashboardUCP? = null

    val mockWebServer = MockWebServer()

    var analyticsHandle: AnalyticsHandle? = null

    var databaseTestHelper: TestDatabaseHelper? = null

    var baseUrl: String = ""

    @BeforeTest
    fun setUp() {
        mockWebServer.start()
        baseUrl = mockWebServer.url("").toString()
        baseUrl = baseUrl.substring(0, baseUrl.length - 1)

        val standardDispatcher = TestDispatcherBuilder("signIn")
            .build()

        mockWebServer.dispatcher = standardDispatcher

        if (analyticsHandle == null) {
            val analytics = object : Analytics {
                override fun sendEvent(eventName: String, eventArgs: Map<String, Any>) {
                    println("eventName: ${eventName}, eventArgs: ${eventArgs.keys.joinToString(",") { key -> "[$key, ${eventArgs[key]}]" }}")
                }
            }
            analyticsHandle = initDummyAnalytics(analytics)
        }

        initializeContext()


    }

    private fun initializeContext() {
        val appInfo = AppInfo(
            baseUrl = baseUrl,
            name = "name",
            model = "model",
            os = "os",
            osVersion = "osVersion",
            clientType = ClientType.IOS,
            clientVersion = "clientVersion",
            buildNumber = "buildNumber",
            deviceId = "deviceId"
        )

        val locator: TestServiceLocator = TestServiceLocator(analyticsHandle!!, appInfo)
        databaseTestHelper = locator.testDBHelper

        useCaseProvider = locator.dashboardUCP
    }


    @Test
    fun `when i create a tag with the use case, api tag is returned`(): Unit = runBlocking {
        initializeContext()
        val loggedInUser = databaseTestHelper?.standardUser()
            ?.copy(userName = "george", userToken = "abcdefg", userLastSeen = Clock.System.now().toString())
        databaseTestHelper?.setUser(loggedInUser)

        val tagName = "tagName"
        val tagType = TagType.INGREDIENT.name
        val parentId = "1234"

        // swap out shopping list success with failure
        val testDispatcher = TestDispatcherBuilder("dashboard/createTag")
            .withConfigFile("createTagSuccess.json")
            .withConfigFile("retrieveTagSuccess.json")
            .withConfigFile("retrieveUserLayouts.json")
            .withConfigFile("retrieveDefaultLayout.json")
            .build()

        mockWebServer.dispatcher = testDispatcher
        var result = useCaseProvider?.createTag(tagName, tagType, parentId)
        assertNotNull(result)
        assertTrue(result.isSuccess)
        val newTag = result.value
        assertEquals("9876", newTag?.externalId)
        assertEquals(tagName, newTag?.display)
        assertEquals(TagType.INGREDIENT.display, newTag?.tagType)
        assertTrue(newTag?.isUser ?: false)
        assertEquals(parentId, newTag?.parentId)

    }

    @Test
    fun `when creating a tag fails, a Failed request is returned`(): Unit = runBlocking {
        val tagName = "tagName"
        val tagType = TagType.INGREDIENT.name
        val parentId = "1234"

        // swap out shopping list success with failure
        val testDispatcher = TestDispatcherBuilder("dashboard/createTag")
            .withConfigFile("createTagFailure.json")
            .build()

        mockWebServer.dispatcher = testDispatcher
        var result = useCaseProvider?.createTag(tagName, tagType, parentId)
        assertNotNull(result)
        assertTrue(result.isFailure)
    }

    @Test
    fun `when i create a tag with the use case, and layouts are available, api tag is returned`(): Unit = runBlocking {
        initializeContext()
        val loggedInUser = databaseTestHelper?.standardUser()
            ?.copy(userName = "george", userToken = "abcdefg", userLastSeen = Clock.System.now().toString())
        databaseTestHelper?.setUser(loggedInUser)

        val tagName = "tagName"
        val tagType = TagType.INGREDIENT.name
        val parentId = "1234"

        // swap out shopping list success with failure
        val testDispatcher = TestDispatcherBuilder("dashboard/createTag")
            .withConfigFile("createTagSuccess.json")
            .withConfigFile("retrieveTagSuccess.json")
            .withConfigFile("retrieveUserLayouts.json")
            .withConfigFile("retrieveDefaultLayout.json")
            .build()

        mockWebServer.dispatcher = testDispatcher
        var result = useCaseProvider?.createTag(tagName, tagType, parentId)
        assertNotNull(result)
        assertTrue(result.isSuccess)
        val newTag = result.value
        assertEquals("9876", newTag?.externalId)
        assertEquals(tagName, newTag?.display)
        assertEquals(TagType.INGREDIENT.display, newTag?.tagType)
        assertTrue(newTag?.isUser ?: false)
        assertEquals(parentId, newTag?.parentId)

    }

    @Test
    fun `creating a tag with empty tagName fails`(): Unit = runBlocking {
        val tagName = ""
        val tagType = TagType.INGREDIENT.name
        val parentId = "1234"

        // swap out shopping list success with failure
        val testDispatcher = TestDispatcherBuilder("dashboard/createTag")
            .withConfigFile("createTagSuccess.json")
            .withConfigFile("retrieveTagSuccess.json")
            .build()

        mockWebServer.dispatcher = testDispatcher
        var result = useCaseProvider?.createTag(tagName, tagType, parentId)
        assertNotNull(result)
        assertTrue(result.isFailure)
    }

    @Test
    fun `creating a tag with empty tagType fails`(): Unit = runBlocking {
        val tagName = "tagName"
        val tagType = ""
        val parentId = "1234"

        // swap out shopping list success with failure
        val testDispatcher = TestDispatcherBuilder("dashboard/createTag")
            .withConfigFile("createTagSuccess.json")
            .withConfigFile("retrieveTagSuccess.json")
            .build()

        mockWebServer.dispatcher = testDispatcher
        var result = useCaseProvider?.createTag(tagName, tagType, parentId)
        assertNotNull(result)
        assertTrue(result.isFailure)
    }

    @Test
    fun `creating a tag with empty parentId fails`(): Unit = runBlocking {
        val tagName = "tagName"
        val tagType = TagType.INGREDIENT.name
        val parentId = ""

        // swap out shopping list success with failure
        val testDispatcher = TestDispatcherBuilder("dashboard/createTag")
            .withConfigFile("createTagSuccess.json")
            .withConfigFile("retrieveTagSuccess.json")
            .build()

        mockWebServer.dispatcher = testDispatcher
        var result = useCaseProvider?.createTag(tagName, tagType, parentId)
        assertNotNull(result)
        assertTrue(result.isFailure)
    }

    @AfterTest
    fun shutDown() {
        mockWebServer.shutdown()
    }
}
