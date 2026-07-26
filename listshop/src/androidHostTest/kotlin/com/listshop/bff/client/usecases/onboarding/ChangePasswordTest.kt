package com.listshop.bff.client.usecases.onboarding

import com.listshop.analytics.*
import com.listshop.bff.*
import com.listshop.bff.test.server.TestDispatcherBuilder
import com.listshop.bff.ucp.DashboardUCP
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockWebServer
import kotlin.test.*

class ChangePasswordTest {

    var useCaseProvider: DashboardUCP? = null

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
        baseUrl = baseUrl.substring(0, baseUrl.length - 1)

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
        val listUCP = listUCPStartup(analyticsHandle!!, appInfo)
        val sessionService = sessionServiceStartup(analyticsHandle!!, appInfo)
        val sdkHandle: SDKHandle = SDKHandle(
            appAnalytics = analyticsHandle!!.appAnalytics,
            tagUCP = tagUCP,
            sessionService = sessionService,
            onboardingUCP = onboardingUCP,
            dashboardUCP = dashboardUCP,
            listManagementUCP = listUCP

        )

        useCaseProvider = sdkHandle.dashboardUCP
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
