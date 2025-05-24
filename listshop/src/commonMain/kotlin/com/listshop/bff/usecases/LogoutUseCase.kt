package com.listshop.bff.usecases

import com.listshop.bff.data.bff.BFFResult
import com.listshop.bff.data.state.OnboardingViewState
import com.listshop.bff.data.state.TransitionViewState
import com.listshop.bff.services.UserService

class LogoutUseCase(
    private val userService: UserService,
) {
    suspend fun process(): BFFResult<TransitionViewState> {
        userService.logoutUser()

        val goal = TransitionViewState.Onboarding(OnboardingViewState.Choose)
        return BFFResult.success(goal)
    }

}