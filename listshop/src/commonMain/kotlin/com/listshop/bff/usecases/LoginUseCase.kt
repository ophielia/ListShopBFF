package com.listshop.bff.usecases

import com.listshop.bff.data.bff.BFFError
import com.listshop.bff.data.bff.BFFErrorSubtype
import com.listshop.bff.data.bff.BFFErrorType
import com.listshop.bff.data.bff.BFFResult
import com.listshop.bff.data.model.ListShoppingList
import com.listshop.bff.data.state.TransitionViewState
import com.listshop.bff.exceptions.AuthenticationException
import com.listshop.bff.services.ListService
import com.listshop.bff.services.UserService
import com.listshop.bff.services.UserSessionService

class LoginUseCase(
    private val userName: String,
    private val password: String,
    private val userService: UserService,
    private val sessionService: UserSessionService,
    private val listService: ListService
) {

    suspend fun process(): BFFResult<TransitionViewState> {
         //MM nfl - skipping checking api compatibility for now
        //        - skipping first seen (will need userLastSeen)
        try {
            userService.signInUser(userName, password)
            val listOfLists = listService.retrieveListOfLists()
            val wrappedLists = ListShoppingList(listOfLists)
            return BFFResult.success(TransitionViewState.ListManagementScreen(wrappedLists))
        } catch (exception: Exception) {
            if (exception is AuthenticationException) {
                // construct result with failure
                val bfferror = BFFError(BFFErrorType.AUTHENTICATION, BFFErrorSubtype.CANT_LOGIN, exception.message ?: "no info")
                return BFFResult.error<TransitionViewState>(  bfferror)
            }
           val generalError = BFFError.errorFromException<TransitionViewState>( exception)
            return generalError
        }

    }

}