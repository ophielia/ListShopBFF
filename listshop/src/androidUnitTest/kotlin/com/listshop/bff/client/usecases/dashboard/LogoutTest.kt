package com.listshop.bff.client.usecases.dashboard

import com.listshop.analytics.*
import com.listshop.bff.TestDatabaseHelper
import com.listshop.bff.TestServiceLocator
import com.listshop.bff.data.bff.BFFErrorSubtype
import com.listshop.bff.data.bff.BFFErrorType
import com.listshop.bff.data.state.ConnectionStatus
import com.listshop.bff.test.server.TestDispatcherBuilder
import com.listshop.bff.ucp.DashboardUCP
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockWebServer
import kotlin.test.*

class LogoutTest {

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
    fun `when i logout a user, the api calls are made`(): Unit = runBlocking {
        // setup database / context
        initializeContext()
        val loggedInUser = databaseTestHelper?.standardUser()
        databaseTestHelper?.setUser(loggedInUser)

        // configure api calls
        val testDispatcher = TestDispatcherBuilder("dashboard/logoutUser")
            .withConfigFile("postLogoutUser.json")
            .withConfigFile("defaultLayoutConfig.json")
            .withConfigFile("standardTagConfig.json")
            .build()

        val connectionStatus = ConnectionStatus.Online

        mockWebServer.dispatcher = testDispatcher
        var result = useCaseProvider?.logout(connectionStatus)
        assertNotNull(result)
        assertTrue(result.isSuccess)
    }

    @Test
    fun `when i logout a user while offline, the call fails`(): Unit = runBlocking {
        // setup database / context
        initializeContext()
        val loggedInUser = databaseTestHelper?.standardUser()
        databaseTestHelper?.setUser(loggedInUser)

        // configure api calls
        val testDispatcher = TestDispatcherBuilder("dashboard/logoutUser")
            .withConfigFile("postLogoutUser.json")
            .build()

        val connectionStatus = ConnectionStatus.Offline

        mockWebServer.dispatcher = testDispatcher

        var result = useCaseProvider?.logout(connectionStatus)
        assertNotNull(result)
        assertTrue(result.isFailure)
        assertEquals("User cannot delete account while offline", result._error?.message)
        assertEquals(BFFErrorType.OFFLINE, result._error?.type)
        assertEquals(BFFErrorSubtype.OFFLINE, result._error?.subType)
    }

    @Test
    fun `when i logout a user which is not logged in, the call fails`(): Unit = runBlocking {
        // setup database / context
        initializeContext()

        // configure api calls
        val testDispatcher = TestDispatcherBuilder("dashboard/deleteUser")
            .withConfigFile("postDeleteUser.json")
            .build()

        mockWebServer.dispatcher = testDispatcher

        val connectionStatus = ConnectionStatus.Online

        var result = useCaseProvider?.logout(connectionStatus)
        assertNotNull(result)
        assertTrue(result.isFailure)
        assertEquals("User cannot logout when not logged in", result._error?.message)
        assertEquals(BFFErrorType.AUTHENTICATION, result._error?.type)
        assertEquals(BFFErrorSubtype.NOT_LOGGGED_IN, result._error?.subType)
    }

    @Test
    fun `when i logout a user, the session is cleared`(): Unit = runBlocking {
        // setup database / context
        initializeContext()
        val loggedInUser = databaseTestHelper?.standardUser()
        databaseTestHelper?.setUser(loggedInUser)
        val standardListInfo = databaseTestHelper?.standardListInfo()
        databaseTestHelper?.setListInfo(standardListInfo)

        // configure api calls
        val testDispatcher = TestDispatcherBuilder("dashboard/logoutUser")
            .withConfigFile("postLogoutUser.json")
            .withConfigFile("defaultLayoutConfig.json")
            .withConfigFile("standardTagConfig.json")
            .build()
        mockWebServer.dispatcher = testDispatcher

        val connectionStatus = ConnectionStatus.Online

        var result = useCaseProvider?.logout(connectionStatus)
        assertNotNull(result)
        assertTrue(result.isSuccess)
        // assert session is cleared
        val listInfo = databaseTestHelper?.currentListInfo()?.first()
        val userInfo = databaseTestHelper?.currentUserInfo()?.first()
        assertNull(userInfo?.userName)
        assertNull(userInfo?.userToken)
        assertNull(listInfo?.serverListId)
    }

    @AfterTest
    fun shutDown() {
        mockWebServer.shutdown()
    }
}
