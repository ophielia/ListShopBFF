package com.listshop.bff.services.impl

import com.listshop.analytics.Analytics
import com.listshop.analytics.initDummyAnalytics
import com.listshop.bff.data.remote.PostShoppingList
import com.listshop.bff.remote.ShoppingListApi
import com.listshop.bff.repositories.ListRepository
import com.listshop.bff.services.SessionService
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.capture.Capture
import dev.mokkery.matcher.capture.capture
import dev.mokkery.matcher.capture.get
import dev.mokkery.mock
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ListServiceImplTest {
    private val remoteApi = mock<ShoppingListApi>()
    private val listRepo = mock<ListRepository>()
    private val sessionService = mock<SessionService>()
    private val analytics = mock<Analytics>()
    private val listShopAnalytics = initDummyAnalytics(analytics).listShopAnalytics

    private lateinit var service: ListServiceImpl

    @BeforeTest
    fun setUp() {
        service = ListServiceImpl(
            remoteApi = remoteApi,
            listRepo = listRepo,
            sessionService = sessionService,
            listShopAnalytics = listShopAnalytics
        )
    }

    @Test
    fun `when addServerList is called then remoteApi createList is invoked`() = runTest {
        val dummyId = "new-list-123"
        val payloadCapture = Capture.slot<PostShoppingList>()

        everySuspend { remoteApi.createList(capture(payloadCapture)) } returns dummyId

        val result = service.addServerList()

        assertEquals(dummyId, result)
        assertNull(payloadCapture.get().name)
    }
}
