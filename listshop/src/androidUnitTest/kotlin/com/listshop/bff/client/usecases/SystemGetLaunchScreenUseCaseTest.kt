package com.listshop.bff.client.usecases


import com.listshop.analytics.Analytics
import com.listshop.analytics.AnalyticsHandle
import com.listshop.analytics.AppInfo
import com.listshop.analytics.initDummyAnalytics
import com.listshop.bff.TestDatabaseHelper
import com.listshop.bff.TestServiceLocator
import com.listshop.bff.data.state.ConnectionStatus
import com.listshop.bff.data.state.OnboardingViewState
import com.listshop.bff.data.state.TransitionViewState
import com.listshop.bff.test.server.TestDispatcherBuilder
import com.listshop.bff.ucp.OnboardingUCP
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockWebServer
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue


class SystemGetLaunchScreenUseCaseTest {

    var useCaseProvider: OnboardingUCP? = null

    val mockWebServer = MockWebServer()
    var baseUrl: String = ""
    var analyticsHandle: AnalyticsHandle? = null
    var databaseTestHelper: TestDatabaseHelper? = null

    @BeforeTest
    fun setUp() {
        mockWebServer.start()
        baseUrl = mockWebServer.url("").toString()
        baseUrl = baseUrl.substring(0, baseUrl.length.minus(1) )

        val newAndBetterDispatcher = TestDispatcherBuilder("signIn")
            .withConfigFile("loginSuccessConfig.json")
            .withConfigFile("loginBadCredentialsConfig.json")
            .withConfigFile("getAllShoppingListsConfig.json")
            .build()

        mockWebServer.dispatcher = newAndBetterDispatcher

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
            clientVersion = "clientVersion",
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
    fun `first launch, connected - should go to choose screen`(): Unit = runBlocking {
        // start with clear database
        databaseTestHelper?.clearDatabase()

        val connectionStatus = ConnectionStatus.Online

        var result = useCaseProvider?.systemGetLaunchScreen(connectionStatus)
        assertNotNull(result)
        assertTrue(result.isSuccess)
        assertTrue(result.value is TransitionViewState)
        assertEquals(OnboardingViewState.Choose,(result.value as TransitionViewState.Onboarding).state )
    }

    @Test
    fun `launch with logged in user - should go to lists`(): Unit = runBlocking {
        // setup database / context
        initializeContext()
        val loggedInUser = databaseTestHelper?.standardUser()
        databaseTestHelper?.setUser(loggedInUser)

        val connectionStatus = ConnectionStatus.Online

        var result = useCaseProvider?.systemGetLaunchScreen(connectionStatus)
        assertNotNull(result)
        assertTrue(result.isSuccess)
        assertTrue(result.value is TransitionViewState)
        assertTrue(result.value is TransitionViewState.ListManagementScreen)

        val shoppingLists = (result.value as TransitionViewState.ListManagementScreen).shoppingLists
        assertNotNull(shoppingLists)

    }


    @Test
    fun `launch with logged out user - should go to choose screen`(): Unit = runBlocking {
        // setup database / context
        initializeContext()
        val loggedOutUser = databaseTestHelper?.standardUser()?.copy(userToken = null)
        databaseTestHelper?.setUser(loggedOutUser)

        val connectionStatus = ConnectionStatus.Online

        var result = useCaseProvider?.systemGetLaunchScreen(connectionStatus)
        assertNotNull(result)
        assertTrue(result.isSuccess)
        assertTrue(result.value is TransitionViewState)
        assertEquals(OnboardingViewState.Choose,(result.value as TransitionViewState.Onboarding).state )

    }

}