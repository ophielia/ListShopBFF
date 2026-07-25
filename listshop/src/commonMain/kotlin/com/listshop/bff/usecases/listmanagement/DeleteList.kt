package com.listshop.bff.usecases.listmanagement

import com.listshop.analytics.AnalyticsHandle
import com.listshop.analytics.debug
import com.listshop.analytics.error
import com.listshop.bff.data.bff.BFFError
import com.listshop.bff.data.bff.BFFResult
import com.listshop.bff.data.model.ListShoppingList
import com.listshop.bff.data.state.ConnectionStatus
import com.listshop.bff.services.ListService
import com.listshop.bff.services.UserService
import com.listshop.bff.usecases.validators.ConnectionStatusValidator

class DeleteList(
    private val connectionStatus: ConnectionStatus,
    private val listIdToDelete: String,
    private val listService: ListService,
    private val analyticsHandle: AnalyticsHandle
) : ConnectionStatusValidator {
    suspend fun process(): BFFResult<ListShoppingList> {
        analyticsHandle.debug("DeleteList - begin use case")
        try {
            checkOnlineStatus(connectionStatus)
            listService.deleteList(listIdToDelete)
            val lists = listService.retrieveListOfLists()
            val listOfLists = ListShoppingList(lists)
            analyticsHandle.debug("DeleteList - end use case")
            return BFFResult.success(value = listOfLists)

        } catch (e: Exception) {
            analyticsHandle.error("Error in DeleteList call")
            return BFFError.errorFromException(e)
        }
    }

    /*
        typealias DeleteListUseCaseResult = Swift.Result<[ShoppingList], ListShopError>

    let listService: ListService
    let onComplete: (DeleteListUseCaseResult) -> Void
    let onStart: () -> Void

    let listId: Int


    init(listId: Int,
         listService: ListService,
         onStart: (() -> Void)?,
         onComplete: ((DeleteListUseCaseResult) -> Void)?) {
        self.listId = listId
        self.listService = listService
        self.onStart = onStart ?? {
        }
        self.onComplete = onComplete ?? { result in
        }
    }

    public func start() {
        os_log("DeleteListUseCase - begin use case", log: Log.usecase, type: .info)
        self.onStart()

        // api call to add item
        firstly {
            try listService.deleteList(listId: listId)
        }
                .done({ shoppingLists in
                    os_log("DeleteListUseCase - successfully completed for server", log: Log.usecase, type: .info)
                    self.onComplete(.success(shoppingLists))
                })
                .catch { error in
                    if let lse = error as? ListShopError {
                        os_log("DeleteListUseCase - failed for server: %s", log: Log.usecase, type: .info, lse.message)
                    } else {
                        os_log("DeleteListUseCase - failed for server: %s", log: Log.usecase, type: .info, error.localizedDescription)
                    }
                    let lse = ListShopError.signInError
                    self.onComplete(.failure(lse))
                }
    }


     */
}

