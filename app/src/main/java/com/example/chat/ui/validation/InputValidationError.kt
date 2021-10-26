package com.example.chat.ui.validation

sealed class InputValidationError {
    data class LessCharactersError(val minCharacters: Int): InputValidationError() {
        override fun validate(input: String) = input.isNotEmpty() && input.length >= minCharacters
    }

    object NoNumbersError : InputValidationError() {
        override fun validate(input: String) = Regex("\\d").containsMatchIn(input)
    }

    object SameCaseError : InputValidationError() {
        override fun validate(input: String) = Regex("[a-z][A-Z]|[A-Z][a-z]").containsMatchIn(input)
    }

    abstract fun validate(input: String): Boolean
}