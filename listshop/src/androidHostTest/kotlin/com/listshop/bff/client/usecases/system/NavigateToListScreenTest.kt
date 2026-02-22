package com.listshop.bff.client.usecases.system

import com.listshop.analytics.*
import com.listshop.bff.TestDatabaseHelper
import com.listshop.bff.TestServiceLocator
import com.listshop.bff.data.bff.BFFResult
import com.listshop.bff.data.model.ShoppingList
import com.listshop.bff.data.remote.ApiShoppingListEmbeddedList
import com.listshop.bff.data.state.ConnectionStatus
import com.listshop.bff.data.state.TransitionViewState
import com.listshop.bff.services.TestSampleProvider
import com.listshop.bff.test.server.TestDispatcherBuilder
import com.listshop.bff.ucp.SystemUCP
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockWebServer
import kotlin.test.*

class NavigateToListScreenTest {

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
    fun `when i navigate to the list screen, the TVS is correct, with list`(): Unit = runBlocking {
        val serverListId = "1234"
        // setup database / context
        initializeContext()
        setLoggedInUser()
        setServerList(serverListId)

        // configure api calls
        val testDispatcher = TestDispatcherBuilder("system/navigateToListScreen")
            .withConfigFile("serverListConfig.json")
            .withConfigFile("listOfListsConfig.json")
            .build()

        val connectionStatus = ConnectionStatus.Online

        mockWebServer.dispatcher = testDispatcher
        var result = useCaseProvider?.navigateToListScreen(connectionStatus)
        assertNotNull(result)
        assertTrue(result.isSuccess)

       assertTrue(result is BFFResult<TransitionViewState>)
        assertTrue(result.value is TransitionViewState.ListScreen)
        val list = (result.value as TransitionViewState.ListScreen).shoppingList
        assertNotNull(list)
        assertEquals(3, list.categories.size)

        val listOfLists = (result.value as TransitionViewState.ListScreen).shoppingLists
        assertNotNull(listOfLists)
        assertEquals(4, listOfLists.list.size)
    }


    @Test
    fun `when i navigate to the list screen, and the server id list isnt there i get the most recent`(): Unit = runBlocking {
        val serverListId = "1234"
        // setup database / context
        initializeContext()
        setLoggedInUser()
        setServerList(serverListId)

        // configure api calls
        val testDispatcher = TestDispatcherBuilder("system/navigateToListScreen")
            .withConfigFile("serverListFailureConfig.json")
            .withConfigFile("serverListMostRecentConfig.json")
            .withConfigFile("listOfListsConfig.json")
            .build()

        val connectionStatus = ConnectionStatus.Online

        mockWebServer.dispatcher = testDispatcher
        var result = useCaseProvider?.navigateToListScreen(connectionStatus)
        assertNotNull(result)
        assertTrue(result.isSuccess)

        assertTrue(result is BFFResult<TransitionViewState>)
        assertTrue(result.value is TransitionViewState.ListScreen)
        val list = (result.value as TransitionViewState.ListScreen).shoppingList
        assertNotNull(list)
        assertEquals(3, list.categories.size)

        val listOfLists = (result.value as TransitionViewState.ListScreen).shoppingLists
        assertNotNull(listOfLists)
        assertEquals(4, listOfLists.list.size)
    }

    @Test
    fun `when i navigate to the list screen with a logged out user, i get the local list`(): Unit = runBlocking {
        // setup database / context
        initializeContext()
        saveLocalList()

        // configure api calls
        val testDispatcher = TestDispatcherBuilder("system/navigateToListScreen")
            .build()

        val connectionStatus = ConnectionStatus.Online

        mockWebServer.dispatcher = testDispatcher
        var result = useCaseProvider?.navigateToListScreen(connectionStatus)
        assertNotNull(result)
        assertTrue(result.isSuccess)

        assertTrue(result is BFFResult<TransitionViewState>)
        assertTrue(result.value is TransitionViewState.ListScreen)
        val list = (result.value as TransitionViewState.ListScreen).shoppingList
        assertNotNull(list)
        assertEquals("LOCAL LIST", list.name)

        val listOfLists = (result.value as TransitionViewState.ListScreen).shoppingLists
        assertNotNull(listOfLists)
        assertEquals(0, listOfLists.list.size)

    }

