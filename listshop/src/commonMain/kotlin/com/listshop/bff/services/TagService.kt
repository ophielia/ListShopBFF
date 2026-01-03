package com.listshop.bff.services

interface TagService {
    suspend fun retrieveTagsAndSaveLocally()
    suspend fun buildTagTree()


}
