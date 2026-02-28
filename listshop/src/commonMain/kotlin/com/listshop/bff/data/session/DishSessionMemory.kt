package com.listshop.bff.data.session

import com.listshop.bff.data.model.DishSearchParameters
import io.ktor.util.Digest

data class DishSessionMemory(
    val searchParameters: DishSearchParameters = DishSearchParameters()
)
