package com.listshop.bff.data.state

import com.listshop.bff.data.model.DishList
import com.listshop.bff.data.model.ListShoppingList
import com.listshop.bff.data.model.ShoppingList

sealed class TransitionViewState {

    object Launching : TransitionViewState()
    data class ListManagementScreen(val shoppingLists: ListShoppingList) : TransitionViewState()
    data class DishManagementScreen(val dishes: DishList) : TransitionViewState()
    data class ListScreen(val shoppingList: ShoppingList, val shoppingLists: ListShoppingList) : TransitionViewState()
    data class Onboarding(val state: OnboardingViewState) : TransitionViewState()
    data class Dashboard(val state: DashboardViewState) : TransitionViewState()
    object Guides : TransitionViewState()
}
