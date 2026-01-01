package com.listshop.bff.services.impl


import com.listshop.analytics.AppInfo
import com.listshop.analytics.ClientType
import com.listshop.bff.data.remote.ApiTag
import com.listshop.bff.db.UserInfoEntity
import com.listshop.bff.remote.TagApi
import com.listshop.bff.repositories.SessionInfoRepository
import com.listshop.bff.repositories.TagRepository
import com.listshop.bff.repositories.TagRepositoryImpl
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

    val sessionRepo = mock<SessionInfoRepository>()
    val tagRepo = mock<TagRepository>()
    val remoteApi = mock<TagApi>()

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
            sessionRepo, remoteApi, tagRepo, appInfo
        )
    }

    @Test
    fun dummyTest() {
        val rufus = "rufus"
        assertEquals(rufus, "rufus")
    }


    @Test
    fun `when i call retrieveTagsAndSaveLocally the tags are retrieved and saved`() = runTest {
        val dummyTagList = dummyApiTagList()
        var deleteCallCount = 0
        var insertCallCount = 0
        everySuspend { remoteApi.retrieveApiTags()  } calls { (_: Unit) ->
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



private fun dummyUserInfoEntity(): UserInfoEntity? {
    var userInfo = UserInfoEntity(
        userName = "test",
        userToken = "testToken",
        userLastSeen = "yesterday",
        userCreated = "a month ago",
        userLastSignedIn = "two weeks ago"
    )
    return userInfo
}

private fun dummyApiTagList(): List<ApiTag> {
    val tag1 = ApiTag("1", "tag1")
    val tag2 = ApiTag("2", "tag2")
    return listOf(tag1, tag2)
}



}
