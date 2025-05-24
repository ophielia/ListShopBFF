package com.listshop.analytics

class ListShopAnalytics internal constructor() {

    fun displayingTags(size: Int) {
        sendEvent("viewUpdatingWithTags", "size" to size)
    }

    fun displayingError(message: NotFetchedReason) {
        sendEvent("viewDisplayingError", "message" to message)
    }

    fun refreshingTags() {
        sendEvent("refreshingTags")
    }

    fun updatingTagsError(throwable: Throwable) {
        sendEvent("errorDownloadingTagList", "throwable" to throwable)
    }

    fun clearingTagViewModel() {
        sendEvent("clearingTagViewModel")
    }

    fun fetchingTagsFromNetwork() {
        sendEvent("tagsFetching")
    }

    fun tagsFetchedFromNetwork(size: Int) {
        sendEvent("tagsFetched", "size" to size)
    }

    enum class NotFetchedReason{
        NotStale, NetworkError, RandomFail
    }

    fun tagsNotFetchedFromNetwork(reason: NotFetchedReason) {
        sendEvent("tagsNotFetchedFromNetwork", "reason" to reason)
    }

    fun insertingTagsToDatabase(size: Int) {
        sendEvent("insertingTagsToDatabase", "size" to size)
    }

    fun databaseCleared() {
        sendEvent("tagDatabaseCleared")
    }

    fun debug(text: String) {
        sendEvent(text)
    }
}