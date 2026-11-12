package com.listshop.bff.usecases.validators

import com.listshop.bff.data.session.UserSession
import com.listshop.bff.data.state.ConnectionStatus
import com.listshop.bff.data.state.UserSessionState
import com.listshop.bff.exceptions.AuthorizationException
import com.listshop.bff.exceptions.OfflineException

interface UserLoggedInValidator {
    fun checkUserLoggedIn(session: UserSession) {
        if (session.sessionState  != UserSessionState.User) {
            throw AuthorizationException("This action requires a logged in user")
        }
    }


}

