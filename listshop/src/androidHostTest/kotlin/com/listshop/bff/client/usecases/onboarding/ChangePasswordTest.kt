package com.listshop.bff.client.usecases.onboarding

import com.listshop.analytics.Analytics
import com.listshop.analytics.AnalyticsHandle
import com.listshop.analytics.AppInfo
import com.listshop.analytics.ClientType
import com.listshop.analytics.initDummyAnalytics
import com.listshop.bff.SDKHandle
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

class ChangePasswordTest {

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

        val newAndBetterDispatcher = TestDispatcherBuilder("onboarding/signIn")
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
            clientType = ClientType.IOS,
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

        useCaseProvider = sdkHandle.onboardingUCP
        testInitialized = true;
    }

    @AfterTest
    fun shutDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `change password server response 200 returns success`(): Unit = runBlocking {
        val oldPassword = "oldPassword"
        val newPassword = "newPassword"

        // swap out shopping list success with failure
        val testDispatcher = TestDispatcherBuilder("onboarding/changePassword")
            .withConfigFile("changePasswordSuccess.json")
            .build()

        mockWebServer.dispatcher = testDispatcher
        var result = useCaseProvider?.changePassword(oldPassword, newPassword)
        assertNotNull(result)
        assertTrue(result.isSuccess)
    }

    @Test
    fun `change password server response 400 returns failure`(): Unit = runBlocking {
        val oldPassword = "oldPassword"
        val newPassword = "newPassword"

        // swap out shopping list success with failure
        val testDispatcher = TestDispatcherBuilder("onboarding/changePassword")
            .withConfigFile("changePasswordError.json")
            .build()

        mockWebServer.dispatcher = testDispatcher
        var result = useCaseProvider?.changePassword(oldPassword, newPassword)
        assertNotNull(result)
        assertTrue(result.isFailure)
    }

}
