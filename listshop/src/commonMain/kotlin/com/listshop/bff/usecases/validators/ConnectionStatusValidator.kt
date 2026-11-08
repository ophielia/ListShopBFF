package com.listshop.bff.usecases.validators

import com.listshop.bff.data.state.ConnectionStatus
import com.listshop.bff.exceptions.OfflineException

interface ConnectionStatusValidator {
    fun checkOnlineStatus(connectionStatus: ConnectionStatus) {
        if (connectionStatus != ConnectionStatus.Online) {
            throw OfflineException("Action cannot be done while offline")
        }
    }


}

