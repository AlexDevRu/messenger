package com.example.chat.ui.phone

import android.app.Activity
import com.example.chat.ui.base.UiEffect
import com.example.chat.ui.base.UiEvent
import com.example.chat.ui.base.UiState
import com.example.chat.ui.validation.InputValidator

object PhoneContract {
    sealed class Event: UiEvent {
        data class OnPhoneChanged(val phone: String): Event()
        data class OnSmsCodeChanged(val smsCode: String): Event()
        object OnSmsCodeValidate: Event()
        data class OnPhoneNumberVerify(val activity: Activity): Event()
    }

    data class State(
        val phone: String,
        val phoneValidationError: List<InputValidator>?,
        val smsCode: String,
        val smsIsSending: Boolean,
        val applyChangedInProgress: Boolean,
    ): UiState

    sealed class Effect: UiEffect {
        object SmsSended: Effect()
        object PhoneSavedSuccessfully: Effect()
        object TimeoutFinished: Effect()
        data class PhoneSaveFailure(val message: String?): Effect()
    }
}