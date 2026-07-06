package com.listshop.bff.client.usecases.dashboard

import com.listshop.analytics.*
import com.listshop.bff.TestDatabaseHelper
import com.listshop.bff.TestServiceLocator
import com.listshop.bff.data.model.TagType
import com.listshop.bff.db.TagEntity
import com.listshop.bff.test.server.TestDispatcherBuilder
import com.listshop.bff.ucp.DashboardUCP
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Clock
import okhttp3.mockwebserver.MockWebServer
import kotlin.test.*

class UpdateTagTest {

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
    fun `when i update a tag with the use case, api tag is returned`(): Unit = runBlocking {
        initializeContext()
        setupLoggedInUser()
        setupTagToUpdate()
        val tagId = "1234"
        val tagName = "BRAND NEW NAME"

        // swap out shopping list success with failure
        val testDispatcher = TestDispatcherBuilder("dashboard/updateTag")
            .withConfigFile("updateTagSuccess.json")
            .withConfigFile("retrieveTagSuccessPostUpdate.json")
            .withConfigFile("retrieveUserLayouts.json")
            .withConfigFile("retrieveDefaultLayout.json")
            .build()

        mockWebServer.dispatcher = testDispatcher
        var result = useCaseProvider?.updateTag(tagId, tagName)
        assertNotNull(result)
        assertTrue(result.isSuccess)
        val newTag = result.value

        assertEquals(tagName, newTag?.display)
        assertTrue(newTag?.isUser ?: false)
        assertEquals(tagId, newTag?.externalId)

    }



    @Test
    fun `when updating a tag fails, a Failed request is returned`(): Unit = runBlocking {
        initializeContext()
        setupLoggedInUser()
        setupTagToUpdate()
        val tagId = "1234"
        val tagName = "BRAND NEW NAME"

        // swap out shopping list success with failure
        val testDispatcher = TestDispatcherBuilder("dashboard/updateTag")
            .withConfigFile("updateTagFailure.json")
            .build()

        mockWebServer.dispatcher = testDispatcher
        var result = useCaseProvider?.updateTag(tagId, tagName)
        assertNotNull(result)
        assertTrue(result.isFailure)
    }

    @Test
    fun `updating a tag with empty tagName fails`(): Unit = runBlocking {
        initializeContext()
        setupLoggedInUser()
        setupTagToUpdate()
        val tagId = "1234"
        val tagName = ""

        // swap out shopping list success with failure
        val testDispatcher = TestDispatcherBuilder("dashboard/updateTag")
            .withConfigFile("updateTagFailure.json")
            .build()

        mockWebServer.dispatcher = testDispatcher
        var result = useCaseProvider?.updateTag(tagId, tagName)
        assertNotNull(result)
        assertTrue(result.isFailure)
    }

    @Test
    fun `updating a tag with empty tagId fails`(): Unit = runBlocking {
        initializeContext()
        setupLoggedInUser()
        setupTagToUpdate()
        val tagId = ""
        val tagName = "BRAND NEW NAME"

        // swap out shopping list success with failure
        val testDispatcher = TestDispatcherBuilder("dashboard/updateTag")
            .withConfigFile("updateTagFailure.json")
            .build()

        mockWebServer.dispatcher = testDispatcher
        var result = useCaseProvider?.updateTag(tagId, tagName)
        assertNotNull(result)
        assertTrue(result.isFailure)
    }


    @Test
    fun `updating a tag when logged out fails`(): Unit = runBlocking {
        initializeContext()
        setupLoggedOutUser()
        setupTagToUpdate()
        val tagId = "1234"
        val tagName = "BRAND NEW NAME"

        // swap out shopping list success with failure
        val testDispatcher = TestDispatcherBuilder("dashboard/updateTag")
            .withConfigFile("updateTagFailure.json")
            .build()

        mockWebServer.dispatcher = testDispatcher
        var result = useCaseProvider?.updateTag(tagId, tagName)
        assertNotNull(result)
        assertTrue(result.isFailure)
    }

    @Test
    fun `updating a tag when tag doesn't exist locally fails`(): Unit = runBlocking {
        initializeContext()
        setupLoggedInUser()

        val tagId = "1234"
        val tagName = "BRAND NEW NAME"

        // swap out shopping list success with failure
        val testDispatcher = TestDispatcherBuilder("dashboard/updateTag")
            .withConfigFile("updateTagSuccess.json")
            .withConfigFile("retrieveTagSuccessPostUpdate.json")
            .withConfigFile("retrieveUserLayouts.json")
            .withConfigFile("retrieveDefaultLayout.json")
            .build()

        mockWebServer.dispatcher = testDispatcher
        var result = useCaseProvider?.updateTag(tagId, tagName)
        assertNotNull(result)
        assertTrue(result.isFailure)
    }


    private fun setupTagToUpdate() {
        val setupTag = TagEntity(
            externalId = "1234",
            isGroup = false,
            name = "originalLittleName",
            parentId = "5678",
            power = "0",
            tagType = TagType.INGREDIENT.name,
            userId = "34"
        )
        databaseTestHelper?.setTag(setupTag)
    }

    private fun setupLoggedInUser() {
        val loggedInUser = databaseTestHelper?.standardUser()
            ?.copy(userName = "george", userToken = "abcdefg", userLastSeen = Clock.System.now().toString())
        databaseTestHelper?.setUser(loggedInUser)
    }
    private fun setupLoggedOutUser() {
        val loggedOutUser = databaseTestHelper?.standardUser()
            ?.copy(userName = "george", userToken = null, userLastSeen = Clock.System.now().toString())
        databaseTestHelper?.setUser(loggedOutUser)
    }

    @AfterTest
    fun shutDown() {
        mockWebServer.shutdown()
    }
}
