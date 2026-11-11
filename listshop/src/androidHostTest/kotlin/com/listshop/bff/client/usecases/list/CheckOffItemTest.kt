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
        // assert last updated has been updated
        assertNotNull(shoppingList.lastLocalChange)
        val lastUpdated = Instant.parse(shoppingList.lastLocalChange ?: "")
        assertTrue(lastUpdated > testStart)
        // assert item is checked off
        val operationItem = shoppingList.categories.first().items.filter { it.externalId == "110113" }.first()
        assertNotNull(operationItem)
        assertNotNull(operationItem.crossedOff)
        assertNotNull(operationItem.crossedOff)
        val crossedOffInstant = Instant.parse(operationItem.crossedOff ?: "")
        assertTrue(crossedOffInstant > testStart)
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
     Input Validation Scenarios
• List ID Mismatch:
fun `when I check off an item with a mismatched list ID, an exception occurs`(){
//• Setup: Provide a listId that does not match the serverListId returned by sessionService.currentListSession().
//• Expected: The use case should throw (or return as failure) an IllegalArgumentException with the message: "List id does not match current list id in session".
}
//• Null Item ID (Online & Logged In):
fun `when I check of an item while online and logged in but with a null item id, an exception occurs`(){
//• Setup: Set itemId to null, connectionStatus to Online, and user session to UserSessionState.User.
//• Expected: Returns a BFFResult.Failure containing an IllegalArgumentException with the message: "Item id cannot be null for server call".


}
• Null Item ID (Offline or Guest):
fun `when i check off an item offline with a null item id, operation succeeds`() {
//• Setup: Set itemId to null while connectionStatus is Offline or the session state is not User.
//• Expected: Should proceed successfully (returning a Success) because the server-side sync (which requires the ID) is skipped.
}

2. Functional Scenarios (Happy Path)


fun `when I check off an item while online and logged out, operation succeeds `() {
• Offline & Logged In (Local Only):
• Setup: connectionStatus = Offline, UserSessionState = User.
• Verification:
• listService.checkOffItemLocally is called.
• listService.checkOffItemOnServer is not called.
• listService.retrieveOrCreateLocalList is called.
• Result: Returns BFFResult.Success containing the local list.
}


3. Error Handling & Analytics
fun `when I check off an item, and server is not available, an exception occurs`(){
• Server Communication Failure:
• Setup: Mock listService.checkOffItemOnServer to throw an exception.
• Expected: The exception is caught, analyticsHandle.error is called, and a BFFResult.Failure is returned.

}

 */

