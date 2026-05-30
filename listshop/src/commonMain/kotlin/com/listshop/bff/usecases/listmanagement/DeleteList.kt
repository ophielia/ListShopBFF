package com.listshop.bff.usecases.listmanagement

import com.listshop.analytics.AnalyticsHandle
import com.listshop.analytics.debug
import com.listshop.analytics.error
import com.listshop.bff.data.bff.BFFError
import com.listshop.bff.data.bff.BFFResult
import com.listshop.bff.services.UserService

class DeleteList(
    private val originalPassword: String,
    private val newPassword: String,
    private val userService: UserService,
    private val analyticsHandle: AnalyticsHandle
) {
    suspend fun process(): BFFResult<Unit> {
        analyticsHandle.debug("ChangePassword - begin use case")
        try {
            val userNameTaken = userService.changePassword(originalPassword, newPassword)
            analyticsHandle.debug("ChangePassword - end use case")
            return BFFResult.success(value = userNameTaken)
        } catch (e: Exception) {
            analyticsHandle.error("Error in CheckUserNameTaken call")
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

