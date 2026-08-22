package com.listshop.bff.client.usecases.onboarding

import com.listshop.analytics.*
import com.listshop.bff.TestDatabaseHelper
import com.listshop.bff.TestServiceLocator
import com.listshop.bff.data.model.ShoppingList
import com.listshop.bff.data.remote.ApiShoppingList
import com.listshop.bff.data.state.ConnectionStatus
import com.listshop.bff.data.state.OnboardingViewState
import com.listshop.bff.data.state.TransitionViewState
import com.listshop.bff.services.TagTree
import com.listshop.bff.services.TestSampleProvider
import com.listshop.bff.test.server.TestDispatcherBuilder
import com.listshop.bff.ucp.OnboardingUCP
import junit.framework.TestCase
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Clock
import okhttp3.mockwebserver.MockWebServer
import kotlin.test.*

class SystemInitializeClientTest {

    var useCaseProvider: OnboardingUCP? = null

    val mockWebServer = MockWebServer()
    var baseUrl: String = ""
    var analyticsHandle: AnalyticsHandle? = null
    var databaseTestHelper: TestDatabaseHelper? = null

    val sampleProvider = TestSampleProvider("src/androidHostTest/resources/mock/json/onboarding/launchScreen")

    @BeforeTest
    fun setUp() {
        mockWebServer.start()
        baseUrl = mockWebServer.url("").toString()
        baseUrl = baseUrl.substring(0, baseUrl.length.minus(1))

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


    fun initializeContext() {
        val appInfo = AppInfo(
            baseUrl = baseUrl,
            name = "name",
            model = "model",
            os = "os",
            osVersion = "osVersion",
            clientType = ClientType.IOS,
            clientVersion = "1.1.0",
            buildNumber = "buildNumber",
            deviceId = "deviceId"
        )
        val locator: TestServiceLocator = TestServiceLocator(analyticsHandle!!, appInfo)
        databaseTestHelper = locator.testDBHelper

        useCaseProvider = locator.onboardingUCP
    }

    @AfterTest
    fun shutDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `client incompatible - should receive error`(): Unit = runBlocking {
        // start with clear database
        databaseTestHelper?.clearDatabase()

        val errorDispatcher = TestDispatcherBuilder("system/initializeClient")
            .withConfigFile("launchScreenIncompatibleConfig.json")
            .build()
        mockWebServer.dispatcher = errorDispatcher

        val connectionStatus = ConnectionStatus.Online

        val result = useCaseProvider?.systemInitializeClient(connectionStatus)
        assertNotNull(result)
        assertEquals("Current version 1.1.0, Required Version 8.0", result._error?.message)
    }

    @Test
    fun `launch offline - should load and return tag tree`(): Unit = runBlocking {
        // setup database / context
        initializeContext()
        val loggedInUser = databaseTestHelper?.standardUser()
        databaseTestHelper?.setUser(loggedInUser)
        loadStandardListLocally()

        val connectionStatus = ConnectionStatus.Offline

        val offlineDispatcher = TestDispatcherBuilder("system/initializeClient")
            .withConfigFile("launchScreenCompatibleConfig.json")
            .build()
        mockWebServer.dispatcher = offlineDispatcher

        val result = useCaseProvider?.systemInitializeClient(connectionStatus)
        assertNotNull(result)
        assertTrue(result.isSuccess)
        assertNotNull(result.value)
        assertTrue(result.value is TagTree)
    }

    @Test
    fun `launch online with anonymous user, should load lookup data`(): Unit = runBlocking {
        // setup database / context
        initializeContext()
        val loggedInUser = databaseTestHelper?.standardUser()
            ?.copy(userName = null, userToken = null, userLastSeen = Clock.System.now().toString())
        databaseTestHelper?.setUser(loggedInUser)
        val listInfo = databaseTestHelper?.standardListInfo()?.copy()
        databaseTestHelper?.setListInfo(listInfo)
        loadStandardListLocally()

        val anonymousDispatcher = TestDispatcherBuilder("system/initializeClient")
            .withConfigFile("launchScreenCompatibleConfig.json")
            .withConfigFile("standardTagConfig.json")
            .withConfigFile("defaultLayoutConfig.json")
            .withConfigFile("userLayoutConfig.json")
            .build()
        mockWebServer.dispatcher = anonymousDispatcher

        val connectionStatus = ConnectionStatus.Online

        val result = useCaseProvider?.systemInitializeClient(connectionStatus)

        assertNotNull(result)
        assertTrue(result.isSuccess)
        assertNotNull(result.value)
        assertTrue(result.value.isFilled())
        assertTrue(result.value is TagTree)

    }

    @Test
    fun `launch online with logged in user, online - should merge list`(): Unit = runBlocking {

        // setup database / context
        initializeContext()
        val loggedInUser = databaseTestHelper?.standardUser()
        databaseTestHelper?.setUser(loggedInUser)
        loadStandardListLocally()

        val anonymousDispatcher = TestDispatcherBuilder("system/initializeClient")
            .withConfigFile("launchScreenCompatibleConfig.json")
            .withConfigFile("standardTagConfig.json")
            .withConfigFile("defaultLayoutConfig.json")
            .withConfigFile("userLayoutConfig.json")
            .withConfigFile("mergeListConfig.json")
            .build()
        mockWebServer.dispatcher = anonymousDispatcher

        val connectionStatus = ConnectionStatus.Online

        val result = useCaseProvider?.systemInitializeClient(connectionStatus)

        assertNotNull(result)
        assertTrue(result.isSuccess)
        assertNotNull(result.value)
        assertTrue(result.value is TagTree)
    }


    private fun loadStandardListLocally() {
        val apiEmbedded = sampleProvider.fillSample<ApiShoppingList>("standardListAsApi")
        val shoppingList = ShoppingList.create(apiEmbedded)
        databaseTestHelper?.setShoppingList(shoppingList)
    }

}
