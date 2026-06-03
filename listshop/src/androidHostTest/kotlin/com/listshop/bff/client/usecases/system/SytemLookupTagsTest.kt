package com.listshop.bff.client.usecases.system

import com.listshop.analytics.*
import com.listshop.bff.TestDatabaseHelper
import com.listshop.bff.TestServiceLocator
import com.listshop.bff.data.model.Tag
import com.listshop.bff.data.model.TagList
import com.listshop.bff.data.remote.ApiTagList
import com.listshop.bff.services.TestSampleProvider
import com.listshop.bff.test.server.TestDispatcherBuilder
import com.listshop.bff.ucp.SystemUCP
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockWebServer
import kotlin.test.*

class SytemLookupTagsTest {

    var useCaseProvider: SystemUCP? = null

    val mockWebServer = MockWebServer()

    var analyticsHandle: AnalyticsHandle? = null

    var databaseTestHelper: TestDatabaseHelper? = null

    var baseUrl: String = ""

    val sampleProvider = TestSampleProvider("src/androidHostTest/resources/mock/json/standards")

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
    fun `when i search for tags, I get results`(): Unit = runBlocking {
        // setup database / context
        initializeContext()
        setLoggedInUser()
        saveLocalTags()

        // configure api calls
        val testDispatcher = TestDispatcherBuilder("system/navigateToDishManagement")
            .withConfigFile("dishListConfig.json")
            .build()
        mockWebServer.dispatcher = testDispatcher

        var result = useCaseProvider?.systemLookupTags("to", listOf("Ingredient"), false)
        assertNotNull(result)
        assertTrue(result.isSuccess)

        assertTrue(result.value is TagList)
        val list = result.value
        assertEquals(42, list.tags.size)


    }

    private fun setLoggedInUser() {
        val loggedInUser = databaseTestHelper?.standardUser()
        databaseTestHelper?.setUser(loggedInUser)
    }

    private fun saveLocalTags() {


        val apiEmbedded: ApiTagList = sampleProvider.fillSample<ApiTagList>("standardTags")
        val apiTagList =  apiEmbedded.tagList
        val tags = apiTagList.map { at -> Tag.create(at) }

        databaseTestHelper?.setTags(tags)
    }

    @AfterTest
    fun shutDown() {
        mockWebServer.shutdown()
    }
}
