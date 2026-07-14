package com.listshop.bff.client.usecases.list

import com.listshop.analytics.*
import com.listshop.bff.TestDatabaseHelper
import com.listshop.bff.TestServiceLocator
import com.listshop.bff.data.model.TagType
import com.listshop.bff.test.server.TestDispatcherBuilder
import com.listshop.bff.ucp.DashboardUCP
import com.listshop.bff.ucp.ListUCP
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Clock
import okhttp3.mockwebserver.MockWebServer
import kotlin.test.*

class AddListTest {

    var useCaseProvider: ListUCP? = null

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

        useCaseProvider = locator.listUCP
    }


    @Test
    fun `when i create a list, a list of lists is returned`(): Unit = runBlocking {
        initializeContext()
        val loggedInUser = databaseTestHelper?.standardUser()
            ?.copy(userName = "george", userToken = "abcdefg", userLastSeen = Clock.System.now().toString())
        databaseTestHelper?.setUser(loggedInUser)

        val tagName = "tagName"
        val tagType = TagType.INGREDIENT.name
        val parentId = "1234"

        // swap out shopping list success with failure
        val testDispatcher = TestDispatcherBuilder("list/addList")
            .withConfigFile("createListSuccess.json")
            .withConfigFile("getAllLists.json")
            .build()

        mockWebServer.dispatcher = testDispatcher
        var result = useCaseProvider?.addList()
        assertNotNull(result)
        assertTrue(result.isSuccess)
        val listOfLists = result.value
        assertNotNull(listOfLists);
    }

    //MM bookmark - up next - test failure, and check / make test for list service

  /*
  @Test
    fun `when creating a list fails, a Failed request is returned`(): Unit = runBlocking {
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

   */
    @AfterTest
    fun shutDown() {
        mockWebServer.shutdown()
    }
}
