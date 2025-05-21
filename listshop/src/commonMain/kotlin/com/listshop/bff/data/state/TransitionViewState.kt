package com.listshop.bff.data.state

import com.listshop.bff.data.model.ListShoppingList

sealed class TransitionViewState {

    object Launching : TransitionViewState()
    data class ListManagementScreen(val shoppingLists: ListShoppingList) : TransitionViewState()
    data class Onboarding(val state: OnboardingViewState) : TransitionViewState()
    data class Dashboard(val state: DashboardViewState) : TransitionViewState()
}