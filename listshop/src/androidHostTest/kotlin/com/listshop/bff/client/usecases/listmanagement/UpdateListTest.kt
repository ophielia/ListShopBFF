package com.listshop.bff.client.usecases.listmanagement

import com.listshop.analytics.*
import com.listshop.bff.TestDatabaseHelper
import com.listshop.bff.TestServiceLocator
import com.listshop.bff.data.bff.BFFErrorSubtype
import com.listshop.bff.data.bff.BFFErrorType
import com.listshop.bff.data.state.ConnectionStatus
import com.listshop.bff.test.server.TestDispatcherBuilder
import com.listshop.bff.ucp.ListManagementUCP
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Clock
import okhttp3.mockwebserver.MockWebServer
import kotlin.test.*

class UpdateListTest {

    var useCaseProvider: ListManagementUCP? = null

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

        useCaseProvider = locator.listManagementUCP
    }


    @Test
    fun `when i update a list, a list of lists is returned`(): Unit = runBlocking {
        initializeContext()
        val loggedInUser = databaseTestHelper?.standardUser()
            ?.copy(userName = "george", userToken = "abcdefg", userLastSeen = Clock.System.now().toString())
        databaseTestHelper?.setUser(loggedInUser)
        databaseTestHelper?.setServerListId("999999999")

        val connectionStatus = ConnectionStatus.Online

        // create list succeeds
        val testDispatcher = TestDispatcherBuilder("listmanagement/updateList")
            .withConfigFile("updateListSuccess.json")
            .withConfigFile("getAllLists.json")
            .build()

        mockWebServer.dispatcher = testDispatcher
        var result = useCaseProvider?.updateList(connectionStatus, "12345", "GEORGE")
        assertNotNull(result)
        assertTrue(result.isSuccess)
        val listOfLists = result.value
        assertNotNull(listOfLists);
    }

    @Test
    fun `when i update the current list, a the current list is reloaded`(): Unit = runBlocking {
        initializeContext()
        val loggedInUser = databaseTestHelper?.standardUser()
            ?.copy(userName = "george", userToken = "abcdefg", userLastSeen = Clock.System.now().toString())
        databaseTestHelper?.setUser(loggedInUser)
        databaseTestHelper?.setServerListId("12345")


        val connectionStatus = ConnectionStatus.Online

        // create list succeeds
        val testDispatcher = TestDispatcherBuilder("listmanagement/updateList")
            .withConfigFile("updateListSuccess.json")
            .withConfigFile("reloadCurrentListConfig.json")
            .withConfigFile("getAllLists.json")
            .build()

        mockWebServer.dispatcher = testDispatcher
        var result = useCaseProvider?.updateList(connectionStatus, "12345", "GEORGE")
        assertNotNull(result)
        assertTrue(result.isSuccess)
        val listOfLists = result.value
        assertNotNull(listOfLists);
    }

    @Test
    fun `when updating a list fails, I get an error`(): Unit = runBlocking {
        initializeContext()
        val loggedInUser = databaseTestHelper?.standardUser()
            ?.copy(userName = "george", userToken = "abcdefg", userLastSeen = Clock.System.now().toString())
        databaseTestHelper?.setUser(loggedInUser)

        val connectionStatus = ConnectionStatus.Online

        // create list succeeds
        val testDispatcher = TestDispatcherBuilder("listmanagement/updateList")
            .withConfigFile("updateListFailure.json")
            .build()

        mockWebServer.dispatcher = testDispatcher
        var result = useCaseProvider?.updateList(connectionStatus, "12345", "listName")
        assertNotNull(result)
        assertFalse(result.isSuccess)
        val error = result._error
        assertNotNull(error)
        assertEquals(BFFErrorType.API, error?.type)
        assertEquals(BFFErrorSubtype.BAD_REQUEST, error?.subType)
    }

    @Test
    fun `when offline, I can't update a list`(): Unit = runBlocking {
        initializeContext()
        val loggedInUser = databaseTestHelper?.standardUser()
            ?.copy(userName = "george", userToken = "abcdefg", userLastSeen = Clock.System.now().toString())
        databaseTestHelper?.setUser(loggedInUser)

        val connectionStatus = ConnectionStatus.Offline

        val testDispatcher = TestDispatcherBuilder("listmanagement/updateList")
            .withConfigFile("updateListSuccess.json")
            .build()

        mockWebServer.dispatcher = testDispatcher
        var result = useCaseProvider?.updateList(connectionStatus, "12345", "listName")
        assertNotNull(result)
        assertFalse(result.isSuccess)
        val error = result._error
        assertNotNull(error)
        assertEquals(BFFErrorType.OFFLINE, error.type, "Action cannot be done while offline")
        assertEquals(BFFErrorSubtype.OFFLINE, error.subType, "Action cannot be done while offline")
    }


    @AfterTest
    fun shutDown() {
        mockWebServer.shutdown()
    }
}
