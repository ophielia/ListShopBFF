package com.listshop.bff.client.usecases.onboarding

import com.listshop.analytics.*
import com.listshop.bff.TestDatabaseHelper
import com.listshop.bff.TestServiceLocator
import com.listshop.bff.data.bff.BFFErrorSubtype
import com.listshop.bff.data.bff.BFFErrorType
import com.listshop.bff.data.model.ShoppingList
import com.listshop.bff.data.remote.ApiShoppingList
import com.listshop.bff.data.remote.ApiShoppingListEmbeddedList
import com.listshop.bff.data.state.ConnectionStatus
import com.listshop.bff.data.state.TransitionViewState
import com.listshop.bff.services.TestSampleProvider
import com.listshop.bff.test.server.TestDispatcherBuilder
import com.listshop.bff.ucp.OnboardingUCP
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockWebServer
import kotlin.test.*

class SignUpTest {

    var useCaseProvider: OnboardingUCP? = null

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


    @AfterTest
    fun shutDown() {
        mockWebServer.shutdown()
    }


    @Test
    fun `i can signup with valid credentials, and list is merged`(): Unit = runBlocking {
        initializeContext()
        saveLocalList()

        val userName = "meg@the-list-shop.com"
        val password = "sarrieb1357"

        // restore original dispatcher
        val originalDispatcher = TestDispatcherBuilder("onboarding/signUp")
            .withConfigFile("createShoppingListConfig.json")
            .withConfigFile("signUpSuccessConfig.json")
            .withConfigFile("mergeListConfig.json")
            .withConfigFile("getAllShoppingListsConfig.json")
            .withConfigFile("userLayoutConfig.json")
            .withConfigFile("userLayoutConfig.json")
            .withConfigFile("defaultLayoutConfig.json")
            .withConfigFile("standardTagConfig.json")
            .build()

        mockWebServer.dispatcher = originalDispatcher
        val connectionStatus = ConnectionStatus.Online
        var result = useCaseProvider?.signUp(userName, password, connectionStatus)
        assertNotNull(result)
        assertFalse(result.isFailure)

        // verify the result
        val shoppingLists = (result.value?.first as TransitionViewState.ListScreen).shoppingLists
        assertNotNull(shoppingLists)
        assertEquals(25, shoppingLists.list.size)

        val shoppingList = (result.value?.first as TransitionViewState.ListScreen).shoppingList
        assertNotNull(shoppingList)
        assertEquals(7, shoppingList.categories.size)

        val tagTree = result.value?.second
        assertNotNull(tagTree)

    }

    @Test
    fun `signup succeeds - no local list present`(): Unit = runBlocking {
        initializeContext()

        val userName = "meg@the-list-shop.com"
        val password = "sarrieb1357"

        // restore original dispatcher
        val originalDispatcher = TestDispatcherBuilder("onboarding/signUp")
            .withConfigFile("createShoppingListConfig.json")
            .withConfigFile("signUpSuccessConfig.json")
            .withConfigFile("mergeListConfig.json")
            .withConfigFile("getAllShoppingListsConfig.json")
            .withConfigFile("userLayoutConfig.json")
            .withConfigFile("userLayoutConfig.json")
            .withConfigFile("defaultLayoutConfig.json")
            .withConfigFile("standardTagConfig.json")
            .build()

        mockWebServer.dispatcher = originalDispatcher
        val connectionStatus = ConnectionStatus.Online
        var result = useCaseProvider?.signUp(userName, password, connectionStatus)
        assertNotNull(result)
        assertFalse(result.isFailure)

        // verify the result
        val shoppingLists = (result.value?.first as TransitionViewState.ListScreen).shoppingLists
        assertNotNull(shoppingLists)
        assertEquals(25, shoppingLists.list.size)

        val shoppingList = (result.value?.first as TransitionViewState.ListScreen).shoppingList
        assertNotNull(shoppingList)
        assertEquals(0, shoppingList.categories.size)

        val tagTree = result.value?.second
        assertNotNull(tagTree)

    }

    @Test
    fun `i can't signup while offline`(): Unit = runBlocking {
        initializeContext()

        val userName = "meg@the-list-shop.com"
        val password = "sarrieb1357"

        // restore original dispatcher
        val originalDispatcher = TestDispatcherBuilder("onboarding/signUp")
            .build()

        mockWebServer.dispatcher = originalDispatcher
        val connectionStatus = ConnectionStatus.Offline
        var result = useCaseProvider?.signUp(userName, password, connectionStatus)
        assertNotNull(result)
        assertTrue(result.isFailure)

        // verify the result
        val error = result._error
        assertEquals(BFFErrorType.OFFLINE, error?.type)
        assertEquals(BFFErrorSubtype.OFFLINE, error?.subType)
        assertEquals("User cannot signup while offline", error?.message)


    }

