package com.listshop.bff.data.model

import com.listshop.bff.db.ListInfoEntity

data class ListInfo(
    var lastInternalUpdate : String?,
    var lastUpdate : String?,
    var localListUpdated : String?,
    var serverListId : String?,
    var lookupDataLastSynced : String?,
    var statisticsLastSynced : String?,
    var localLastSynced : String?,
    var serverListLastSynced : String?

) {
    companion object Factory {
        fun create(dbValue: ListInfoEntity): ListInfo {
            return ListInfo(
                lastInternalUpdate = dbValue.lastInternalUpdate ,
                lastUpdate = dbValue.lastUpdate ,
                localListUpdated = dbValue.localListUpdated ,
                serverListId = dbValue.serverListId ,
                lookupDataLastSynced = dbValue.lookupDataLastSynced ,
                statisticsLastSynced = dbValue.statisticsLastSynced ,
                localLastSynced = dbValue.localLastSynced ,
                serverListLastSynced = dbValue.serverListLastSynced
            )
        }

    }

}

