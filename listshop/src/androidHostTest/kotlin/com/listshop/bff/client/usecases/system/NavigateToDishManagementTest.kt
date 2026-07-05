package com.listshop.bff.client.usecases.system

import com.listshop.analytics.*
import com.listshop.bff.TestDatabaseHelper
import com.listshop.bff.TestServiceLocator
import com.listshop.bff.data.bff.BFFResult
import com.listshop.bff.data.state.ConnectionStatus
import com.listshop.bff.data.state.TransitionViewState
import com.listshop.bff.test.server.TestDispatcherBuilder
import com.listshop.bff.ucp.SystemUCP
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockWebServer
import kotlin.test.*

class NavigateToDishManagementTest {

    var useCaseProvider: SystemUCP? = null

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

        useCaseProvider = locator.systemUCP
    }


    @Test
    fun `when i navigate to the dish management screen, the TVS is correct, with list`(): Unit = runBlocking {
        // setup database / context
        initializeContext()
        setLoggedInUser()

        // configure api calls
        val testDispatcher = TestDispatcherBuilder("system/navigateToDishManagement")
            .withConfigFile("dishListConfig.json")
            .build()

        val connectionStatus = ConnectionStatus.Online

        mockWebServer.dispatcher = testDispatcher
        var result = useCaseProvider?.navigateToDishManagement(connectionStatus)
        assertNotNull(result)
        assertTrue(result.isSuccess)

       assertTrue(result is BFFResult<TransitionViewState>)
        assertTrue(result.value is TransitionViewState.DishManagementScreen)
        val list = (result.value as TransitionViewState.DishManagementScreen).dishes
        assertNotNull(list)
        assertEquals(8, list.dishes.size)


    }

    @Test
    fun `when i navigate to the dish management screen with a logged out user, i get an empty list`(): Unit = runBlocking {
        // setup database / context
        initializeContext()

        // configure api calls
        val testDispatcher = TestDispatcherBuilder("system/navigateToDishManagement")
            .withConfigFile("dishListConfig.json")
            .build()

        val connectionStatus = ConnectionStatus.Online

        mockWebServer.dispatcher = testDispatcher
        var result = useCaseProvider?.navigateToDishManagement(connectionStatus)
        assertNotNull(result)
        assertTrue(result.isSuccess)

        assertTrue(result is BFFResult<TransitionViewState>)
        assertTrue(result.value is TransitionViewState.DishManagementScreen)
        val list = (result.value as TransitionViewState.DishManagementScreen).dishes
        assertNotNull(list)
        assertEquals(0, list.dishes.size)    }

    @Test
    fun `when i navigate to the dish management screen when disconnected, i get an empty list`(): Unit = runBlocking {
        // setup database / context
        initializeContext()
        setLoggedInUser()

        // configure api calls
        val testDispatcher = TestDispatcherBuilder("system/navigateToDishManagement")
            .withConfigFile("dishListConfig.json")
            .build()

        val connectionStatus = ConnectionStatus.Offline

        mockWebServer.dispatcher = testDispatcher
        var result = useCaseProvider?.navigateToDishManagement(connectionStatus)
        assertNotNull(result)
        assertTrue(result.isSuccess)

        assertTrue(result is BFFResult<TransitionViewState>)
        assertTrue(result.value is TransitionViewState.DishManagementScreen)
        val list = (result.value as TransitionViewState.DishManagementScreen).dishes
        assertNotNull(list)
        assertEquals(0, list.dishes.size)

    }

    @Test
    fun `when i navigate to the dish management screen and the api call fails, i get an error`(): Unit = runBlocking {
        // setup database / context
        initializeContext()
        setLoggedInUser()

        // configure api calls
        val testDispatcher = TestDispatcherBuilder("system/navigateToDishManagement")
            .withConfigFile("dishListFailureConfig.json")
            .build()

        val connectionStatus = ConnectionStatus.Online

        mockWebServer.dispatcher = testDispatcher
        var result = useCaseProvider?.navigateToDishManagement(connectionStatus)
        assertNotNull(result)
        assertTrue(result.isFailure)

    }


    private fun setLoggedInUser() {
        val loggedInUser = databaseTestHelper?.standardUser()
        databaseTestHelper?.setUser(loggedInUser)
    }


    @AfterTest
    fun shutDown() {
        mockWebServer.shutdown()
    }
}
