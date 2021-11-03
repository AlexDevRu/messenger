package com.example.chat.ui.validation

import com.example.chat.R
import com.example.chat.ui.models.StringResWrapper


sealed class InputValidator {
    data class LessCharactersValidator(val minCharacters: Int): InputValidator() {
        override fun validate(input: String) = input.isNotEmpty() && input.length >= minCharacters

        override fun getMessage(vararg args: Any) = StringResWrapper(R.string.small_length_validation, arrayOf(args[0].toString(), minCharacters))
    }

    object NoNumbersValidator : InputValidator() {
        override fun validate(input: String) = Regex("\\d").containsMatchIn(input)

        override fun getMessage(vararg args: Any) = StringResWrapper(R.string.password_no_numbers_validation)
    }

    object SameCaseValidator : InputValidator() {
        override fun validate(input: String) = Regex("[a-z][A-Z]|[A-Z][a-z]").containsMatchIn(input)

        override fun getMessage(vararg args: Any) = StringResWrapper(R.string.password_same_case_validation)
    }

    object EmailValidator: InputValidator() {
        private val emailRegex = Regex("(?:[a-z0-9!#\$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#\$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9]))\\.){3}(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9])|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)])")
        override fun validate(input: String) = emailRegex.matches(input)

        override fun getMessage(vararg args: Any) = StringResWrapper(R.string.email_validation)
    }

    abstract fun validate(input: String): Boolean
    abstract fun getMessage(vararg args: Any): StringResWrapper
}