package com.listshop.bff.usecases.listmanagement

import com.listshop.analytics.AnalyticsHandle
import com.listshop.analytics.debug
import com.listshop.analytics.error
import com.listshop.bff.data.bff.BFFError
import com.listshop.bff.data.bff.BFFResult
import com.listshop.bff.services.UserService

class AddList(
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

    /**
    typealias AddListUseCaseResult = Swift.Result<[ShoppingList], ListShopError>

    let listService: ListService
    let onComplete: (AddListUseCaseResult) -> Void
    let onStart: () -> Void


    init(listService: ListService,
    onStart: (() -> Void)?,
    onComplete: ((AddListUseCaseResult) -> Void)?) {
    self.listService = listService
    self.onStart = onStart ?? {
    }
    self.onComplete = onComplete ?? { result in
    }
    }

    public func start() {
    os_log("AddListUseCase - begin use case", log: Log.usecase, type: .info)
    self.onStart()
    let _ = firstly {
    listService.createServerList()
    }.then({ [weak self] _ in
    try (self?.listService.retrieveListOfLists()) ?? Promise.value([])
    })
    .done({ shoppingLists in
    os_log("AddListUseCase - successfully completed for server", log: Log.usecase, type: .info)
    self.onComplete(.success(shoppingLists ))
    })
    }
     */

}

