package com.listshop.bff.services

import com.listshop.bff.data.model.Dish
import com.listshop.bff.data.model.DishSearchParameters
import com.listshop.bff.data.model.ShoppingList
import com.listshop.bff.data.state.ConnectionStatus

interface DishService {

    suspend fun retrieveDishList(searchParameters: DishSearchParameters): List<Dish>
}
