package com.listshop.bff.repositories

import com.listshop.bff.data.remote.ApiLayout

interface LayoutRepository {
    fun saveLayoutLocally(layout: ApiLayout)
    fun clearLayoutDataLocally()



}
