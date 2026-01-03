package com.listshop.bff.data.model

import kotlin.text.get

enum class TagType(val display: String) {
    INGREDIENT("Ingredient"),
    DISH_TYPE(display = "DishType"),
    RATING(display = "Rating"),
    TAG_TYPE(display = "TagType"),
    NON_EDIBLE(display = "NonEdible");

    companion object {
        private val map = TagType.entries.associateBy(TagType::display)
        fun fromDisplay(display: String) = map[display]
    }
}

