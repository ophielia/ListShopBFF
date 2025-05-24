package com.listshop.bff.services

import com.listshop.bff.data.model.ShoppingList

interface ListService {

    suspend fun retrieveListOfLists(): List<ShoppingList>
}