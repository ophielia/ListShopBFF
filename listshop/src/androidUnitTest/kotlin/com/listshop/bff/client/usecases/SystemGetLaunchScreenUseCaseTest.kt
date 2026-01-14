package com.listshop.bff.client.usecases


import com.listshop.analytics.Analytics
import com.listshop.analytics.AnalyticsHandle
import com.listshop.analytics.AppInfo
import com.listshop.analytics.ClientType
import com.listshop.analytics.initDummyAnalytics
import com.listshop.bff.TestDatabaseHelper
import com.listshop.bff.TestServiceLocator
import com.listshop.bff.data.model.ShoppingList
import com.listshop.bff.data.remote.ApiShoppingListEmbeddedList
import com.listshop.bff.data.state.ConnectionStatus
import com.listshop.bff.data.state.OnboardingViewState
import com.listshop.bff.data.state.TransitionViewState
import com.listshop.bff.remote.ShoppingListApi
import com.listshop.bff.services.TestSampleProvider
import com.listshop.bff.test.server.TestDispatcherBuilder
import com.listshop.bff.ucp.OnboardingUCP
import junit.framework.TestCase.assertFalse
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Clock
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

    val sampleProvider = TestSampleProvider("src/androidUnitTest/resources/mock/json/launchScreen")

    @BeforeTest
    fun setUp() {
        mockWebServer.start()
        baseUrl = mockWebServer.url("").toString()
        baseUrl = baseUrl.substring(0, baseUrl.length.minus(1) )

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
    fun initializeContext() {
        val appInfo = AppInfo(
            baseUrl = baseUrl,
            name = "name",
            model = "model",
            os = "os",
            osVersion = "osVersion",
            clientType = ClientType.IOS,
            clientVersion = "1.1.0",
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
    fun `client incompatible - should receive error`(): Unit = runBlocking {
        // start with clear database
        databaseTestHelper?.clearDatabase()

        val errorDispatcher = TestDispatcherBuilder("launchScreen")
            .withConfigFile("launchScreenIncompatibleConfig.json")
            .build()
        mockWebServer.dispatcher = errorDispatcher

        val connectionStatus = ConnectionStatus.Online

        var result = useCaseProvider?.systemGetLaunchScreen(connectionStatus)
        assertNotNull(result)
        assertEquals("Current version 1.1.0, Required Version 8.0", result._error?.message)

        // restore original dispatcher
    }

    @Test
    fun `launch with logged in user, offline - should go to local list`(): Unit = runBlocking {
        // setup database / context
        initializeContext()
        val loggedInUser = databaseTestHelper?.standardUser()
        databaseTestHelper?.setUser(loggedInUser)
        loadStandardListLocally()

        val connectionStatus = ConnectionStatus.Offline

        val offlineDispatcher = TestDispatcherBuilder("launchScreen")
            .withConfigFile("launchScreenCompatibleConfig.json")
            .build()
        mockWebServer.dispatcher = offlineDispatcher

        var result = useCaseProvider?.systemGetLaunchScreen(connectionStatus)
        assertNotNull(result)
        assertTrue(result.isSuccess)
        assertTrue(result.value?.first is TransitionViewState)
        assertTrue(result.value?.first is TransitionViewState.ListScreen)

        val shoppingList = (result.value?.first as TransitionViewState.ListScreen).shoppingList
        assertNotNull(shoppingList)

        val shoppingLists = (result.value?.first as TransitionViewState.ListScreen).shoppingLists
        assertNotNull(shoppingLists)
        assertTrue(shoppingLists.list.isEmpty())

        val tagTree = result.value?.second
        assertNotNull(tagTree)
    }


    @Test
    fun `launch with logged in user, online - should merge local list`(): Unit = runBlocking {
        // setup database / context
        initializeContext()
        val loggedInUser = databaseTestHelper?.standardUser()
        databaseTestHelper?.setUser(loggedInUser)
        loadStandardListLocally()

        val anonymousDispatcher = TestDispatcherBuilder("launchScreen")
            .withConfigFile("launchScreenCompatibleConfig.json")
            .withConfigFile("standardTagConfig.json")
            .withConfigFile("defaultLayoutConfig.json")
            .withConfigFile("userLayoutConfig.json")
            .withConfigFile("listOfListsConfig.json")
            .withConfigFile("mergeListConfig.json")
            .build()
        mockWebServer.dispatcher = anonymousDispatcher

        val connectionStatus = ConnectionStatus.Online

        var result = useCaseProvider?.systemGetLaunchScreen(connectionStatus)
        assertNotNull(result)
        var viewState = result.value?.first
        assertNotNull(viewState)
        val shoppingLists = (viewState as TransitionViewState.ListScreen).shoppingLists
        assertNotNull(shoppingLists)
        assertFalse(shoppingLists.list.isEmpty())
        val shoppingList =  (viewState as TransitionViewState.ListScreen).shoppingList
        assertNotNull(shoppingList)
        assertEquals("MergedWithServer", shoppingList.name)

        val tagTree = result.value?.second
        assertNotNull(tagTree)

    }

    @Test
    fun `launch with logged in user, list deleted - go to most recent list`(): Unit = runBlocking {
        // setup database / context
        initializeContext()
        val loggedInUser = databaseTestHelper?.standardUser()
        databaseTestHelper?.setUser(loggedInUser)

        val anonymousDispatcher = TestDispatcherBuilder("launchScreen")
            .withConfigFile("launchScreenCompatibleConfig.json")
            .withConfigFile("standardTagConfig.json")
            .withConfigFile("defaultLayoutConfig.json")
            .withConfigFile("userLayoutConfig.json")
            .withConfigFile("listOfListsConfig.json")
            .withConfigFile("getMostRecentListConfig.json")
            .build()
        mockWebServer.dispatcher = anonymousDispatcher

        val connectionStatus = ConnectionStatus.Online

        var result = useCaseProvider?.systemGetLaunchScreen(connectionStatus)
        assertNotNull(result)
        var viewState = result.value?.first
        assertNotNull(viewState)
        val shoppingLists = (viewState as TransitionViewState.ListScreen).shoppingLists
        assertNotNull(shoppingLists)
        assertFalse(shoppingLists.list.isEmpty())
        val shoppingList =  (viewState as TransitionViewState.ListScreen).shoppingList
        assertNotNull(shoppingList)
        assertEquals("MostRecentList", shoppingList.name)

        val tagTree = result.value?.second
        assertNotNull(tagTree)

    }

    @Test
    fun `launch with logged out user, go to onboarding`(): Unit = runBlocking {
        // setup database / context
        initializeContext()
        val loggedInUser = databaseTestHelper?.standardUser()?.copy( userToken = null, userLastSeen = Clock.System.now().toString())
        databaseTestHelper?.setUser(loggedInUser)

        val loggedOutDispatcher = TestDispatcherBuilder("launchScreen")
            .withConfigFile("launchScreenCompatibleConfig.json")
            .withConfigFile("standardTagConfig.json")
            .withConfigFile("defaultLayoutConfig.json")
            .withConfigFile("userLayoutConfig.json")
            .build()
        mockWebServer.dispatcher = loggedOutDispatcher

        val connectionStatus = ConnectionStatus.Online

        var result = useCaseProvider?.systemGetLaunchScreen(connectionStatus)
        assertNotNull(result)
        var viewState = result.value?.first
        assertNotNull(viewState)
        assertTrue(viewState is TransitionViewState.Onboarding)
        assertEquals(OnboardingViewState.Choose, (viewState as TransitionViewState.Onboarding).state)

        val tagTree = result.value?.second
        assertNotNull(tagTree)
    }

    @Test
    fun `launch with anonymous user, go to local list`(): Unit = runBlocking {
        // setup database / context
        initializeContext()
        val loggedInUser = databaseTestHelper?.standardUser()?.copy(userName = null, userToken = null, userLastSeen = Clock.System.now().toString())
        databaseTestHelper?.setUser(loggedInUser)
        val listInfo = databaseTestHelper?.standardListInfo()?.copy()
        databaseTestHelper?.setList(listInfo)
        loadStandardListLocally()

        val anonymousDispatcher = TestDispatcherBuilder("launchScreen")
            .withConfigFile("launchScreenCompatibleConfig.json")
            .withConfigFile("standardTagConfig.json")
            .withConfigFile("defaultLayoutConfig.json")
            .withConfigFile("userLayoutConfig.json")
            .build()
        mockWebServer.dispatcher = anonymousDispatcher

        val connectionStatus = ConnectionStatus.Online

        var result = useCaseProvider?.systemGetLaunchScreen(connectionStatus)
        assertNotNull(result)
        var viewState = result.value?.first
        assertNotNull(viewState)
        val shoppingLists = (viewState as TransitionViewState.ListScreen).shoppingLists
        assertNotNull(shoppingLists)
        assertTrue(shoppingLists.list.isEmpty())
        val shoppingList =  (viewState as TransitionViewState.ListScreen).shoppingList
        assertNotNull(shoppingList)
        assertEquals("StandardLocalList", shoppingList.name)

        val tagTree = result.value?.second
        assertNotNull(tagTree)

    }

    @Test
    fun `launch with anonymous user, first time user -  go to greeting`(): Unit = runBlocking {
        // setup database / context
        initializeContext()
        // no user set up - this user is anonymous

        val anonymousDispatcher = TestDispatcherBuilder("launchScreen")
            .withConfigFile("launchScreenCompatibleConfig.json")
            .withConfigFile("standardTagConfig.json")
            .withConfigFile("defaultLayoutConfig.json")
            .withConfigFile("userLayoutConfig.json")
            .build()
        mockWebServer.dispatcher = anonymousDispatcher


        val connectionStatus = ConnectionStatus.Online

        var result = useCaseProvider?.systemGetLaunchScreen(connectionStatus)
        assertNotNull(result)
        var viewState = result.value?.first
        assertNotNull(viewState)
        assertTrue(viewState is TransitionViewState.Guides)

        val tagTree = result.value?.second
        assertNotNull(tagTree)


    }

    @Test
    fun `launch with first time user, no list - go to greeting`(): Unit = runBlocking {
        // setup database / context
        initializeContext()
        val loggedInUser = databaseTestHelper?.standardUser()?.copy(userName = null, userToken = null, userLastSeen = null)
        databaseTestHelper?.setUser(loggedInUser)

        val anonymousDispatcher = TestDispatcherBuilder("launchScreen")
            .withConfigFile("launchScreenCompatibleConfig.json")
            .withConfigFile("standardTagConfig.json")
            .withConfigFile("defaultLayoutConfig.json")
            .withConfigFile("userLayoutConfig.json")
            .build()
        mockWebServer.dispatcher = anonymousDispatcher

        val connectionStatus = ConnectionStatus.Online

        var result = useCaseProvider?.systemGetLaunchScreen(connectionStatus)
        assertNotNull(result)
        var viewState = result.value?.first
        assertNotNull(viewState)
        assertTrue(viewState is TransitionViewState.Guides)

        val tagTree = result.value?.second
        assertNotNull(tagTree)

    }


    private fun loadStandardListLocally() {
        val apiEmbedded = sampleProvider.fillSample<ApiShoppingListEmbeddedList>("standardListAsApi")
        val shoppingList = ShoppingList.create(apiEmbedded.embeddedList)
        databaseTestHelper?.setShoppingList(shoppingList)
    }

}
