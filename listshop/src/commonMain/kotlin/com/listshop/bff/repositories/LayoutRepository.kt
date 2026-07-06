package com.listshop.bff.repositories

import com.listshop.bff.data.remote.ApiLayout
import com.listshop.bff.data.remote.ApiLayoutCategory
import com.listshop.bff.data.remote.ApiTag

interface LayoutRepository {
    fun saveLayoutLocally(layout: ApiLayout)
    fun clearLayoutDataLocally()
    fun saveCategoryMappingLocally(tag: ApiTag, category: ApiLayoutCategory)


}