    @Test
    fun `i can't signup with empty credentials`(): Unit = runBlocking {
        initializeContext()

        val userName = ""
        val password = ""

        // restore original dispatcher
        val originalDispatcher = TestDispatcherBuilder("onboarding/signUp")
            .build()

        mockWebServer.dispatcher = originalDispatcher
        val connectionStatus = ConnectionStatus.Online
        var result = useCaseProvider?.signUp(userName, password, connectionStatus)
        assertNotNull(result)
        assertTrue(result.isFailure)

        // verify the result
        val error = result._error
        assertEquals(BFFErrorType.VALIDATION, error?.type)
        assertEquals(BFFErrorSubtype.INVALID_INPUT, error?.subType)
        assertEquals("userName or password is blank", error?.message)


    }

    @Test
    fun `i can't signup with really long credentials`(): Unit = runBlocking {
        initializeContext()

        val userName = "nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn"
        val password = "nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn"

        // restore original dispatcher
        val originalDispatcher = TestDispatcherBuilder("onboarding/signUp")
            .build()

        mockWebServer.dispatcher = originalDispatcher
        val connectionStatus = ConnectionStatus.Online
        var result = useCaseProvider?.signUp(userName, password, connectionStatus)
        assertNotNull(result)
        assertTrue(result.isFailure)

        // verify the result
        val error = result._error
        assertEquals(BFFErrorType.VALIDATION, error?.type)
        assertEquals(BFFErrorSubtype.INVALID_INPUT, error?.subType)
        assertEquals("userName too long", error?.message)


    }

    @Test
    fun `signup succeeds, but merge list fails - go to list screen with local lists`(): Unit = runBlocking {
        initializeContext()
        saveLocalList()

        val userName = "meg@the-list-shop.com"
        val password = "sarrieb1357"

        // restore original dispatcher
        val originalDispatcher = TestDispatcherBuilder("onboarding/signUp")
            .withConfigFile("createShoppingListFailureConfig.json")
            .withConfigFile("signUpSuccessConfig.json")
            .withConfigFile("mergeListFailureConfig.json")
            .withConfigFile("getAllShoppingListsConfig.json")
            .withConfigFile("userLayoutConfig.json")
            .withConfigFile("userLayoutConfig.json")
            .withConfigFile("defaultLayoutConfig.json")
            .withConfigFile("standardTagConfig.json")
            .build()

        mockWebServer.dispatcher = originalDispatcher
        val connectionStatus = ConnectionStatus.Online
        var result = useCaseProvider?.signUp(userName, password, connectionStatus)
        assertNotNull(result)
        assertFalse(result.isFailure)

        // verify the result
        val shoppingLists = (result.value?.first as TransitionViewState.ListScreen).shoppingLists
        assertNotNull(shoppingLists)
        assertEquals(25, shoppingLists.list.size)

        val shoppingList = (result.value?.first as TransitionViewState.ListScreen).shoppingList
        assertNotNull(shoppingList)
        assertEquals(7, shoppingList.categories.size)

        val tagTree = result.value?.second
        assertNotNull(tagTree)
    }

    @Test
    fun `signup succeeds, but retrieve list of lists fails - go to list screen with empty lists`(): Unit = runBlocking {
        initializeContext()
        saveLocalList()

        val userName = "meg@the-list-shop.com"
        val password = "sarrieb1357"

        // restore original dispatcher
        val originalDispatcher = TestDispatcherBuilder("onboarding/signUp")
            .withConfigFile("createShoppingListConfig.json")
            .withConfigFile("signUpSuccessConfig.json")
            .withConfigFile("mergeListConfig.json")
            .withConfigFile("getAllShoppingListsFailureConfig.json")
            .withConfigFile("userLayoutConfig.json")
            .withConfigFile("userLayoutConfig.json")
            .withConfigFile("defaultLayoutConfig.json")
            .withConfigFile("standardTagConfig.json")
            .build()

        mockWebServer.dispatcher = originalDispatcher
        val connectionStatus = ConnectionStatus.Online
        var result = useCaseProvider?.signUp(userName, password, connectionStatus)
        assertNotNull(result)
        assertFalse(result.isFailure)

        // verify the result
        val shoppingLists = (result.value?.first as TransitionViewState.ListScreen).shoppingLists
        assertNotNull(shoppingLists)
        assertEquals(0, shoppingLists.list.size)

        val shoppingList = (result.value?.first as TransitionViewState.ListScreen).shoppingList
        assertNotNull(shoppingList)
        assertEquals(7, shoppingList.categories.size)

        val tagTree = result.value?.second
        assertNotNull(tagTree)

    }


    private fun saveLocalList() {
        var apiEmbedded = sampleProvider.fillSample<ApiShoppingList>("standardSingleList")
        apiEmbedded.name = "LOCAL LIST"
        val shoppingList = ShoppingList.Factory.create(apiEmbedded)

        databaseTestHelper?.setShoppingList(shoppingList)
    }


    private fun setLocalListUpdated() {
        val listInfo =
            databaseTestHelper?.standardListInfo()?.copy(localListUpdated = "2021-08-23T15:25:43.511Z")
        databaseTestHelper?.setListInfo(listInfo)

    }

}
