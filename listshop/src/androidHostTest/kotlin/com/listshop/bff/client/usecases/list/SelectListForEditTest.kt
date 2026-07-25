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

class SelectListForEditTest {

    var useCaseProvider: ListUCP? = null

    val mockWebServer = MockWebServer()

    var analyticsHandle: AnalyticsHandle? = null

    var databaseTestHelper: TestDatabaseHelper? = null

    var baseUrl: String = ""

    val sampleProvider = TestSampleProvider("src/androidHostTest/resources/mock/json/launchScreen")

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
    fun `when i select a list, a single list is returned`(): Unit = runBlocking {
        initializeContext()
        val loggedInUser = databaseTestHelper?.standardUser()
        databaseTestHelper?.setUser(loggedInUser)
        val apiEmbedded = sampleProvider.fillSample<ApiShoppingList>("standardListAsApi")
        databaseTestHelper?.loadStandardListLocally(apiEmbedded)

        val connectionStatus = ConnectionStatus.Online

        // create list succeeds
        val testDispatcher = TestDispatcherBuilder("list/selectList")
            .withConfigFile("selectListSuccess.json")
            .build()

        mockWebServer.dispatcher = testDispatcher
        var result = useCaseProvider?.selectListForEdit(connectionStatus, "12345")
        assertNotNull(result)
        assertTrue(result.isSuccess)
        val list: ShoppingList? = result.value
        assertNotNull(list);
        assertEquals("12345", list.externalId)

    }

    @Test
    fun `when i select a list, the list is set as the server list`(): Unit = runBlocking {
        initializeContext()
        val loggedInUser = databaseTestHelper?.standardUser()
        databaseTestHelper?.setUser(loggedInUser)
        val apiEmbedded = sampleProvider.fillSample<ApiShoppingList>("standardListAsApi")
        databaseTestHelper?.loadStandardListLocally(apiEmbedded)

        val connectionStatus = ConnectionStatus.Online

        // create list succeeds
        val testDispatcher = TestDispatcherBuilder("list/selectList")
            .withConfigFile("selectListSuccess.json")
            .build()

        mockWebServer.dispatcher = testDispatcher
        var result = useCaseProvider?.selectListForEdit(connectionStatus, "12345")
        assertNotNull(result)
        assertTrue(result.isSuccess)

        val listInformations = databaseTestHelper?.currentListInfo()
        val listInformation = listInformations?.first()
        assertNotNull(listInformation)
        assertEquals("12345", listInformation?.serverListId)
    }

    @Test
    fun `when selecting a list fails, I get an error`(): Unit = runBlocking {
        initializeContext()
        val loggedInUser = databaseTestHelper?.standardUser()
            ?.copy(userName = "george", userToken = "abcdefg", userLastSeen = Clock.System.now().toString())
        databaseTestHelper?.setUser(loggedInUser)

        val connectionStatus = ConnectionStatus.Online

        // create list succeeds
        val testDispatcher = TestDispatcherBuilder("list/selectList")
            .withConfigFile("selectListFailure.json")
            .build()

        mockWebServer.dispatcher = testDispatcher
        var result = useCaseProvider?.selectListForEdit(connectionStatus, "12345")
        assertNotNull(result)
        assertFalse(result.isSuccess)
        val error = result._error
        assertNotNull(error)
        assertEquals(BFFErrorType.API, error?.type)
        assertEquals(BFFErrorSubtype.BAD_REQUEST, error?.subType)
    }

    @Test
    fun `when offline, I can't select a list`(): Unit = runBlocking {
        initializeContext()
        val loggedInUser = databaseTestHelper?.standardUser()
            ?.copy(userName = "george", userToken = "abcdefg", userLastSeen = Clock.System.now().toString())
        databaseTestHelper?.setUser(loggedInUser)

        val connectionStatus = ConnectionStatus.Offline

        val testDispatcher = TestDispatcherBuilder("list/selectList")
            .withConfigFile("selectListSuccess.json")
            .build()

        mockWebServer.dispatcher = testDispatcher
        var result = useCaseProvider?.addList(connectionStatus)
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
