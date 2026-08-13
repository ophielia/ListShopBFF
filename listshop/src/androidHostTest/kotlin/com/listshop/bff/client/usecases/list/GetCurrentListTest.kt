package com.listshop.bff.client.usecases.list

import com.listshop.analytics.*
import com.listshop.bff.TestDatabaseHelper
import com.listshop.bff.TestServiceLocator
import com.listshop.bff.data.bff.BFFErrorSubtype
import com.listshop.bff.data.bff.BFFErrorType
import com.listshop.bff.data.model.ShoppingList
import com.listshop.bff.data.remote.ApiShoppingList
import com.listshop.bff.data.state.ConnectionStatus
import com.listshop.bff.services.TestSampleProvider
import com.listshop.bff.test.server.TestDispatcherBuilder
import com.listshop.bff.ucp.ListUCP
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Clock
import okhttp3.mockwebserver.MockWebServer
import kotlin.test.*

class GetCurrentListTest {

    var useCaseProvider: ListUCP? = null

    val mockWebServer = MockWebServer()

    var analyticsHandle: AnalyticsHandle? = null

    var databaseTestHelper: TestDatabaseHelper? = null

    var baseUrl: String = ""

    val sampleProvider = TestSampleProvider("src/androidHostTest/resources/mock/json/standards")

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
    fun `when i get the current list, the list for the current server id is returned`(): Unit = runBlocking {
        initializeContext()
        val loggedInUser = databaseTestHelper?.standardUser()
            ?.copy(userName = "george", userToken = "abcdefg", userLastSeen = Clock.System.now().toString())
        databaseTestHelper?.setUser(loggedInUser)
        databaseTestHelper?.setServerListId("12345")

        val connectionStatus = ConnectionStatus.Online

        // create list succeeds
        val testDispatcher = TestDispatcherBuilder("list")
            .withConfigFile("getCurrentListSuccess.json")
            .build()

        mockWebServer.dispatcher = testDispatcher
        var result = useCaseProvider?.getCurrentList(connectionStatus)
        assertNotNull(result)
        assertTrue(result.isSuccess)
        val list = result.value
        assertNotNull(list);
    }

    @Test
    fun `when i get the current list, and it doesn't exist, the most recent list is returned`(): Unit = runBlocking {
        initializeContext()
        val loggedInUser = databaseTestHelper?.standardUser()
            ?.copy(userName = "george", userToken = "abcdefg", userLastSeen = Clock.System.now().toString())
        databaseTestHelper?.setUser(loggedInUser)
        databaseTestHelper?.setServerListId("9101112")

        val connectionStatus = ConnectionStatus.Online

        // create list succeeds
        val testDispatcher = TestDispatcherBuilder("list")
            .withConfigFile("getCurrentListSuccess.json")
            .withConfigFile("serverListMostRecentSuccess.json")
            .build()

        mockWebServer.dispatcher = testDispatcher
        var result = useCaseProvider?.getCurrentList(connectionStatus)
        assertNotNull(result)
        assertTrue(result.isSuccess)
        val list = result.value
        assertNotNull(list);
        assertEquals("99999", list.externalId)
    }

    @Test
    fun `when getting the current list fails, I get an error`(): Unit = runBlocking {
        initializeContext()
        val loggedInUser = databaseTestHelper?.standardUser()
            ?.copy(userName = "george", userToken = "abcdefg", userLastSeen = Clock.System.now().toString())
        databaseTestHelper?.setUser(loggedInUser)

        val connectionStatus = ConnectionStatus.Online

        // create list succeeds
        val testDispatcher = TestDispatcherBuilder("list")
            .withConfigFile("getCurrentListFailure.json")
            .build()

        mockWebServer.dispatcher = testDispatcher
        var result = useCaseProvider?.getCurrentList(connectionStatus)
        assertNotNull(result)
        assertFalse(result.isSuccess)
        val error = result._error
        assertNotNull(error)
        assertEquals(BFFErrorType.API, error?.type)
        assertEquals(BFFErrorSubtype.BAD_REQUEST, error?.subType)
    }

    @Test
    fun `when offline, I get the local list`(): Unit = runBlocking {
        initializeContext()
        val loggedInUser = databaseTestHelper?.standardUser()
            ?.copy(userName = "george", userToken = "abcdefg", userLastSeen = Clock.System.now().toString())
        databaseTestHelper?.setUser(loggedInUser)
        saveLocalList()

        val connectionStatus = ConnectionStatus.Offline

        val testDispatcher = TestDispatcherBuilder("list")
            .withConfigFile("getCurrentListSuccess.json")
            .build()

        mockWebServer.dispatcher = testDispatcher
        var result = useCaseProvider?.getCurrentList(connectionStatus)
        assertNotNull(result)
        assertTrue(result.isSuccess)
        val shoppingList = result.value
        assertNotNull(shoppingList)
        assertEquals("LOCAL LIST", shoppingList.name)
    }


    @AfterTest
    fun shutDown() {
        mockWebServer.shutdown()
    }

    private fun saveLocalList() {
        var apiEmbedded = sampleProvider.fillSample<ApiShoppingList>("standardSingleList")
        apiEmbedded.name = "LOCAL LIST"
        val shoppingList = ShoppingList.Factory.create(apiEmbedded)

        databaseTestHelper?.setShoppingList(shoppingList)
    }
}
