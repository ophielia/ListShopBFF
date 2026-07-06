package com.listshop.bff.services

import com.listshop.bff.data.remote.ApiTag

interface LayoutService {
    suspend fun retrieveLayoutsAndSaveLocally()
    suspend fun clearUserLayouts()
    suspend fun updateLayoutInformationForTag(newTag: ApiTag)
}
