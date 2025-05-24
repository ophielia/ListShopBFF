package com.listshop.bff.services.impl


import com.listshop.analytics.AppInfo
import com.listshop.bff.data.model.UserInfo
import com.listshop.bff.data.state.UserSessionState
import com.listshop.bff.db.UserInfoEntity
import com.listshop.bff.repositories.SessionInfoRepository
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.matcher.capture.Capture
import dev.mokkery.matcher.capture.capture
import dev.mokkery.matcher.capture.get
import dev.mokkery.mock
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class UserSessionServiceImplTest {

    val repository = mock<SessionInfoRepository>()

    var service: UserSessionServiceImpl? = null

    @BeforeTest
    fun setUp() {
        val appInfo = AppInfo( baseUrl = "baseUrl",
         name = "name",
         model = "model",
         os = "os",
         osVersion = "osVersion",
         clientVersion = "clientVersion",
         buildNumber = "buildNumber",
         deviceId = "deviceId" )
        service = UserSessionServiceImpl(repository, appInfo)
    }

    @Test
    fun dummyTest() {
        val rufus = "rufus"
        assertEquals(rufus,"rufus")
    }

    @Test
    fun testCurrentSessionExistingUser() {
        var userInfo = dummyUserInfoEntity()
        every { repository.getUserInfo() } returns userInfo

        val userSession = service?.currentSession()
        assertNotNull(userSession)
        assertEquals(userSession.userName,"test")
        assertEquals(userSession.userToken,"testToken")
        assertEquals(userSession.userLastSeen,"yesterday")
        assertEquals(userSession.userLastSignedIn,"two weeks ago")
        assertEquals(userSession.sessionState, UserSessionState.User)
        assertEquals(userSession.appVersion,"clientVersion")
        assertEquals(userSession.baseUrl,"baseUrl")
    }


    @Test
    fun `when i call setUserToken the token is sent to save`() = runTest {
        var userInfo =  dummyUserInfoEntity()
        every { repository.getUserInfo() } returns userInfo

        val savedUserInfo = Capture.slot<UserInfo>()
        every {repository.updateUserInfo(capture(savedUserInfo))} returns Unit

        service?.setUserToken("BRAND NEW TOKEN")
        assertNotNull(savedUserInfo.get())
        assertEquals("BRAND NEW TOKEN", savedUserInfo.get().userToken,)
    }

    private fun dummyUserInfoEntity(): UserInfoEntity? {
        var userInfo = UserInfoEntity(     userName = "test",
            userToken = "testToken",
            userLastSeen = "yesterday",
            userCreated = "a month ago",
            userLastSignedIn = "two weeks ago")
        return userInfo
    }


}