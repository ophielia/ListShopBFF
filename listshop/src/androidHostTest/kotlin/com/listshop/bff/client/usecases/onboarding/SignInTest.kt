package com.listshop.bff.client.usecases.onboarding

import com.listshop.analytics.Analytics
import com.listshop.analytics.AnalyticsHandle
import com.listshop.analytics.AppInfo
import com.listshop.analytics.ClientType
import com.listshop.analytics.initDummyAnalytics
import com.listshop.bff.SDKHandle
import com.listshop.bff.TestDatabaseHelper
import com.listshop.bff.TestServiceLocator
import com.listshop.bff.dashboardUCPStartup
import com.listshop.bff.data.bff.BFFErrorSubtype
import com.listshop.bff.data.bff.BFFErrorType
import com.listshop.bff.data.model.ShoppingList
import com.listshop.bff.data.remote.ApiShoppingListEmbeddedList
import com.listshop.bff.data.state.ConnectionStatus
import com.listshop.bff.data.state.TransitionViewState
import com.listshop.bff.onboardingUCPStartup
import com.listshop.bff.services.TestSampleProvider
import com.listshop.bff.sessionServiceStartup
import com.listshop.bff.tagUCPStartup
import com.listshop.bff.test.server.TestDispatcher
import com.listshop.bff.test.server.TestDispatcherBuilder
import com.listshop.bff.ucp.OnboardingUCP
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockWebServer
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class SignInTest {

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
    fun `i cant login with bad credentials`(): Unit = runBlocking {
        initializeContext()

        val userName = "meg@the-list-shop.com"
        val password = "badPassword"

        val connectionStatus = ConnectionStatus.Online

        val dispatcher = TestDispatcherBuilder("onboarding/signIn")
            .withConfigFile("loginSuccessConfig.json")
            .withConfigFile("loginBadCredentialsConfig.json")
            .withConfigFile("getAllShoppingListsConfig.json")
            .build()
        mockWebServer.dispatcher = dispatcher

        var result = useCaseProvider?.signIn(userName, password, connectionStatus)
        assertNotNull(result)
        assertTrue(result.isFailure)
        // verify the error result
        val error = result._error!!
        assertEquals(BFFErrorType.AUTHENTICATION,error.type)
        assertEquals(BFFErrorSubtype.CANT_LOGIN,error.subType)
        assertEquals("login call failed with status: 401", error.message)
    }

    @Test
    fun `i can login with correct credentials`(): Unit = runBlocking {
        initializeContext()
        val userName = "meg@the-list-shop.com"
        val password = "sarrieb1357"

        // restore original dispatcher
        val originalDispatcher = TestDispatcherBuilder("onboarding/signIn")
            .withConfigFile("loginSuccessConfig.json")
            .withConfigFile("getAllShoppingListsConfig.json")
            .withConfigFile("standardTagConfig.json")
            .withConfigFile("userLayoutConfig.json")
            .withConfigFile("userPropertySuccess.json")
            .withConfigFile("defaultLayoutConfig.json")
            .withConfigFile("serverListMostRecentConfig.json")
            .build()

        mockWebServer.dispatcher = originalDispatcher
        val connectionStatus = ConnectionStatus.Online
        var result = useCaseProvider?.signIn(userName, password, connectionStatus)
        assertNotNull(result)
        assertFalse(result.isFailure)
        // verify the result
        val shoppingLists = (result.value?.first as TransitionViewState.ListScreen).shoppingLists
        assertNotNull(shoppingLists)
        assertEquals(9, shoppingLists.list.size)

        val shoppingList = (result.value?.first as TransitionViewState.ListScreen).shoppingList
        assertNotNull(shoppingList)
        assertEquals(3, shoppingList.categories.size)

        val tagTree = result.value?.second
        assertNotNull(tagTree)
    }

    @Test
    fun `login works and no error if local list present`(): Unit = runBlocking {
        initializeContext()
        saveLocalList()
        setLocalListUpdated()

        val userName = "meg@the-list-shop.com"
        val password = "sarrieb1357"

        // restore original dispatcher
        val originalDispatcher = TestDispatcherBuilder("onboarding/signIn")
            .withConfigFile("loginSuccessConfig.json")
            .withConfigFile("getAllShoppingListsConfig.json")
            .withConfigFile("standardTagConfig.json")
            .withConfigFile("userLayoutConfig.json")
            .withConfigFile("userPropertySuccess.json")
            .withConfigFile("defaultLayoutConfig.json")
            .withConfigFile("mergeListConfig.json")
            .withConfigFile("serverListMostRecentConfig.json")
            .build()

        mockWebServer.dispatcher = originalDispatcher
        val connectionStatus = ConnectionStatus.Online
        var result = useCaseProvider?.signIn(userName, password, connectionStatus)
        assertNotNull(result)
        assertFalse(result.isFailure)
        // verify the result
        val shoppingLists = (result.value?.first as TransitionViewState.ListScreen).shoppingLists
        assertNotNull(shoppingLists)
        assertEquals(9, shoppingLists.list.size)

        val shoppingList = (result.value?.first as TransitionViewState.ListScreen).shoppingList
        assertNotNull(shoppingList)
        assertEquals(3, shoppingList.categories.size)

        val tagTree = result.value?.second
        assertNotNull(tagTree)
    }



    @Test
    fun `login works but error on retrieving lists - empty lists, tag tree`(): Unit = runBlocking {
        val userName = "meg@the-list-shop.com"
        val password = "sarrieb1357"

        // swap out shopping list success with failure
        val errorDispatcher = TestDispatcherBuilder("onboarding/signIn")
            .withConfigFile("loginSuccessConfig.json")
            .withConfigFile("getAllShoppingListsConfig.json")
            .withConfigFile("standardTagConfig.json")
            .withConfigFile("userLayoutConfig.json")
            .withConfigFile("userPropertySuccess.json")
            .withConfigFile("defaultLayoutConfig.json")
            .withConfigFile("serverListMostRecentFailureConfig.json")
            .build()

        mockWebServer.dispatcher = errorDispatcher

        val connectionStatus = ConnectionStatus.Online

        var result = useCaseProvider?.signIn(userName, password, connectionStatus)
        assertNotNull(result)
        assertTrue(result.isSuccess)
        val shoppingLists = (result.value?.first as TransitionViewState.ListScreen).shoppingLists
        assertNotNull(shoppingLists)
        assertEquals(0, shoppingLists.list.size)

        val shoppingList = (result.value?.first as TransitionViewState.ListScreen).shoppingList
        assertNotNull(shoppingList)
        assertEquals(0, shoppingList.categories.size)

        val tagTree = result.value?.second
        assertNotNull(tagTree)
    }


    @Test
    fun `login works but error on syncing data - empty lists, tag tree`(): Unit = runBlocking {
        val userName = "meg@the-list-shop.com"
        val password = "sarrieb1357"

        // swap out shopping list success with failure
        val errorDispatcher = TestDispatcherBuilder("onboarding/signIn")
            .withConfigFile("loginSuccessConfig.json")
            .withConfigFile("getAllShoppingListsConfig.json")
            .withConfigFile("tagFailureConfig.json")
            .withConfigFile("userLayoutConfig.json")
            .withConfigFile("userPropertySuccess.json")
            .withConfigFile("defaultLayoutConfig.json")
            .withConfigFile("serverListMostRecentConfig.json")
            .build()

        mockWebServer.dispatcher = errorDispatcher

        val connectionStatus = ConnectionStatus.Online

        var result = useCaseProvider?.signIn(userName, password, connectionStatus)
        assertNotNull(result)
        assertTrue(result.isSuccess)
        val shoppingLists = (result.value?.first as TransitionViewState.ListScreen).shoppingLists
        assertNotNull(shoppingLists)
        assertEquals(0, shoppingLists.list.size)

        val shoppingList = (result.value?.first as TransitionViewState.ListScreen).shoppingList
        assertNotNull(shoppingList)
        assertEquals(0, shoppingList.categories.size)

        val tagTree = result.value?.second
        assertNotNull(tagTree)
    }

    @Test
    fun `login works but error on merging list - empty lists, tag tree`(): Unit = runBlocking {
        initializeContext()
        saveLocalList()
        setLocalListUpdated()

        val userName = "meg@the-list-shop.com"
        val password = "sarrieb1357"

        // swap out shopping list success with failure
        val errorDispatcher = TestDispatcherBuilder("onboarding/signIn")
            .withConfigFile("loginSuccessConfig.json")
            .withConfigFile("getAllShoppingListsConfig.json")
            .withConfigFile("standardTagConfig.json")
            .withConfigFile("userLayoutConfig.json")
            .withConfigFile("userPropertySuccess.json")
            .withConfigFile("defaultLayoutConfig.json")
            .withConfigFile("mergeListFailureConfig.json")
            .withConfigFile("serverListMostRecentConfig.json")
            .build()

        mockWebServer.dispatcher = errorDispatcher

        val connectionStatus = ConnectionStatus.Online

        var result = useCaseProvider?.signIn(userName, password, connectionStatus)
        assertNotNull(result)
        assertTrue(result.isSuccess)
        val shoppingLists = (result.value?.first as TransitionViewState.ListScreen).shoppingLists
        assertNotNull(shoppingLists)
        assertEquals(0, shoppingLists.list.size)

        val shoppingList = (result.value?.first as TransitionViewState.ListScreen).shoppingList
        assertNotNull(shoppingList)
        assertEquals(0, shoppingList.categories.size)

        val tagTree = result.value?.second
        assertNotNull(tagTree)

    }

    private fun saveLocalList() {
        var apiEmbedded = sampleProvider.fillSample<ApiShoppingListEmbeddedList>("standardSingleList")
        apiEmbedded.embeddedList.name = "LOCAL LIST"
        val shoppingList = ShoppingList.Factory.create(apiEmbedded.embeddedList)

        databaseTestHelper?.setShoppingList(shoppingList)
    }


    private fun setLocalListUpdated() {
        val listInfo =
            databaseTestHelper?.standardListInfo()?.copy(localListUpdated = "2021-08-23T15:25:43.511Z")
        databaseTestHelper?.setListInfo(listInfo)

    }

}
