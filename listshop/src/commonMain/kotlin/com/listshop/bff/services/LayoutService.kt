package com.listshop.bff.services

import com.listshop.bff.data.model.ShoppingList
import com.listshop.bff.data.state.ConnectionStatus

interface LayoutService {
    fun retrieveLayoutsAndSaveLocally()


}
