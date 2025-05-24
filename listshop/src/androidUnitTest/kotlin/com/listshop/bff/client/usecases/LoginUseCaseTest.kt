package com.listshop.bff.client.usecases


import com.listshop.analytics.Analytics
import com.listshop.analytics.AnalyticsHandle
import com.listshop.analytics.AppInfo
import com.listshop.analytics.initDummyAnalytics
import com.listshop.bff.SDKHandle
import com.listshop.bff.TestServiceLocator
import com.listshop.bff.dashboardUCPStartup
import com.listshop.bff.onboardingUCPStartup
import com.listshop.bff.sessionServiceStartup
import com.listshop.bff.tagUCPStartup
import com.listshop.bff.test.server.TestDispatcherBuilder
import com.listshop.bff.ucp.OnboardingUCP
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockWebServer
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue


class LoginUseCaseTest {

    var useCaseProvider: OnboardingUCP? = null

    val mockWebServer = MockWebServer()

    var analyticsHandle: AnalyticsHandle? = null

    var testInitialized = false;
    @BeforeTest
    fun setUp() {
        if (testInitialized) {
            return
        }

        mockWebServer.start()
        var baseUrl = mockWebServer.url("").toString()
        baseUrl = baseUrl.substring(0,baseUrl.length - 1)

        val newAndBetterDispatcher = TestDispatcherBuilder("signIn")
            .withConfigFile("loginSuccessConfig.json")
            .withConfigFile("loginBadCredentialsConfig.json")
            .withConfigFile("getAllShoppingListsConfig.json")
            .build()

        mockWebServer.dispatcher = newAndBetterDispatcher

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



        if (analyticsHandle == null) {
            val analytics = object : Analytics {
                override fun sendEvent(eventName: String, eventArgs: Map<String, Any>) {
                    println("eventName: ${eventName}, eventArgs: ${eventArgs.keys.joinToString(",") { key -> "[$key, ${eventArgs[key]}]" }}")
                }
            }
            analyticsHandle = initDummyAnalytics(analytics)
        }
        val locator: TestServiceLocator = TestServiceLocator(analyticsHandle!!, appInfo)
        val tagUCP = tagUCPStartup(analyticsHandle!!, appInfo)
        val onboardingUCP = onboardingUCPStartup(analyticsHandle!!, appInfo)
        val dashboardUCP = dashboardUCPStartup(analyticsHandle!!, appInfo)
        val sessionService = sessionServiceStartup(analyticsHandle!!, appInfo)
        val sdkHandle: SDKHandle = SDKHandle(
            appAnalytics = analyticsHandle!!.appAnalytics,
            tagUCP = tagUCP,
            sessionService = sessionService,
            onboardingUCP = onboardingUCP,
            dashboardUCP = dashboardUCP

        )
        // use driver from locator to insert sql statements, if necessary.

        useCaseProvider = sdkHandle.onboardingUCP
        testInitialized = true;
    }

    @AfterTest
    fun shutDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `login works but error on retrieving lists`(): Unit = runBlocking {
        val userName = "meg@the-list-shop.com"
        val password = "sarrieb1357"

        // swap out shopping list success with failure
        val errorDispatcher = TestDispatcherBuilder("signIn")
            .withConfigFile("loginSuccessConfig.json")
            .withConfigFile("getAllShoppingListsErrorConfig.json")
            .build()

        mockWebServer.dispatcher = errorDispatcher
        var result = useCaseProvider?.signIn(userName, password)
        assertNotNull(result)
        assertFalse(result.isSuccess)

        // restore original dispatcher
        val originalDispatcher = TestDispatcherBuilder("signIn")
            .withConfigFile("loginSuccessConfig.json")
            .withConfigFile("loginBadCredentialsConfig.json")
            .withConfigFile("getAllShoppingListsConfig.json")
            .build()

        mockWebServer.dispatcher = originalDispatcher
    }

    @Test
    fun `i cant login with bad credentials`(): Unit = runBlocking {
        val userName = "meg@the-list-shop.com"
        val password = "badPassword"

        var result = useCaseProvider?.signIn(userName, password)
        assertNotNull(result)
        assertTrue(result.isFailure)
        // verify the error result
        // make standard exception handler in main api which takes exception as an argument
        // go ahead and add exception handling to all calls (shopping list)
    }

    @Test
    fun `i can login with correct credentials`(): Unit = runBlocking {
        val userName = "meg@the-list-shop.com"
        val password = "sarrieb1357"

        var result = useCaseProvider?.signIn(userName, password)
        assertNotNull(result)
        assertFalse(result.isFailure)
        // verify the error result
        // make standard exception handler in main api which takes exception as an argument
        // go ahead and add exception handling to all calls (shopping list)
    }

}