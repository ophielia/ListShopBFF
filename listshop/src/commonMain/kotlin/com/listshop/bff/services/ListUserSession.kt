package com.listshop.bff.services

data class ListSession(
    var lastInternalUpdate: String?,
    //var lastUpdate : String?,
    var localListUpdated: String?,
    var serverListId: String?,
    var lookupDataLastSynced: String?,
    //var statisticsLastSynced : String?,
    var localLastSynced: String?,
    var serverListLastSynced: String?
) {


}