    @Test
    fun `when i navigate to the list screen when disconnected, i get the local list`(): Unit = runBlocking {
        // setup database / context
        initializeContext()
        setLoggedInUser()
        saveLocalList()

        // configure api calls
        val testDispatcher = TestDispatcherBuilder("system/navigateToListScreen")
            .build()

        val connectionStatus = ConnectionStatus.Offline

        mockWebServer.dispatcher = testDispatcher
        var result = useCaseProvider?.navigateToListScreen(connectionStatus)
        assertNotNull(result)
        assertTrue(result.isSuccess)

        assertTrue(result is BFFResult<TransitionViewState>)
        assertTrue(result.value is TransitionViewState.ListScreen)
        val list = (result.value as TransitionViewState.ListScreen).shoppingList
        assertNotNull(list)
        assertEquals("LOCAL LIST", list.name)

        val listOfLists = (result.value as TransitionViewState.ListScreen).shoppingLists
        assertNotNull(listOfLists)
        assertEquals(0, listOfLists.list.size)
    }

    @Test
    fun `when i navigate to the list screen and all api calls fail, i get an empty list`(): Unit = runBlocking {
        val serverListId = "1234"
        // setup database / context
        initializeContext()
        setLoggedInUser()
        setServerList(serverListId)

        // configure api calls
        val testDispatcher = TestDispatcherBuilder("system/navigateToListScreen")
            .withConfigFile("listOfListsFailConfig.json")
            .withConfigFile("serverListFailureConfig.json")
            .withConfigFile("serverListMostRecentFailureConfig.json")
            .build()

        val connectionStatus = ConnectionStatus.Online

        mockWebServer.dispatcher = testDispatcher
        var result = useCaseProvider?.navigateToListScreen(connectionStatus)
        assertNotNull(result)
        assertTrue(result.isSuccess)
        assertTrue(result.value is TransitionViewState.ListScreen)
        val list = (result.value as TransitionViewState.ListScreen).shoppingList
        assertNotNull(list)
        assertEquals(0, list.categories.size)

        val listOfLists = (result.value as TransitionViewState.ListScreen).shoppingLists
        assertNotNull(listOfLists)
        assertEquals(0, listOfLists.list.size)
    }


    @Test
    fun `when i should get the local list, and no list exists, i get an empty list`(): Unit = runBlocking {
        // setup database / context
        initializeContext()
        setLoggedInUser()
        saveLocalList()

        // configure api calls
        val testDispatcher = TestDispatcherBuilder("system/navigateToListScreen")
            .build()

        val connectionStatus = ConnectionStatus.Offline

        mockWebServer.dispatcher = testDispatcher
        var result = useCaseProvider?.navigateToListScreen(connectionStatus)
        assertNotNull(result)
        assertTrue(result.isSuccess)

        assertTrue(result is BFFResult<TransitionViewState>)
        assertTrue(result.value is TransitionViewState.ListScreen)
        val list = (result.value as TransitionViewState.ListScreen).shoppingList
        assertNotNull(list)
        assertEquals("LOCAL LIST", list.name)

        val listOfLists = (result.value as TransitionViewState.ListScreen).shoppingLists
        assertNotNull(listOfLists)
        assertEquals(0, listOfLists.list.size)
    }


    private fun setLoggedInUser() {
        val loggedInUser = databaseTestHelper?.standardUser()
        databaseTestHelper?.setUser(loggedInUser)
    }

    private fun setServerList(listId: String ) {
        val listSession = databaseTestHelper?.setServerListId(listId)
    }

    private fun saveLocalList() {
         var apiEmbedded = sampleProvider.fillSample<ApiShoppingListEmbeddedList>("standardSingleList")
        apiEmbedded.embeddedList.name = "LOCAL LIST"
        val shoppingList = ShoppingList.Factory.create(apiEmbedded.embeddedList)

        databaseTestHelper?.setShoppingList(shoppingList)
    }

    @AfterTest
    fun shutDown() {
        mockWebServer.shutdown()
    }
}
