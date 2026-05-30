package com.listshop.bff.usecases.listmanagement

import com.listshop.analytics.AnalyticsHandle
import com.listshop.analytics.debug
import com.listshop.analytics.error
import com.listshop.bff.data.bff.BFFError
import com.listshop.bff.data.bff.BFFResult
import com.listshop.bff.services.UserService

class SelectListForEdit(
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

        typealias SelectListForEditResult = Swift.Result<ShoppingList, ListShopError>

    let listId: Int
    let listService: ListService
    let onComplete: (SelectListForEditResult) -> Void
    let onStart: () -> Void

    init(listId: Int,
         listService: ListService,
         onStart: (() -> Void)?,
         onComplete: ((SelectListForEditResult) -> Void)?) {
        self.listId = listId
        self.listService = listService
        self.onStart = onStart ?? {
        }
        self.onComplete = onComplete ?? { result in
        }
    }

    public func start() {
        os_log("SelectListForEdit - begin use case", log: Log.usecase, type: .info)
        self.onStart()
        let id32 = Int32(self.listId)
        // call list service to get list
        let _ = firstly {
             listService.retrieveServerList(listId: id32)
        }.done { (shoppingList: ShoppingList) in
            self.onComplete(.success(shoppingList))
        }

    }



     */
}

