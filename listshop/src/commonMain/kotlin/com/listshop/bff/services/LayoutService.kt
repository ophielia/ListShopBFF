package com.listshop.bff.services

interface LayoutService {
    suspend fun retrieveLayoutsAndSaveLocally()
    suspend fun clearUserLayouts()
}
