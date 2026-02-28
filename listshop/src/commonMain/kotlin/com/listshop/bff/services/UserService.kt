package com.listshop.bff.services

interface UserService {

    suspend fun authenticateUser()
    suspend fun logoutUser(isOffline: Boolean)
    suspend fun signInUser(userName: String, password: String)
    suspend fun checkUserNameTaken(userName: String): Boolean
    suspend fun changePassword(originalPassword: String, newPassword: String)
    suspend fun updateUserProperty(property: String, value: String)
    suspend fun requestPasswordReset(userName: String)
    suspend fun deleteUser()
    suspend fun signUpUser(userName: String, password: String)

}
