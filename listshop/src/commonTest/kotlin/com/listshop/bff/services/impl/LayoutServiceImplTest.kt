package com.listshop.bff.services.impl

import com.listshop.analytics.Analytics
import com.listshop.analytics.AppInfo
import com.listshop.analytics.ClientType
import com.listshop.analytics.ListShopAnalytics
import com.listshop.analytics.initDummyAnalytics
import com.listshop.bff.data.remote.ApiLayout
import com.listshop.bff.data.remote.ApiLayoutCategory
import com.listshop.bff.data.remote.ApiTag
import com.listshop.bff.remote.LayoutApi
import com.listshop.bff.repositories.LayoutRepository
import com.listshop.bff.services.SessionService
import com.listshop.bff.services.TestUtils
import dev.mokkery.answering.calls
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.capture.Capture
import dev.mokkery.matcher.capture.capture
import dev.mokkery.matcher.capture.get
import dev.mokkery.mock
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class LayoutServiceImplTest {

    val layoutApi = mock<LayoutApi>()
    val layoutRepo = mock<LayoutRepository>()
    val sessionService = mock<SessionService>()
    val analytics = mock<Analytics>()
    val listShopAnalytics = initDummyAnalytics(analytics).listShopAnalytics

    lateinit var service: LayoutServiceImpl

    @BeforeTest
    fun setUp() {
        val appInfo = AppInfo(
            baseUrl = "baseUrl",
            name = "name",
            model = "model",
            os = "os",
            osVersion = "osVersion",
            clientType = ClientType.IOS,
            clientVersion = "clientVersion",
            buildNumber = "buildNumber",
            deviceId = "deviceId"
        )
        service = LayoutServiceImpl(
            layoutApi = layoutApi,
            layoutRepo = layoutRepo,
            sessionService = sessionService,
            appInfo = appInfo,
            listShopAnalytics = listShopAnalytics
        )
    }

    @Test
    fun `when i call retrieveLayoutsAndSaveLocally the layouts are cleared retrieved and saved`() = runTest {
        val dummyLayoutList = TestUtils.dummyApiLayoutList()
        var clearCallCount = 0
        var saveCallCount = 0

        every { layoutRepo.clearLayoutDataLocally() } calls { (_: Unit) ->
            clearCallCount++
        }

        everySuspend { layoutApi.retrieveAllLayouts() } returns dummyLayoutList

        val savedLayout = Capture.slot<ApiLayout>()
        every { layoutRepo.saveLayoutLocally(capture(savedLayout)) } calls {
            saveCallCount++
        }

        service.retrieveLayoutsAndSaveLocally()

        assertEquals(1, clearCallCount, "Clear call count incorrect")
        assertEquals(dummyLayoutList.size, saveCallCount, "Save call count incorrect")
        assertNotNull(savedLayout.get())
        assertEquals(dummyLayoutList.first().externalId, savedLayout.get().externalId)
    }

    @Test
    fun `when i call clearUserLayouts the layouts are cleared retrieved and saved`() = runTest {
        val dummyLayoutList = TestUtils.dummyApiLayoutList()
        var clearCallCount = 0
        var saveCallCount = 0

        every { layoutRepo.clearLayoutDataLocally() } calls { (_: Unit) ->
            clearCallCount++
        }

        everySuspend { layoutApi.retrieveAllLayouts() } returns dummyLayoutList

        val savedLayout = Capture.slot<ApiLayout>()
        every { layoutRepo.saveLayoutLocally(capture(savedLayout)) } calls {
            saveCallCount++
        }

        service.clearUserLayouts()

        assertEquals(1, clearCallCount, "Clear call count incorrect")
        assertEquals(dummyLayoutList.size, saveCallCount, "Save call count incorrect")
        assertNotNull(savedLayout.get())
    }

    @Test
    fun `when i call updateLayoutInformationForTag the layout for tag is retrieved and saved`() = runTest {
        val dummyTag = ApiTag(externalId = "extTagId", name = "tagName")
        val dummyCategory = TestUtils.dummyApiLayoutCategory()

        everySuspend { layoutApi.retrieveLayoutForTag("extTagId") } returns dummyCategory
        
        var saveMappingCount = 0
        val capturedTag = Capture.slot<ApiTag>()
        val capturedCategory = Capture.slot<ApiLayoutCategory>()
        
        every { layoutRepo.saveCategoryMappingLocally(capture(capturedTag), capture(capturedCategory)) } calls {
            saveMappingCount++
        }

        service.updateLayoutInformationForTag(dummyTag)

        assertEquals(1, saveMappingCount)
        assertEquals(dummyTag.externalId, capturedTag.get().externalId)
        assertEquals(dummyCategory.externalId, capturedCategory.get().externalId)
    }

    @Test
    fun `when i call updateLayoutInformationForTag with no external id nothing happens`() = runTest {
        val dummyTag = ApiTag(externalId = null, name = "tagName")

        service.updateLayoutInformationForTag(dummyTag)

        // No interactions should occur. If they do, Mokkery should complain as they aren't mocked.
    }
}
