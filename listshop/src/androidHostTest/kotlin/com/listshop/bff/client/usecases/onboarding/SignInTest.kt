package com.listshop.bff.client.usecases.onboarding

import com.listshop.analytics.*
import com.listshop.bff.TestDatabaseHelper
import com.listshop.bff.TestServiceLocator
import com.listshop.bff.data.bff.BFFErrorSubtype
import com.listshop.bff.data.bff.BFFErrorType
import com.listshop.bff.data.bff.BFFResult
import com.listshop.bff.data.model.ShoppingList
import com.listshop.bff.data.remote.ApiShoppingList
import com.listshop.bff.data.state.ConnectionStatus
import com.listshop.bff.data.state.TransitionViewState
import com.listshop.bff.services.TagTree
import com.listshop.bff.services.TestSampleProvider
import com.listshop.bff.test.server.TestDispatcherBuilder
import com.listshop.bff.ucp.OnboardingUCP
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockWebServer
import kotlin.test.*

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
        assertTrue(result is BFFResult.Failure)
        // verify the error result
        val error = result.error!!
        assertEquals(BFFErrorType.AUTHENTICATION, error.type)
        assertEquals(BFFErrorSubtype.CANT_LOGIN, error.subType)
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
            .withConfigFile("serverListMostRecentConfig.json")
            .build()

        mockWebServer.dispatcher = originalDispatcher
        val connectionStatus = ConnectionStatus.Online
        var result = useCaseProvider?.signIn(userName, password, connectionStatus)
        assertNotNull(result)
        assertFalse(result is BFFResult.Failure)
        assertTrue(result is BFFResult.Success<Pair<TransitionViewState, TagTree>>)
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
        assertFalse(result is BFFResult.Failure)
        assertTrue(result is BFFResult.Success<Pair<TransitionViewState, TagTree>>)
        // verify the result
        val shoppingLists = (result.value?.first as TransitionViewState.ListScreen).shoppingLists
        assertNotNull(shoppingLists)
        assertEquals(25, shoppingLists.list.size)

        val shoppingList = (result.value?.first as TransitionViewState.ListScreen).shoppingList
        assertNotNull(shoppingList)
        assertEquals(7, shoppingList.categories.size)

        val legend = shoppingList.legend
        assertNotNull(legend)
        assertEquals(13, legend?.points?.size)

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
        assertTrue(result is BFFResult.Success<Pair<TransitionViewState, TagTree>>)
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
        assertTrue(result is BFFResult.Success<Pair<TransitionViewState, TagTree>>)
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
        assertTrue(result is BFFResult.Success<Pair<TransitionViewState, TagTree>>)
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
