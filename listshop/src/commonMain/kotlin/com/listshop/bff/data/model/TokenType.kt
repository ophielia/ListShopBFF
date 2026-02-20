package com.listshop.bff.data.model

import kotlin.text.get

enum class TokenType(val display: String) {
    PASSWORD_RESET("PasswordReset");

    companion object {
        private val map = entries.associateBy(TokenType::display)
        fun fromDisplay(display: String) = map[display]
    }
}

