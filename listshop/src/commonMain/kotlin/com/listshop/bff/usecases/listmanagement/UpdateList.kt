package com.listshop.bff.usecases.listmanagement

import com.listshop.analytics.AnalyticsHandle
import com.listshop.analytics.debug
import com.listshop.analytics.error
import com.listshop.bff.data.bff.BFFError
import com.listshop.bff.data.bff.BFFResult
import com.listshop.bff.services.UserService

class UpdateList(
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

        typealias UpdateListPropertiesUseCaseResult = Swift.Result<[ShoppingList], ListShopError>

    let listService: ListService
    let onComplete: (UpdateListPropertiesUseCaseResult) -> Void
    let onStart: () -> Void

    let listId: Int
    let listName: String
    let isStarterList: Bool

    init(listId: Int,
         listName: String,
         isStarterList: Bool,
         listService: ListService,
         onStart: (() -> Void)?,
         onComplete: ((UpdateListPropertiesUseCaseResult) -> Void)?) {
        self.listService = listService
        self.listId = listId
        self.listName = listName
        self.isStarterList = isStarterList
        self.onStart = onStart ?? {
        }
        self.onComplete = onComplete ?? { result in
        }
    }

    public func start() {
        os_log("UpdateListPropertiesUseCase - begin use case", log: Log.usecase, type: .info)
        self.onStart()
        let _ = firstly {
            try listService.updateListProperties(listId: listId, listName: listName, isStarterList: isStarterList )
        }.then({ [weak self] _ in
                    try (self?.listService.retrieveListOfLists()) ?? Promise.value([])
                })
                .done({ shoppingLists in
                    os_log("UpdateListPropertiesUseCase - successfully completed for server", log: Log.usecase, type: .info)
                    self.onComplete(.success(shoppingLists))
                })
    }



     */

}

