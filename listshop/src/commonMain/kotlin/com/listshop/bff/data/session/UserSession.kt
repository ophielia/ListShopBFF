package com.listshop.bff.data.session

import com.listshop.bff.data.state.UserSessionState

data class UserSession(
    var userName: String?,
    var userToken: String?,
    var userLastSeen: String?,
    var userLastSignedIn: String?,
    var sessionState: UserSessionState,
    var userProperties: Map<String,String> = mapOf(),
    var dishSession: DishSessionMemory? = null,
    var appVersion: String,
    var appBuild: String,
    var baseUrl: String
) {
}
