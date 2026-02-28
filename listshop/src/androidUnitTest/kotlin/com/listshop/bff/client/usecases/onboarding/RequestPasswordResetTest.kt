package com.listshop.bff.client.usecases.onboarding

import com.listshop.analytics.*
import com.listshop.bff.TestDatabaseHelper
import com.listshop.bff.TestServiceLocator
import com.listshop.bff.db.UserPropertiesEntity
import com.listshop.bff.test.server.TestDispatcherBuilder
import com.listshop.bff.ucp.OnboardingUCP
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Clock
import okhttp3.mockwebserver.MockWebServer
import kotlin.test.*

class RequestPasswordResetTest {

    var useCaseProvider: OnboardingUCP? = null

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

        useCaseProvider = locator.onboardingUCP
    }


    @Test
    fun `when i request a password reset, the remote call is successfully made`(): Unit = runBlocking {
        initializeContext()
        val loggedInUser = databaseTestHelper?.standardUser()
            ?.copy(userName = "george", userToken = "abcdefg", userLastSeen = Clock.System.now().toString())
        databaseTestHelper?.setUser(loggedInUser)

        val userName = "test@requestchange.com"

        // swap out shopping list success with failure
        val testDispatcher = TestDispatcherBuilder("onboarding/requestPasswordReset")
            .withConfigFile("callSuccess.json")
            .build()

        mockWebServer.dispatcher = testDispatcher
        var result = useCaseProvider?.requestPasswordReset(userName)
        assertNotNull(result)
        assertTrue(result.isSuccess)
    }


    @Test
    fun `when i request a password reset and the call fails, the use case fails`(): Unit = runBlocking {
        initializeContext()
        val loggedInUser = databaseTestHelper?.standardUser()
            ?.copy(userName = "george", userToken = "abcdefg", userLastSeen = Clock.System.now().toString())
        databaseTestHelper?.setUser(loggedInUser)

        val userName = "test@requestchange.com"

        // swap out shopping list success with failure
        val testDispatcher = TestDispatcherBuilder("onboarding/requestPasswordReset")
            .withConfigFile("callFailure.json")
            .build()

        mockWebServer.dispatcher = testDispatcher
        var result = useCaseProvider?.requestPasswordReset(userName)
        assertNotNull(result)
        assertTrue(result.isFailure)
    }


    //

    @AfterTest
    fun shutDown() {
        mockWebServer.shutdown()
    }
}
