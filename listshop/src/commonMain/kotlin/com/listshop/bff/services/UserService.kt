package com.listshop.bff.services

interface UserService {

    suspend fun authenticateUser()
    suspend fun logoutUser()
    suspend fun signInUser(userName: String, password: String)

}