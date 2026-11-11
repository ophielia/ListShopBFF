package com.listshop.bff.client.usecases.list

import com.listshop.analytics.*
import com.listshop.bff.TestDatabaseHelper
import com.listshop.bff.TestServiceLocator
import com.listshop.bff.data.bff.BFFErrorSubtype
import com.listshop.bff.data.bff.BFFErrorType
import com.listshop.bff.data.bff.BFFResult
import com.listshop.bff.data.model.ShoppingList
import com.listshop.bff.data.remote.ApiShoppingList
import com.listshop.bff.data.state.ConnectionStatus
import com.listshop.bff.services.TestSampleProvider
import com.listshop.bff.test.server.TestDispatcherBuilder
import com.listshop.bff.ucp.ListUCP
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import okhttp3.mockwebserver.MockWebServer
import kotlin.test.*

class CheckOffItemTest {

    var useCaseProvider: ListUCP? = null

    val mockWebServer = MockWebServer()

    var analyticsHandle: AnalyticsHandle? = null

    var databaseTestHelper: TestDatabaseHelper? = null

    var baseUrl: String = ""

    val sampleProvider = TestSampleProvider("src/androidHostTest/resources/mock/json/list/checkOffItem")

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

        useCaseProvider = locator.listUCP
    }

    // Happy Paths
    @Test
    fun `when I check off an item while online and logged in, operation succeeds`(): Unit = runBlocking {
        //   • Setup: connectionStatus = Online, UserSessionState = User.
        initializeContext()
        val loggedInUser = databaseTestHelper?.standardUser()
            ?.copy(userName = "george", userToken = "abcdefg", userLastSeen = Clock.System.now().toString())
        databaseTestHelper?.setUser(loggedInUser)
        databaseTestHelper?.setServerListId("12345")
        saveLocalList()
        val connectionStatus = ConnectionStatus.Online

        // check off item succeeds
        val testDispatcher = TestDispatcherBuilder("list")
            .withConfigFile("checkOffTrueWithItemIdSuccess.json")
            .withConfigFile("getCurrentListSuccessCheckOff.json")
            .build()

        mockWebServer.dispatcher = testDispatcher
        var result = useCaseProvider?.checkOffItem(
            listId = "12345",
            itemId = "110113",
            tagId = "65",
            true,
            ConnectionStatus.Online
        )

        assertNotNull(result)
        assertTrue(result is BFFResult.Success<ShoppingList>)
        val shoppingList: ShoppingList = result.value
        assertNotNull(shoppingList)
        // doesn't make sense to check the contents, here
        // contents were overwritten by the server, which is dummy data
        // contents will need to be checked - local contents, anyway, in offline test
    }

    @Test
    fun `when I check off an item while offline, operation succeeds `(): Unit = runBlocking {
        //  Test  Offline & Logged In (Local Only):
        val testStart = Clock.System.now()
        initializeContext()
        val loggedInUser = databaseTestHelper?.standardUser()
            ?.copy(userName = "george", userToken = "abcdefg", userLastSeen = Clock.System.now().toString())
        databaseTestHelper?.setUser(loggedInUser)
        databaseTestHelper?.setServerListId("12345")
        saveLocalList()
        val connectionStatus = ConnectionStatus.Offline

        var result = useCaseProvider?.checkOffItem(
            listId = "12345",
            itemId = "110113",
            tagId = "65",
            true,
            connectionStatus = connectionStatus
        )

        assertNotNull(result)
        assertTrue(result is BFFResult.Success<ShoppingList>)
        val shoppingList: ShoppingList = result.value
        assertNotNull(shoppingList)
        // assert item is checked off
        val operationItem = shoppingList.categories.first().items.filter { it.externalId == "110113" }.first()
        assertNotNull(operationItem)
        assertNotNull(operationItem.crossedOff)
        assertNotNull(operationItem.crossedOff)
        val crossedOffInstant = Instant.parse(operationItem.crossedOff ?: "")
        assertTrue(crossedOffInstant > testStart)
    }

    @Test
    fun `when I check off an item while offline and logged out, operation succeeds `(): Unit = runBlocking{
        //    • Offline & Logged In (Local Only)
        val testStart = Clock.System.now()
        initializeContext()
        databaseTestHelper?.setServerListId("12345")
        saveLocalList()
        val connectionStatus = ConnectionStatus.Offline

        var result = useCaseProvider?.checkOffItem(
            listId = "12345",
            itemId = "110113",
            tagId = "65",
            true,
            connectionStatus = connectionStatus
        )

        assertNotNull(result)
        assertTrue(result is BFFResult.Success<ShoppingList>)
        val shoppingList: ShoppingList = result.value
        assertNotNull(shoppingList)
        // assert item is checked off
        val operationItem = shoppingList.categories.first().items.filter { it.externalId == "110113" }.first()
        assertNotNull(operationItem)
        assertNotNull(operationItem.crossedOff)
        assertNotNull(operationItem.crossedOff)
        val crossedOffInstant = Instant.parse(operationItem.crossedOff ?: "")
        assertTrue(crossedOffInstant > testStart)
    }

    @Test
    fun `when I check off an item while online and logged out, operation succeeds `(): Unit = runBlocking{
        //    • Offline & Logged In (Local Only)
        val testStart = Clock.System.now()
        initializeContext()
        databaseTestHelper?.setServerListId("12345")
        saveLocalList()
        val connectionStatus = ConnectionStatus.Online

        var result = useCaseProvider?.checkOffItem(
            listId = "12345",
            itemId = "110113",
            tagId = "65",
            true,
            connectionStatus = connectionStatus
        )

        assertNotNull(result)
        assertTrue(result is BFFResult.Success<ShoppingList>)
        val shoppingList: ShoppingList = result.value
        assertNotNull(shoppingList)

        // assert item is checked off
        val operationItem = shoppingList.categories.first().items.filter { it.externalId == "110113" }.first()
        assertNotNull(operationItem)
        assertNotNull(operationItem.crossedOff)
        assertNotNull(operationItem.crossedOff)
        val crossedOffInstant = Instant.parse(operationItem.crossedOff ?: "")
        assertTrue(crossedOffInstant > testStart)
    }

    @Test
    fun `when I check off an item using a tag while offline, operation succeeds `(): Unit = runBlocking {
        //  Test  Offline & Logged In (Local Only):
        val testStart = Clock.System.now()
        initializeContext()
        val loggedInUser = databaseTestHelper?.standardUser()
            ?.copy(userName = "george", userToken = "abcdefg", userLastSeen = Clock.System.now().toString())
        databaseTestHelper?.setUser(loggedInUser)
        databaseTestHelper?.setServerListId("12345")
        saveLocalList()
        val connectionStatus = ConnectionStatus.Offline

        var result = useCaseProvider?.checkOffItem(
            listId = "12345",
            itemId = null,
            tagId = "65",
            true,
            connectionStatus = connectionStatus
        )

        assertNotNull(result)
        assertTrue(result is BFFResult.Success<ShoppingList>)
        val shoppingList: ShoppingList = result.value
        assertNotNull(shoppingList)

        // assert item is checked off
        val operationItem = shoppingList.categories.first().items.filter { it.externalId == "110113" }.first()
        assertNotNull(operationItem)
        assertNotNull(operationItem.crossedOff)
        assertNotNull(operationItem.crossedOff)
        val crossedOffInstant = Instant.parse(operationItem.crossedOff ?: "")
        assertTrue(crossedOffInstant > testStart)
    }

    @Test
    fun `when I check off an item using a tag while offline and logged out, operation succeeds `(): Unit = runBlocking{
        //    • Offline & Logged In (Local Only)
        val testStart = Clock.System.now()
        initializeContext()
        databaseTestHelper?.setServerListId("12345")
        saveLocalList()
        val connectionStatus = ConnectionStatus.Offline

        var result = useCaseProvider?.checkOffItem(
            listId = "12345",
            itemId = null,
            tagId = "65",
            true,
            connectionStatus = connectionStatus
        )

        assertNotNull(result)
        assertTrue(result is BFFResult.Success<ShoppingList>)
        val shoppingList: ShoppingList = result.value
        assertNotNull(shoppingList)
        // assert item is checked off
        val operationItem = shoppingList.categories.first().items.filter { it.externalId == "110113" }.first()
        assertNotNull(operationItem)
        assertNotNull(operationItem.crossedOff)
        assertNotNull(operationItem.crossedOff)
        val crossedOffInstant = Instant.parse(operationItem.crossedOff ?: "")
        assertTrue(crossedOffInstant > testStart)
    }

    @Test
    fun `when I check off an item using a tag while online and logged out, operation succeeds `(): Unit = runBlocking{
        //    • Offline & Logged In (Local Only)
        val testStart = Clock.System.now()
        initializeContext()
        databaseTestHelper?.setServerListId("12345")
        saveLocalList()
        val connectionStatus = ConnectionStatus.Online

        var result = useCaseProvider?.checkOffItem(
            listId = "12345",
            itemId = null,
            tagId = "65",
            true,
            connectionStatus = connectionStatus
        )

        assertNotNull(result)
        assertTrue(result is BFFResult.Success<ShoppingList>)
        val shoppingList: ShoppingList = result.value
        assertNotNull(shoppingList)

        // assert item is checked off
        val operationItem = shoppingList.categories.first().items.filter { it.externalId == "110113" }.first()
        assertNotNull(operationItem)
        assertNotNull(operationItem.crossedOff)
        assertNotNull(operationItem.crossedOff)
        val crossedOffInstant = Instant.parse(operationItem.crossedOff ?: "")
        assertTrue(crossedOffInstant > testStart)
    }

    @Test
    fun `when I un-check off an item while online and logged in, operation succeeds`(): Unit = runBlocking {
        //   • Setup: connectionStatus = Online, UserSessionState = User.
        val itemId = "110174" // bananna - already crossed off
        val tagId = "50615" // bananna tag id
        initializeContext()
        val loggedInUser = databaseTestHelper?.standardUser()
            ?.copy(userName = "george", userToken = "abcdefg", userLastSeen = Clock.System.now().toString())
        databaseTestHelper?.setUser(loggedInUser)
        databaseTestHelper?.setServerListId("12345")
        saveLocalList()
        val connectionStatus = ConnectionStatus.Online

        // check off item succeeds
        val testDispatcher = TestDispatcherBuilder("list")
            .withConfigFile("checkOffFalseBananaWithItemIdSuccess.json")
            .withConfigFile("getCurrentListSuccessCheckOff.json")
            .build()

        mockWebServer.dispatcher = testDispatcher
        var result = useCaseProvider?.checkOffItem(
            listId = "12345",
            itemId = itemId,
            tagId = tagId,
            false,
            ConnectionStatus.Online
        )

        assertNotNull(result)
        assertTrue(result is BFFResult.Success<ShoppingList>)
        val shoppingList: ShoppingList = result.value
        assertNotNull(shoppingList)
        // doesn't make sense to check the contents, here
        // contents were overwritten by the server, which is dummy data
        // contents will need to be checked - local contents, anyway, in offline test
    }

    @Test
    fun `when I un-check off an item while offline, operation succeeds `(): Unit = runBlocking {
        //  Test  Offline & Logged In (Local Only):
        val itemId = "110174" // bananna - already crossed off
        val tagId = "50615" // bananna tag id
        val testStart = Clock.System.now()
        initializeContext()
        val loggedInUser = databaseTestHelper?.standardUser()
            ?.copy(userName = "george", userToken = "abcdefg", userLastSeen = Clock.System.now().toString())
        databaseTestHelper?.setUser(loggedInUser)
        databaseTestHelper?.setServerListId("12345")
        saveLocalList()
        val connectionStatus = ConnectionStatus.Offline

        var result = useCaseProvider?.checkOffItem(
            listId = "12345",
            itemId = itemId,
            tagId = tagId,
            false,
            connectionStatus = connectionStatus
        )

        assertNotNull(result)
        assertTrue(result is BFFResult.Success<ShoppingList>)
        val shoppingList: ShoppingList = result.value
        assertNotNull(shoppingList)
        // assert item is checked off
        val operationItem = shoppingList.categories.first().items.filter { it.externalId == "110113" }.first()
        assertNotNull(operationItem)
        assertNull(operationItem.crossedOff)
    }

    @Test
    fun `when I un-check off an un-checked off item while offline, operation succeeds `(): Unit = runBlocking {
        //  Test  Offline & Logged In (Local Only):
        val itemId = "110174" // bananna - already crossed off
        val tagId = "50615" // bananna tag id
        val testStart = Clock.System.now()
        initializeContext()
        val loggedInUser = databaseTestHelper?.standardUser()
            ?.copy(userName = "george", userToken = "abcdefg", userLastSeen = Clock.System.now().toString())
        databaseTestHelper?.setUser(loggedInUser)
        databaseTestHelper?.setServerListId("12345")
        saveLocalList()
        val connectionStatus = ConnectionStatus.Offline

        var result = useCaseProvider?.checkOffItem(
            listId = "12345",
            itemId = itemId,
            tagId = tagId,
            false,
            connectionStatus = connectionStatus
        )

        assertNotNull(result)
        assertTrue(result is BFFResult.Success<ShoppingList>)
        val shoppingList: ShoppingList = result.value
        assertNotNull(shoppingList)
        // assert item is checked off
        val operationItem = shoppingList.categories.first().items.filter { it.externalId == "110113" }.first()
        assertNotNull(operationItem)
        assertNull(operationItem.crossedOff)
    }

    // Error / Validation Paths
    @Test
    fun `when I check off an item using a tag while online and logged in, operation fails`(): Unit = runBlocking {
        //   • Setup: connectionStatus = Online, UserSessionState = User.
        initializeContext()
        val loggedInUser = databaseTestHelper?.standardUser()
            ?.copy(userName = "george", userToken = "abcdefg", userLastSeen = Clock.System.now().toString())
        databaseTestHelper?.setUser(loggedInUser)
        databaseTestHelper?.setServerListId("12345")
        saveLocalList()
        val connectionStatus = ConnectionStatus.Online

        var result = useCaseProvider?.checkOffItem(
            listId = "12345",
            itemId = null,
            tagId = "65",
            true,
            connectionStatus = connectionStatus
        )

        assertNotNull(result)
        assertTrue(result is BFFResult.Failure)
        val error = (result as BFFResult.Failure).error
        assertEquals("Item id cannot be null for server call", error.message)
        assertEquals(BFFErrorType.VALIDATION, error.type)
        assertEquals(BFFErrorSubtype.INVALID_INPUT, error.subType)
    }

    @Test
    fun `when I check off an item with a mismatched list ID, an exception occurs`(): Unit = runBlocking {
        //• Setup: Provide a listId that does not match the serverListId returned by sessionService.currentListSession().
        //   • Setup: connectionStatus = Online, UserSessionState = User.
        initializeContext()
        val loggedInUser = databaseTestHelper?.standardUser()
            ?.copy(userName = "george", userToken = "abcdefg", userLastSeen = Clock.System.now().toString())
        databaseTestHelper?.setUser(loggedInUser)
        databaseTestHelper?.setServerListId("12345")
        saveLocalList()
        val connectionStatus = ConnectionStatus.Online

        var result = useCaseProvider?.checkOffItem(
            listId = "23456",
            itemId = "110113",
            tagId = "65",
            true,
            connectionStatus
        )

        assertNotNull(result)
        assertTrue(result is BFFResult.Failure)
        val error = (result as BFFResult.Failure).error
        assertEquals("List id does not match current list id in session", error.message)
        assertEquals(BFFErrorType.VALIDATION, error.type)
        assertEquals(BFFErrorSubtype.INVALID_INPUT, error.subType)

    }

    @Test
    fun `when I check off an item with a mismatched local list IDs, an exception occurs`(): Unit = runBlocking {
        //• Setup: Provide a listId that does not match the serverListId returned by sessionService.currentListSession().
        //   • Setup: connectionStatus = Online, UserSessionState = User.
        initializeContext()

        databaseTestHelper?.setServerListId("12345")
        saveLocalList()
        val connectionStatus = ConnectionStatus.Online

        var result = useCaseProvider?.checkOffItem(
            listId = "23456",
            itemId = "110113",
            tagId = "65",
            true,
            ConnectionStatus.Online
        )

        assertNotNull(result)
        assertTrue(result is BFFResult.Failure)
        val error = (result as BFFResult.Failure).error
        assertEquals("List id does not match current list id in session", error.message)
        assertEquals(BFFErrorType.VALIDATION, error.type)
        assertEquals(BFFErrorSubtype.INVALID_INPUT, error.subType)

    }

    @Test
    fun `when I check off an item locally with null list ids, no exceptions occur`(): Unit = runBlocking {
        //• Setup: Provide a null list id and non null serverListId - should succeed
        initializeContext()
        databaseTestHelper?.setServerListId("12345")
        saveLocalList()
        databaseTestHelper?.setLocalListId(null)
        val connectionStatus = ConnectionStatus.Online

        var result = useCaseProvider?.checkOffItem(
            listId = null,
            itemId = "110113",
            tagId = "65",
            true,
            connectionStatus = connectionStatus
        )

        assertNotNull(result)
        assertTrue(result is BFFResult.Success<*>)
    }

    @Test
    fun `when I check off an item locally with all null list ids, no exceptions occur`(): Unit = runBlocking {
        //• Setup: Provide a null list id and serverListId - should succeed
        initializeContext()
        saveLocalList()
        databaseTestHelper?.setLocalListId(null)
        val connectionStatus = ConnectionStatus.Online

        var result = useCaseProvider?.checkOffItem(
            listId = null,
            itemId = "110113",
            tagId = "65",
            true,
            connectionStatus = connectionStatus
        )

        assertNotNull(result)
        assertTrue(result is BFFResult.Success<*>)
    }



    @AfterTest
    fun shutDown() {
        mockWebServer.shutdown()
    }

    private fun saveLocalList() {
        var apiEmbedded = sampleProvider.fillSample<ApiShoppingList>("checkOffList")
        apiEmbedded.name = "LOCAL LIST"
        val shoppingList = ShoppingList.create(apiEmbedded)

        databaseTestHelper?.setShoppingList(shoppingList)
    }

}


/*



3. Error Handling & Analytics
fun `when I check off an item, and server is not available, an exception occurs`(){
• Server Communication Failure:
• Setup: Mock listService.checkOffItemOnServer to throw an exception.
• Expected: The exception is caught, analyticsHandle.error is called, and a BFFResult.Failure is returned.

}

 */

