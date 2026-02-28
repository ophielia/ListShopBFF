package com.listshop.bff.services.impl


import com.listshop.analytics.AppInfo
import com.listshop.analytics.ClientType
import com.listshop.bff.data.model.TagType
import com.listshop.bff.data.remote.ApiTag
import com.listshop.bff.remote.TagApi
import com.listshop.bff.repositories.TagRepository
import com.listshop.bff.services.LayoutService
import com.listshop.bff.services.SessionService
import com.listshop.bff.services.TestUtils
import dev.mokkery.answering.calls
import dev.mokkery.everySuspend
import dev.mokkery.matcher.capture.Capture
import dev.mokkery.matcher.capture.capture
import dev.mokkery.matcher.capture.get
import dev.mokkery.mock
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class TagServiceImplTest {

    val tagRepo = mock<TagRepository>()
    val remoteApi = mock<TagApi>()
    val layoutService = mock<LayoutService>()
    val sessionService = mock<SessionService>()

    var service: TagServiceImpl? = null

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
        service = TagServiceImpl(
            tagApi = remoteApi,
            tagRepo = tagRepo,
            layoutService = layoutService,
            sessionService = sessionService,
            appInfo = appInfo
        )
    }

    @Test
    fun dummyTest() {
        val rufus = "rufus"
        assertEquals(rufus, "rufus")
    }


    @Test
    fun `when i call retrieveTagsAndSaveLocally the tags are retrieved and saved`() = runTest {
        val dummyTagList = TestUtils.dummyApiTagList()
        var deleteCallCount = 0
        var insertCallCount = 0
        everySuspend { remoteApi.retrieveApiTags() } calls { (_: Unit) ->
            delay(500)
            dummyTagList
        }

        everySuspend { tagRepo.deleteAll() } calls { (_: Unit) ->
            deleteCallCount++
        }

        val savedTagList = Capture.slot<List<ApiTag>>()
        everySuspend { tagRepo.insertApiTagsLocally(capture(savedTagList)) } calls { inputList ->
            insertCallCount++
        }

        service?.retrieveTagsAndSaveLocally()
        assertEquals(1, insertCallCount, "Insert call count incorrect")
        assertEquals(1, deleteCallCount, "Delete call count incorrect")
        assertNotNull(savedTagList.get())
        assertEquals(dummyTagList.size, savedTagList.get().size, "same tags should be passed to save")
    }

    @Test
    fun `when i call buildTagTree a TagTree is returned`() = runTest {
        val dummyTagList = TestUtils.dummyTagStructure()
        val typesForTreeAsStrings = TagType.entries.map { it.display }

        everySuspend { tagRepo.findTagsByTypes(typesForTreeAsStrings) } calls { (_: Unit) ->
            delay(500)
            dummyTagList
        }

        val callResult = service?.buildTagTree()
        // later, when accessing tag tree is possible, will need to check this list
        assertNotNull(callResult)
    }


}
