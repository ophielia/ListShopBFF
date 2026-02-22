package com.listshop.bff.client.usecases.dashboard

import com.listshop.analytics.*
import com.listshop.bff.TestDatabaseHelper
import com.listshop.bff.TestServiceLocator
import com.listshop.bff.db.UserPropertiesEntity
import com.listshop.bff.test.server.TestDispatcherBuilder
import com.listshop.bff.ucp.DashboardUCP
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Clock
import okhttp3.mockwebserver.MockWebServer
import kotlin.test.*

class UpdateUserPropertyTest {

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
    fun `when i update a user property, the remote call is made`(): Unit = runBlocking {
        initializeContext()
        val loggedInUser = databaseTestHelper?.standardUser()
            ?.copy(userName = "george", userToken = "abcdefg", userLastSeen = Clock.System.now().toString())
        databaseTestHelper?.setUser(loggedInUser)

        val key = "My_Key"
        val value = "any old value"

        // swap out shopping list success with failure
        val testDispatcher = TestDispatcherBuilder("dashboard/updateUserProperty")
            .withConfigFile("updateUserPropertySuccess.json")
            .withConfigFile("getUserPropertySuccess.json")
            .build()

        mockWebServer.dispatcher = testDispatcher
        var result = useCaseProvider?.updateUserProperty(key, value)
        assertNotNull(result)
        assertTrue(result.isSuccess)
    }

    @Test
    fun `when i update a user property, the change is made locally`(): Unit = runBlocking {
        initializeContext()
        val loggedInUser = databaseTestHelper?.standardUser()
            ?.copy(userName = "george", userToken = "abcdefg", userLastSeen = Clock.System.now().toString())
        databaseTestHelper?.setUser(loggedInUser)

        val key = "My_Key"
        val value = "any old value"

        // swap out shopping list success with failure
        val testDispatcher = TestDispatcherBuilder("dashboard/updateUserProperty")
            .withConfigFile("updateUserPropertySuccess.json")
            .withConfigFile("getUserPropertySuccess.json")
            .build()

        mockWebServer.dispatcher = testDispatcher
        var result = useCaseProvider?.updateUserProperty(key, value)
        assertNotNull(result)
        assertTrue(result.isSuccess)
        val userProperties: List<UserPropertiesEntity> = databaseTestHelper?.getUserProperties() ?: emptyList()
        val userProperty = userProperties.filter { it.key.equals(key) }.firstOrNull()
        assertNotNull(userProperty)
        assertEquals(value, userProperty.property_value)
    }


    @Test
    fun `when the server call to update a property fails, a failed result is returned`(): Unit = runBlocking {
        initializeContext()
        val loggedInUser = databaseTestHelper?.standardUser()
            ?.copy(userName = "george", userToken = "abcdefg", userLastSeen = Clock.System.now().toString())
        databaseTestHelper?.setUser(loggedInUser)

        val key = "My_Key"
        val value = "any old value"

        // swap out shopping list success with failure
        val testDispatcher = TestDispatcherBuilder("dashboard/updateUserProperty")
            .withConfigFile("updateUserPropertyFailure.json")
            .build()

        mockWebServer.dispatcher = testDispatcher
        var result = useCaseProvider?.updateUserProperty(key, value)
        assertNotNull(result)
        assertTrue(result.isFailure)
    }


    @Test
    fun `when i update an existing property, the property is overwritten`(): Unit = runBlocking {
        val key = "My_Key"
        val value = "any old value"

        initializeContext()
        val loggedInUser = databaseTestHelper?.standardUser()
            ?.copy(userName = "george", userToken = "abcdefg", userLastSeen = Clock.System.now().toString())
        databaseTestHelper?.setUser(loggedInUser)
        databaseTestHelper?.setUserProperty(key, "the old value")

        // swap out shopping list success with failure
        val testDispatcher = TestDispatcherBuilder("dashboard/updateUserProperty")
            .withConfigFile("updateUserPropertySuccess.json")
            .withConfigFile("getUserPropertySuccess.json")
            .build()

        mockWebServer.dispatcher = testDispatcher
        var result = useCaseProvider?.updateUserProperty(key, value)
        assertNotNull(result)
        assertTrue(result.isSuccess)
        val userProperties: List<UserPropertiesEntity> = databaseTestHelper?.getUserProperties() ?: emptyList()
        val userProperty = userProperties.filter { it.key.equals(key) }.firstOrNull()
        assertNotNull(userProperty)
        assertEquals(value, userProperty.property_value)
    }

    //

    @AfterTest
    fun shutDown() {
        mockWebServer.shutdown()
    }
}
