package com.listshop.bff.data.model

data class DishSearchParameters(
    val searchFragment: String? = null,
    var currentFilterList: List<String> = emptyList(),
    var sortKey: DishSortKey? = DishSortKey.CreatedOn,
    var sortDirection: SortDirection? = SortDirection.Descending
)
