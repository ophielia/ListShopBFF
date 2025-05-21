package com.listshop.bff.services

import com.listshop.bff.data.state.UserSessionState

data class UserSession(
    var userName: String?,
    var userToken: String?,
    var userLastSeen: String?,
    var userLastSignedIn: String?,
    var sessionState: UserSessionState,
    var appVersion: String,
    var appBuild: String,
    var baseUrl: String
) {
}